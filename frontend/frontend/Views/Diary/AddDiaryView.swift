//
//  AddDiaryView.swift
//  frontend
//
//  Created by Katherine JANG on 5/20/23.
//

import SwiftUI
import PhotosUI

struct AddDiaryView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var path: PathViewModel
    @ObservedObject var diaryManager = DiaryViewModel()
    @State var selectedColor: Color = Color.clear
    @State var selectedImage: PhotosPickerItem? = nil
    @State var imageData: Data? = nil
    @State var diaryInfo = DiaryInfoModel()

    @State private var isCompleteClicked: Bool = false
    private var isTitleLengthValid: Bool { (0...Rules.diaryTitleMaxLength).contains(diaryInfo.title.count) }
    private var isTitleBlank: Bool { diaryInfo.title.isBlank() }
    private var isGroupNameLengthValid: Bool { (0...Rules.groupNameMaxLength).contains(diaryInfo.groupName?.count ?? 0) }
    private var isAllValid: Bool { isTitleLengthValid && isGroupNameLengthValid }

    @State private var isBlankTitleAlertPresented: Bool = false

    var isOnboarding = false
    var onboardingDone: () -> Void = { }

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack {
                DiaryCoverView(diaryInfo: $diaryInfo, coverImage: $imageData, onManipulate: true)
                    .frame(width: 200, height: 300)
                    .padding(.bottom, 30)
                VStack(alignment: .leading) {
                    TextField(Titles.diaryTitle, text: $diaryInfo.title)
                        .textFieldStyle(CustomTextFieldStyle(isValid: isTitleLengthValid))
                        .serif(18)
                        .onChange(of: diaryInfo.title) { newValue in
                            if !isTitleLengthValid {
                                diaryInfo.title = String(newValue.prefix(Rules.diaryTitleMaxLength))
                            }
                        }
                    Text("\(diaryInfo.title.count) / \(Rules.diaryTitleMaxLength)")
                        .foregroundStyle(Color.defaultGray)
                        .font(.caption)
                }
                .padding(.vertical, 16)
                VStack(alignment: .leading) {
                    TextField(Titles.groupName, text: $diaryInfo.groupName.nonOptional())
                        .textFieldStyle(CustomTextFieldStyle(isValid: isGroupNameLengthValid))
                        .serif(18)
                        .onChange(of: diaryInfo.groupName ?? "") { newValue in
                            if !isGroupNameLengthValid {
                                diaryInfo.groupName = String(newValue.prefix(Rules.groupNameMaxLength))
                            }
                        }
                    Text("\(diaryInfo.groupName?.count ?? 0) / \(Rules.groupNameMaxLength)")
                        .foregroundStyle(Color.defaultGray)
                        .font(.caption)
                }
                .padding(.vertical, 16)
                .padding(.bottom, 20)
                HStack {
                    Text(Titles.selectCover)
                        .regular()
                    Spacer()
                }
                .padding(.leading, 10)
                selectCover
                    .padding(.bottom, 30)
            }
            .padding(.horizontal, 20)
        }
        .scrollDismissesKeyboard(.automatic)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    guard !isTitleBlank else {
                        isBlankTitleAlertPresented = true
                        return
                    }
                    isCompleteClicked = true
                    completeButtonHandler()
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
        .alert(
            Titles.blankTitleAlertTitle,
            isPresented: $isBlankTitleAlertPresented
        ) {
            Button {
                isBlankTitleAlertPresented = false
            } label: {
                Text(Titles.confirm)
            }
        } message: {
            Text(Titles.blankTitleAlertDescription)
        }
        .onAppear {
            if diaryInfo.diaryId == nil {
                self.selectedColor = defaultColors[0]
            } else if diaryInfo.coverType == .color {
                self.selectedColor = Color(hexWithAlpha: diaryInfo.coverData) ?? Color.clear
            }
        }
    }

    var selectCover: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 20)
                .foregroundColor(Color.coverSelectGray)
            Grid {
                GridRow {
                    ForEach(0..<6) { idx in
                        Button {
                            onCoverColorChanged(defaultColors[idx])
                        } label: {
                            Circle()
                        }
                        .buttonStyle(ColorPaletteButton(color: defaultColors[idx], isSelected: selectedColor == defaultColors[idx]))
                    }
                }
                GridRow{
                    ForEach(6..<10) { idx in
                        Button {
                            onCoverColorChanged(defaultColors[idx])
                        } label: {
                            Circle()
                        }
                        .buttonStyle(ColorPaletteButton(color: defaultColors[idx], isSelected: selectedColor == defaultColors[idx]))
                    }
                    ColorPicker("", selection: $selectedColor.onChange(coverColorChanged))
                        .labelsHidden()
                        .scaleEffect(CGSize(width: 1.5, height: 1.5))
                        .padding(.horizontal, 8)
                    PhotosPicker(
                        selection: $selectedImage,
                        matching: .images,
                        photoLibrary: .shared()
                    ) {
                        Image(systemName: "photo.on.rectangle")
                            .foregroundColor(.black)
                            .background {
                                Circle()
                                    .frame(width: 44, height: 44)
                                    .foregroundColor(.white)
                            }
                    }
                    .onChange(of: selectedImage) { item in
                        Task {
                            diaryInfo.coverType = .image
                            diaryInfo.coverData = getImagePath(selectedImage?.itemIdentifier ?? "profileImage", .diary)
                            if let data = try? await item?.loadTransferable(type: Data.self) {
                                imageData = data
                            }
                        }
                    }
                }
            }
            .padding()
        }
    }

    // colorpicker용
    func coverColorChanged(to value: Color) {
        self.diaryInfo.coverType = .color
        self.diaryInfo.coverData = selectedColor.uiColor.hexStringWithOpacity ?? "#FFFFFFFF"
    }

    func onCoverColorChanged(_ color: Color) {
        self.selectedColor = color
        self.diaryInfo.coverType = .color
        self.diaryInfo.coverData = selectedColor.uiColor.hexStringWithOpacity ?? "#FFFFFFFF"
    }

    // MARK: 완료 버튼 핸들러
    private func completeButtonHandler() {
        Task {
            if diaryInfo.groupName == "" {
                diaryInfo.groupName = nil
            }
            do {
                if diaryInfo.diaryId == nil {
                    // 일기장 생성
                    try await DiaryListManager.shared.createNewDiary(newDiary: diaryInfo, image: imageData)
                } else {
                    // 일기장 편집
                    try await diaryManager.editDiary(diaryInfo, imageData: imageData)
                }
                if isOnboarding {
                    dismiss()
                    onboardingDone()
                } else {
                    path.goBack()
                }
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
                isCompleteClicked = false
            }
        }
    }
}

struct ColorPaletteButton: ButtonStyle {
    let color: Color
    let isSelected: Bool

    func makeBody(configuration: Configuration) -> some View {
        ZStack(alignment: .center){
            configuration.label
                .foregroundColor(color)
                .background(Circle().stroke(isSelected ? Color.checkBlue : .clear, lineWidth: 4))
                .frame(width: 44, height: 44)
            if isSelected {
                Image("CheckIcon")
            }
        }
    }
}
