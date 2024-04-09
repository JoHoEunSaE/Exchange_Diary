//
//  DiarySelectionView.swift
//  frontend
//
//  Created by 김나연 on 1/9/24.
//

import Foundation
import SwiftUI

struct DiarySelectionView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject var noteCreateManager = CreateNoteViewModel.shared
    @ObservedObject var diaryListManager = DiaryListManager.shared
    let onDiaryTap: () -> Void

    private let columns = [GridItem(.flexible()), GridItem(.flexible())]
    private let defaultPadding = 20.0
    private let titlePadding = 30.0
    private let gridPadding = 10.0

    var body: some View {
        VStack {
            Text(Titles.selectedDiaryPlaceholder)
                .largeBold()
                .padding(.top, titlePadding)
                if diaryListManager.diaryList.isEmpty {
                    VStack(spacing: 30) {
                        Text(Titles.emptyDiaryListContent)
                            .multilineTextAlignment(.center)
                            .foregroundStyle(Color.defaultGray)
                        CommonButton(Titles.createDiaryRecommendButton) {
                            noteCreateManager.isCreateNotePresented = false
                        }
                        Spacer()
                    }
                    .padding(defaultPadding)
                } else {
                    ScrollView(showsIndicators: false) {
                    LazyVGrid(columns: columns) {
                        ForEach(diaryListManager.diaryList, id: \.self.diaryId) { diary in
                            DiaryCoverView(diaryInfo: .constant(diary), onManipulate: false) // TODO: constant 고쳐야됨
                                .border(diary.diaryId == diaryListManager.selectedDiary?.diaryId ? Color.defaultBlue : .clear, width: 1)
                                .frame(width: 140, height: 210)
                                .padding(.vertical, gridPadding)
                                .onTapGesture {
                                    diaryListManager.selectDiary(diary)
                                    onDiaryTap()
                                }
                        }
                    }
                    .padding(EdgeInsets(top: 0, leading: defaultPadding, bottom: defaultPadding, trailing: defaultPadding))
                }
            }
        }
    }
}
