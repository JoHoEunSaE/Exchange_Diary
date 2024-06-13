//
//  DeleteDiaryView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 12/29/23.
//

import SwiftUI

struct DeleteDiarySheetView: View {
    @EnvironmentObject var path: PathViewModel
    @Environment(\.dismiss) var dismiss
    @ObservedObject var diaryManager: DiaryViewModel
    @Binding var settingSheetPresented: Bool
    @Binding var selectedDetent: PresentationDetent
    
    var body: some View {
        GeometryReader { geometry in
            ScrollView {
                VStack(alignment: .leading, spacing: 40) {
                    VStack(alignment: .leading) {
                        Text(Titles.deleteDiaryTitle)
                            .extraLargeBold()
                            .padding(.vertical, 16)
                        Divider()
                    }
                    VStack(alignment: .leading, spacing: 10) {
                        Text(Titles.removeRecordsTitle)
                            .regularBold()
                        Text(Titles.removeRecordsContent)
                            .small()
                    }
                    VStack(alignment: .leading, spacing: 10) {
                        Text(Titles.privateNoteTitle)
                            .regularBold()
                        Text(Titles.privateNoteContent)
                            .small()
                    }
                    HStack {
                        backButton
                        deletionButton
                    }
                    .padding(.vertical, 20)
                    Spacer()
                }
            }
            .scrollIndicators(.hidden)
            .padding(.horizontal, 20)
            .frame(height: selectedDetent == .large ? geometry.size.height : nil)
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
                withAnimation {
                    selectedDetent = .large
                }
            }
        }
        .onDisappear {
            selectedDetent = .medium
        }
    }
    
    private var backButton: some View {
        CommonButton("잠시 더 생각해볼게요") {
            dismiss()
        }
        .pale()
        .small()
    }
    
    private var deletionButton: some View {
        CommonButton("삭제할게요") {
            Task {
                do {
                    if DiaryListManager.shared.selectedDiary == diaryManager.diaryInfo {
                        DiaryListManager.shared.selectedDiary = nil
                    }
                    self.settingSheetPresented  = false
                    try await diaryManager.deleteDiary()
                    path.goBack()
                } catch {
                    AlertManager.shared.setError(error as? ResponseErrorData)
                }
            }
        }
        .small()
    }
}

#Preview {
    DeleteDiarySheetView(diaryManager: DiaryViewModel(diaryId: 3), settingSheetPresented: .constant(true), selectedDetent: .constant(.medium))
}
