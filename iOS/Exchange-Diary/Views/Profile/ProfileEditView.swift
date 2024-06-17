//
//  setProfileView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/05/20.
//

import SwiftUI
import Combine
import _PhotosUI_SwiftUI

// TODO: 툴바 안보이는 뷰로 변경하기.
struct ProfileEditView: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.dismiss) var dismiss

    @State private var editProfile: UploadProfile = UploadProfile(
        nickname: MyProfileManager.shared.myProfile.nickname,
        statement: MyProfileManager.shared.myProfile.statement,
        profileImageUrl: MyProfileManager.shared.myProfile.profileImageUrl
    )
    @State private var profileImageData: Data? = nil
    @State private var showPhotoOptionsSheet: Bool = false
    @State private var showPhotosPicker: Bool = false
    @State private var selectedImage: PhotosPickerItem? = nil
    @State private var statementLength: Int = MyProfileManager.shared.myProfile.statement.count
    @State private var detentHeight: CGFloat = 0

    private var isProfileUnchanged: Bool {
        MyProfileManager.shared.myProfile.nickname == editProfile.nickname &&
        MyProfileManager.shared.myProfile.statement == editProfile.statement &&
        MyProfileManager.shared.myProfile.profileImageUrl == editProfile.profileImageUrl
    }
    private var isLengthZero: Bool { editProfile.nickname.count == 0 }
    private var isLengthValid: Bool { isLengthZero || Rules.nicknameLengthRange.contains(editProfile.nickname.count) }
    private var isCharactersValid: Bool { isLengthZero || isValidCharacters(editProfile.nickname) }
    private var isAllValid: Bool { !isProfileUnchanged && !isLengthZero && isLengthValid && isCharactersValid }
    @State private var isCompleteClicked: Bool = false

    var body: some View {
        VStack {
            photoSelectionButton
            profileFields
            Spacer()
        }
        .padding(40)
        .sheet(isPresented: $showPhotoOptionsSheet, content: photoOptionsSheet)
        .photosPicker(isPresented: $showPhotosPicker, selection: $selectedImage, matching: .images, photoLibrary: .shared())
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            TopbarTitle(Titles.profileEdit)
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    isCompleteClicked = true
                    completeProfileEditing()
                } label: {
                    if isCompleteClicked {
                        ProgressView()
                    } else {
                        Text(Titles.complete)
                    }
                }
                .tint(.defaultBlue)
                .disabled(!isAllValid || isCompleteClicked)
            }
        }
    }

    // MARK: 사진 선택 버튼 View
    private var photoSelectionButton: some View {
        Button {
            showPhotoOptionsSheet = true
        } label: {
            ZStack {
                if profileImageData != nil {
                    Image(uiImage: UIImage(data: profileImageData ?? Data()) ?? UIImage(systemName: "person.circle.fill")!)
                        .resizable()
                        .frame(width: 120, height: 120)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.lightGray, lineWidth: 1))
                        .foregroundColor(.gray)
                } else {
                    LoadProfileImage(editProfile.profileImageUrl, size: 120)
                        .overlay(Circle().stroke(Color.lightGray, lineWidth: 1))
                }

                Image("editPenIcon")
                    .overlay(Circle().stroke(Color.defaultBackground, lineWidth: 2))
                    .offset(x: 42, y: 42)
            }
        }
        .onChange(of: selectedImage) { image in
            handlePhotoSelectionChange(image)
        }
        .padding(.top, 20)
        .padding(.bottom, 90)
    }

    // MARK: 프로필 필드 입력 View
    private var profileFields: some View {
        VStack(alignment: .leading) {
            TextField(Titles.userNamePlaceholder, text: $editProfile.nickname)
                .serif(16)
                .textFieldStyle(CustomTextFieldStyle(isValid: isLengthZero || isLengthValid && isCharactersValid))
            Text(Titles.userNameUnique)
                .font(.caption)
                .foregroundColor(.defaultGray)
            Text(Titles.userNameLength)
                .font(.caption)
                .foregroundColor(isLengthValid ? .defaultGray : .red)
            Text(Titles.userNameValidChars)
                .font(.caption)
                .foregroundColor(isCharactersValid ? .defaultGray : .red)
            TextField(Titles.statementPlaceholder, text: $editProfile.statement)
                .serif(16)
                .padding(.top, 20)
                .textFieldStyle(CustomTextFieldStyle())
                .onChange(of: editProfile.statement, perform: updateTextWithMaxLength)
            Text("\(statementLength) / \(Rules.statementMaxLength)")
                .foregroundColor(.defaultGray)
                .font(.caption)
        }
    }

    // MARK: 닉네임 유효성 검사
    func isValidCharacters(_ string: String) -> Bool {
        return string.range(of: Rules.nicknameRegex, options: .regularExpression) != nil
    }

    // MARK: 사진 선택 변경 처리
    private func handlePhotoSelectionChange(_ item: PhotosPickerItem?) {
        Task {
            guard let item = item, let data = try? await item.loadTransferable(type: Data.self) else {
                return
            }
            profileImageData = data
            editProfile.profileImageUrl = getImagePath(item.itemIdentifier ?? "profileImage", .profile)
        }
    }

    // MARK: 프로필 편집 완료
    private func completeProfileEditing() {
        Task {
            do {
                try await MyProfileManager.shared.editMyProfile(profile: editProfile, profileImage: profileImageData)
                await MainActor.run {
                    dismiss()
                }
            } catch {
                await AlertManager.shared.setError(error as? ResponseErrorData)
                isCompleteClicked = false
            }
        }
    }

    // MARK: 상태메시지 텍스트 최대 길이 조절
    private func updateTextWithMaxLength(newValue: String) {
        statementLength = newValue.count
        if newValue.count > Rules.statementMaxLength {
            editProfile.statement = String(newValue.prefix(Rules.statementMaxLength))
        }
    }
}


// MARK: 사진 옵션 시트
extension ProfileEditView {
    private func photoOptionsSheet() -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Button {
                showPhotoOptionsSheet = false
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    showPhotosPicker = true
                }
            } label: {
                HStack {
                    Image("photoIcon")
                        .foregroundColor(Color.accentColor)
                    Text(Titles.selectFromLibrary)
                        .foregroundColor(Color.accentColor)
                        .fontWeight(.semibold)
                    Spacer()
                }
            }
            .padding(.vertical, 5)

            Button(role: .destructive) {
                deleteImage()
            } label: {
                Image("trashIcon")
                    .foregroundColor(Color.defaultRed)
                Text(Titles.deleteCurrentPhoto)
                    .foregroundColor(Color.defaultRed)
                    .fontWeight(.semibold)
                Spacer()
            }
            .padding(.vertical, 5)
        }
        .padding(.horizontal, 20)
        .readHeight()
        .onPreferenceChange(HeightPreferenceKey.self) { height in
            if let height {
                self.detentHeight = height
            }
        }
        .presentationDetents([.height(self.detentHeight)])
        .presentationDragIndicator(.visible)
    }

    private func deleteImage() {
        selectedImage = nil
        profileImageData = nil
        editProfile.profileImageUrl = nil
        showPhotoOptionsSheet = false
    }
}

#Preview {
    ProfileEditView()
}
