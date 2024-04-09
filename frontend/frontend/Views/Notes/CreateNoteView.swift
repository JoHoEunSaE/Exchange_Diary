//
//  CreateNoteView.swift
//  frontend
//
//  Created by Katherine JANG on 6/26/23.
//

import SwiftUI
import PhotosUI
import Alamofire

struct CreateNoteView: View {
    enum FocusedField {
        case title
        case content
    }

    enum ConfirmAlert {
        case blankTitle
        case longTitle
        case longContent

        var title: String {
            switch self {
            case .blankTitle:
                return Titles.blankTitleAlertTitle
            case .longTitle, .longContent:
                return Titles.longAlertTitle
            }
        }

        var description: String {
            switch self {
            case .blankTitle:
                return Titles.blankTitleAlertDescription
            case .longTitle:
                return Titles.longTitleAlertDescription
            case .longContent:
                return Titles.longContentAlertDescription
            }
        }
    }

    @ObservedObject var diaryListManager = DiaryListManager.shared
    @ObservedObject var createNoteManager = CreateNoteViewModel.shared
    let dismiss: () -> Void

    @State var title: String = ""
    @State var content: String = ""
    @State var selectedImage: PhotosPickerItem?
    @State var imageData: Data?

    @State var isDiarySelectionPresented: Bool = false
    @State var isPhotosPickerPresented: Bool = false
    @State private var isCancelAlertPresented: Bool = false
    @State private var isConfirmAlertPresented: Bool = false
    @State private var confirmAlert: ConfirmAlert = .blankTitle
    @FocusState private var focusedField: FocusedField?
    @State private var imageInside = false

    @State private var isPostClicked: Bool = false
    private var isAllValid: Bool { !(diaryListManager.selectedDiary == nil || title.isEmpty || content.isEmpty) }
    private var isWriting: Bool { !title.isBlank() || !content.isEmpty || selectedImage != nil }
    private let wrapPadding = 20.0
    private let bottomItemPadding = EdgeInsets(top: 10.0, leading: 15.0, bottom: 10.0, trailing: 15.0)
    private let contentPadding = 40.0

    private let titleMaxLength = Rules.noteTitleMaxLength
    private let contentMaxLength = Rules.noteContentMaxLength

    init(dismiss: @escaping () -> Void = {}) {
        self.dismiss = dismiss
        createNoteManager.clearContent()
    }

    var body: some View {
        NavigationStack {
            VStack(alignment: .center, spacing: 0) {
                DiarySelectionButton {
                    isDiarySelectionPresented = true
                }
                GeometryReader { geo in
                    ScrollView {
                        TextField(Titles.noteTitlePlaceholder, text: $title)
                            .textFieldStyle(CustomTextFieldStyle())
                            .focused($focusedField, equals: .title)
                            .regular()
                            .padding(.horizontal, 3)
                            .onSubmit {
                                focusedField = .content
                            }
                        ZStack(alignment: .topLeading) {
                            TextEditorView(text: $content)
                                .frame(minHeight: geo.size.height - (contentPadding + 15.0), maxHeight: .infinity)
                                .focused($focusedField, equals: .content)
                            if content.isEmpty {
                                Text(Titles.noteContentPlaceholder)
                                    .regular()
                                    .foregroundStyle(.tertiary)
                                    .padding(.top, 8)
                                    .padding(.horizontal, 3)
                                    .allowsHitTesting(false)
                            }
                        }
                    }
                }
                Spacer(minLength: contentPadding)
            }
            .padding(wrapPadding)
            .navigationBarTitleDisplayMode(.inline)
            .overlay(alignment: .bottom) {
                VStack(alignment: .trailing, spacing: 0) {
                    if let imageData, let uiImage = UIImage(data: imageData) {
                        SelectedImageView(uiImage: uiImage)
                            .padding()
                            .offset(x: imageInside ? 50 : 0)
                            .onTapGesture {
                                withAnimation(.easeOut) {
                                    imageInside.toggle()
                                }
                            }
                    }
                    Divider()
                    bottomItem
                        .padding(bottomItemPadding)
                }
            }
            .toolbar {
                topLeadingItem
                topPrincipalItem
                topTrailingItem
            }
            .onChange(of: selectedImage) { newItem in
                guard let newItem = newItem else {
                    return
                }
                Task {
                    if let data = try? await newItem.loadTransferable(type: Data.self) {
                        imageData = data
                    }
                }
            }
            .navigationBarTitleDisplayMode(.inline)
        }
        .sheet(isPresented: $isDiarySelectionPresented) {
            DiarySelectionView() {
                isDiarySelectionPresented = false
            }
            .presentationDragIndicator(.visible)
            .presentationDetents([.medium, .large])
        }
        .alert(
            Titles.discardAlertTitle,
            isPresented: $isCancelAlertPresented
        ) {
            Button(role: .cancel) {
                isCancelAlertPresented = false
            } label: {
                Text(Titles.cancel)
            }
            Button(role: .destructive) {
                dismiss()
            } label: {
                Text(Titles.discard)
            }
        } message: {
            Text(Titles.discardAlertDescription)
        }
        .alert(
            confirmAlert.title,
            isPresented: $isConfirmAlertPresented
        ) {
            Button {
                isConfirmAlertPresented = false
            } label: {
                Text(Titles.confirm)
            }
        } message: {
            Text(confirmAlert.description)
        }
        .photosPicker(
            isPresented: $isPhotosPickerPresented,
            selection: $selectedImage,
            matching: .any(of: [.images])
        )
        .onAppear {
            guard diaryListManager.selectedDiary != nil else {
                isDiarySelectionPresented = true
                return
            }
            focusedField = .title
        }
    }
}

