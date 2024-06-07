
//
//  AddDiarySheetTmp.swift
//  frontend
//
//  Created by Katherine JANG on 1/24/24.
//

import SwiftUI

enum addDiaryStep {
    case enterCode
    case showPreview
    case `default`
}

struct AddDiarySheetView: View {
    @EnvironmentObject var path: PathViewModel
    
    @State var enteredCode: String = ""
    @State var diaryInfo = DiaryInfoModel()
    @State var isCodeWrong = false
    @State var addProcess = addDiaryStep.default
    @State var showAlert = false
    @Binding var isAddDiary:Bool
     
    var body: some View {
        switch addProcess {
        case .enterCode:
            enterCode
        case .showPreview:
            VStack(spacing: 16) {
                enterCode
                diaryPreview
            }
        case .default:
            VStack(alignment: .leading, spacing: 16) {
                createDiary
                joinWithCode
            }
        }
    }
    
    var createDiary: some View {
        Button {
            path.navigateTo(.addDiary)
            isAddDiary = false
        } label: {
            HStack {
                Image("plusIcon")
                Text(Titles.createNew)
                    .regularBold()
                Spacer()
            }
        }
    }
    
    var joinWithCode: some View {
        Button {
            self.addProcess = .enterCode
        } label: {
            HStack {
                Image("inviteIcon")
                Text(Titles.joinWithCode)
                    .regularBold()
                Spacer()
            }
        }
    }
    
    var enterCode: some View {
        VStack {
            Text(Titles.joinWithCode)
                .largeBold()
                .padding(.bottom, 16)
            HStack {
                TextField(Titles.codePlaceholder, text: $enteredCode)
                    .onChange(of: enteredCode) { inputText in
                        if inputText.isEmpty {
                            self.isCodeWrong = false
                        }
                    }
                    .onSubmit {
                        Task {
                            guard let preview = await DiaryListManager.shared.getDiaryInfoWithCode(enteredCode) else {
                                self.isCodeWrong = true
                                return
                            }
                            self.diaryInfo = preview
                            self.addProcess = .showPreview
                        }
                    }
                if !enteredCode.isEmpty {
                    emptyCode
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 42)
            .padding(EdgeInsets(top: 0, leading: 20, bottom: 0, trailing: 10))
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.reverseAccentColor)
                    .shadow(color:.black.opacity(0.1), radius: 12.5, x: 4, y: 4)
                    .frame(height: 42)
            )
            if isCodeWrong {
                wrongCode
            }
        }
    }
    
    var emptyCode: some View {
        Button {
            enteredCode = ""
        } label: {
            Image(systemName: "delete.left.fill")
                .foregroundColor(.clearButton)
                .frame(width: 32, height: 32)
        }
    }
    
    var wrongCode: some View {
        HStack {
            Text(Titles.wrongInvitationCode)
                .foregroundStyle(.red)
                .font(.caption)
                .lineSpacing(4)
            Spacer()
        }
        .padding(8)
    }
    
    var diaryPreview: some View {
        Button {
            self.showAlert = true
        } label: {
            DiaryCoverView(diaryInfo: $diaryInfo, onManipulate: false)
                .frame(width: 140, height: 210)
        }
        .alert(Titles.addDiary, isPresented: $showAlert, actions: {
            Button(Titles.cancel, role: .cancel, action: {})
            Button(Titles.confirm) {
                DiaryListManager.shared.joinDiary(enteredCode, diaryInfo.diaryId ?? 0)
                self.isAddDiary = false
            }
        }, message: {
            Text(Titles.addDiaryConfirm)
        })
        .padding(.vertical, 16)
    }
}
