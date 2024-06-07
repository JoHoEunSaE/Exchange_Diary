//
//  OnboardingEditNicknameView.swift
//  frontend
//
//  Created by 신인호 on 1/4/24.
//

import SwiftUI

struct OnboardingEditNicknameView: View {
    @Binding var selectedTab: Int
    @State private var nickname = ""
    private var isLengthZero: Bool { nickname.count == 0 }
    private var isLengthValid: Bool { isLengthZero || Rules.nicknameLengthRange.contains(nickname.count)  }
    private var isCharactersValid: Bool { isLengthZero || isValidCharacters(nickname) }
    private var isAllValid: Bool { !isLengthZero && isLengthValid && isCharactersValid }
    
    var body: some View {
        VStack {
            VStack(spacing: 20) {
                Text(Titles.userNameMake)
                    .multilineTextAlignment(.center)
                    .minimumScaleFactor(0.1)
                    .serifBold(28)
                    .lineSpacing(10)
                Text(Titles.userNameUnique)
                    .smallBold()
                    .foregroundStyle(Color.defaultGray)
            }
            .padding(.vertical, 96)
            VStack(alignment: .leading) {
                TextField(Titles.userNamePlaceholder, text: $nickname)
                    .serif(16)
                    .textFieldStyle(CustomTextFieldStyle(isValid: isLengthZero || isAllValid))
                    .onSubmit {
                        if !nickname.isBlank() {
                            changeNicknameButton()
                        }
                    }
                VStack(alignment: .leading) {
                    Text(Titles.userNameLength)
                        .font(.caption)
                        .foregroundStyle(isLengthValid ? Color.defaultGray : .red)
                    Text(Titles.userNameValidChars)
                        .font(.caption)
                        .foregroundStyle(isCharactersValid ? Color.defaultGray : .red)
                }
            }
            .padding(.horizontal, 20)
            .frame(width: 350, alignment: .topLeading)
            Spacer()
            CommonButton(Titles.confirm, isDisabled: !isAllValid, action: changeNicknameButton)
        }
        .padding(20)
    }
    
    // MARK: 닉네임 유효성 검사
    func isValidCharacters(_ string: String) -> Bool {
        return string.range(of: Rules.nicknameRegex, options: .regularExpression) != nil
    }
    
    // MARK: 확인 버튼 기능
    func changeNicknameButton() {
        Task {
            do {
                try await MyProfileManager.shared.editMyProfile(
                    profile: UploadProfile(
                        nickname: nickname,
                        statement: "안녕하세요. \(nickname)입니다."
                    ),
                    profileImage: nil
                )
                withAnimation {
                    selectedTab = 1
                }
            } catch {
                await AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
}

#Preview {
    OnboardingEditNicknameView(selectedTab: .constant(0))
}
