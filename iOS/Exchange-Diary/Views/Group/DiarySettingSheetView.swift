//
//  DiarySettingSheetView.swift
//  frontend
//
//  Created by Katherine JANG on 11/29/23.
//

import SwiftUI

enum diarySettingOption {
    case quit
    case edit
    case delete
    case delegate
    case setting
}

struct DiarySettingSheetView: View {
    @ObservedObject var diaryManager: DiaryViewModel
    @State var leaveAlertPresented: Bool = false
    @State var changeMasterAlertPresented: Bool = false
    @State var navigateChangeMaster: Bool = false
    @Binding var isSettingSheetPresented: Bool
    @Binding var selectedDetent: PresentationDetent
    @EnvironmentObject var path: PathViewModel
    
    var body: some View {
        NavigationStack {
            VStack {
                List {
                    Section {
                        NavigationLink(
                            "초대코드 복사",
                            destination: InviteSheetView(diaryManager: diaryManager).navigationTitle("초대코드 복사")
                        )
                    }
                    if diaryManager.isMaster {
                        Section {
                            Button(Titles.edit) {
                                self.isSettingSheetPresented = false
                                path.navigateTo(.addDiary, diaryManager.diaryInfo)
                            }
                            NavigationLink(
                                "대표 변경",
                                destination: DelegateMasterView(diaryManager: diaryManager, isLeaveDiary: false)
                            )
                        }
                    }
                    Section {
                        Button(role: .destructive, action: {
                            leaveAlertPresented = true
                        }) {
                            Text(Titles.discard)
                        }
                        if diaryManager.isMaster {
                            NavigationLink(
                                "일기장 삭제",
                                destination: DeleteDiarySheetView(
                                    diaryManager: diaryManager,
                                    settingSheetPresented: $isSettingSheetPresented,
                                    selectedDetent: $selectedDetent
                                )
                                .navigationTitle("일기장 삭제")
                            )
                            .foregroundStyle(Color.red)
                        }
                    }
                    .navigationDestination(isPresented: $navigateChangeMaster){
                        DelegateMasterView(diaryManager: diaryManager, isLeaveDiary: true)
                    }
                }
                .alert("일기장 나가기", isPresented: $leaveAlertPresented) {
                    Button(Titles.cancel, role: .cancel, action: {})
                    Button(role: .destructive) {
                        self.leaveDiary()
                    } label: {
                        Text(diaryManager.isMaster ?  "변경하기" : "나가기")
                    }
                } message: {
                    Text(diaryManager.isMaster ? "대표를 변경해야 나갈 수 있습니다" : "정말로 나가시겠습니까?")
                }
            }
        }
    }
    
    private func leaveDiary() {
        if diaryManager.isMaster {
            navigateChangeMaster = true
        } else {
            Task{
                do {
                    if DiaryListManager.shared.selectedDiary == diaryManager.diaryInfo {
                        DiaryListManager.shared.selectedDiary = nil
                    }
                    try await diaryManager.leaveDiary()
                    path.goBack()
                } catch {
                    AlertManager.shared.setError(error as? ResponseErrorData)
                }
            }
        }
    }
}

#Preview {
    DiarySettingSheetView(
        diaryManager: DiaryViewModel(diaryId: 6),
        isSettingSheetPresented: .constant(true),
        selectedDetent: .constant(.medium)
    )
}