// MARK: Toolbar Items
extension CreateNoteView {
    var topLeadingItem: some ToolbarContent {
        ToolbarItem(placement: .topBarLeading) {
            Button {
                if isWriting {
                    isCancelAlertPresented = true
                } else {
                    dismiss()
                }
            } label: {
                Text(Titles.cancel)
                    .regular()
            }
        }
    }

    var topPrincipalItem: some ToolbarContent {
        TopbarTitle(Titles.createNoteTitle)
    }

    var topTrailingItem: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button {
                guard !title.isBlank() else {
                    confirmAlert = .blankTitle
                    isConfirmAlertPresented = true
                    return
                }

                guard title.count <= titleMaxLength else {
                    confirmAlert = .longTitle
                    isConfirmAlertPresented = true
                    return
                }

                guard content.count <= contentMaxLength else {
                    confirmAlert = .longContent
                    isConfirmAlertPresented = true
                    return
                }

                isPostClicked = true
                Task {
                    do {
                        try await createNoteManager.postNote(
                            title: title,
                            content: content,
                            image: selectedImage,
                            imageData: imageData
                        )
                        dismiss()
                    } catch {
                        AlertManager.shared.setError(error as? ResponseErrorData)
                        isPostClicked = false
                    }
                }
            } label: {
                if isPostClicked {
                    ProgressView()
                } else {
                    Text(Titles.post)
                }
            }
            .tint(.defaultBlue)
            .disabled(!isAllValid || isPostClicked)
        }
    }

    var bottomItem: some View {
        let text = focusedField == .content ? content : title
        let maxLength = focusedField == .content ? contentMaxLength : titleMaxLength

        return HStack {
            if selectedImage == nil {
                AddImageButton {
                    isPhotosPickerPresented = true
                }
            } else {
                RemoveImageButton {
                    selectedImage = nil
                    imageData = nil
                }
            }

            Spacer()
            
            if focusedField != nil {
                Text("\(text.count) / \(maxLength)자")
                    .foregroundStyle(Color.limitedTextColor(text: text, max: maxLength))
                    .font(.caption)
            }
            
            ToggleKeyboardButton(isKeyboardOn: focusedField != nil) {
                focusedField = focusedField == nil ? .content : nil
            }
            .padding(.leading)
        }
    }
}

// MARK: Buttons

struct DiarySelectionButton: View {
    let onButtonTap: () -> Void

    private let bottomPadding = 4.0

    var body: some View {
        Button {
            onButtonTap()
        } label: {
            HStack(spacing: 6) {
                if let selectedDiary = DiaryListManager.shared.selectedDiary {
                    if selectedDiary.coverType == .image {
                        LoadPreviewImage(selectedDiary.coverData)
                            .cornerRadius(4)
                            .frame(width: 24, height: 24)
                            .shadow(color: .black.opacity(0.08), radius: 20, x: 10, y: 10)
                    } else {
                        RoundedRectangle(cornerRadius: 4)
                            .frame(width: 24, height: 24)
                            .foregroundColor(Color(hexWithAlpha: selectedDiary.coverData))
                            .shadow(color: .black.opacity(0.08), radius: 20, x: 10, y: 10)
                    }
                    Text(selectedDiary.title)
                        .regularBold()
                } else {
                    Text(Titles.selectedDiaryPlaceholder)
                        .regular()
                        .foregroundStyle(Color.defaultGray)
                }
                Image(systemName: "chevron.down")
                    .small()
                    .tint(.defaultGray)
            }
        }
        .padding(.bottom)
    }
}

struct AddImageButton: View {
    let onButtonTap: () -> Void

    var body: some View {
        Button {
            onButtonTap()
        } label: {
            Image("photoIcon")
        }
    }
}

struct RemoveImageButton: View {
    let onButtonTap: () -> Void

    var body: some View {
        Button {
            onButtonTap()
        } label: {
            Image("trashIcon")
                .foregroundStyle(.red)
        }
    }
}

struct SelectedImageView: View {
    let uiImage: UIImage

    private let imageSize = 56.0

    var body: some View {
        Image(uiImage: uiImage)
            .resizable()
            .aspectRatio(contentMode: .fill)
            .frame(width: imageSize, height: imageSize)
            .clipped()
            .clipShape(RoundedRectangle(cornerRadius: 4))
            .shadow(color: .black.opacity(0.25), radius: 15, x: 4, y: 4)
    }
}

struct ToggleKeyboardButton: View {
    let isKeyboardOn: Bool
    let onButtonTap: () -> Void

    var body: some View {
        Button {
            onButtonTap()
        } label: {
            Group {
                if isKeyboardOn {
                    Image(systemName: "keyboard.chevron.compact.down")
                } else {
                    Image(systemName: "keyboard")
                        .font(.system(size: 16))
                }
            }
            .foregroundStyle(Color.defaultGray)
        }
    }
}
