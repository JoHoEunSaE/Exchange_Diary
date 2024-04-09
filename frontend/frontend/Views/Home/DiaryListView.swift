//
//  DiaryListView.swift
//  frontend
//
//  Created by Katherine JANG on 6/27/23.
//

import SwiftUI

struct DiaryListView: View {
    @EnvironmentObject var path: PathViewModel
    @StateObject var diaryListManager = DiaryListManager.shared

    @State private var width: CGFloat?
    @State private var diaryCountMaxedOut = false
    @Binding var isAddDiaryPresented: Bool
    
    let diaryWidth = 140.0
    let diaryHeight = 210.0
    let verticalPadding = 30.0
    let horizontalPadding = 20.0
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 16.0) {
                ForEach(0..<diaryListManager.diaryList.count, id: \.self) { idx in
                    Button {
                        let diaryId =  diaryListManager.diaryList[idx].diaryId
                        DiaryListManager.shared.selectDiary(diaryListManager.diaryList[idx])
                        path.navigateTo(.diary, diaryId)
                    } label: {
                        DiaryCoverView(diaryInfo: $diaryListManager.diaryList[idx], onManipulate: false)
                            .frame(width: diaryWidth, height: diaryHeight)
                            .padding(.vertical, verticalPadding)
                    }
                }
                Button {
                    if diaryListManager.diaryList.count > 4 {
                        diaryCountMaxedOut = true
                    } else {
                        isAddDiaryPresented.toggle()
                    }
                } label: {
                    AddDiaryButtonView()
                        .frame(width: diaryWidth, height: diaryHeight)
                        .padding(.vertical, verticalPadding)
                }
            }
            .alert(Titles.diaryLimit, isPresented: $diaryCountMaxedOut) {
                Button(Titles.confirm, role: .cancel) { }
            }
            .padding(.horizontal, horizontalPadding)
            .frame(minWidth: UIScreen.main.bounds.size.width)
        }
        .frame(height: diaryHeight + verticalPadding * 2.0)
    }
}

#Preview {
    DiaryListView(isAddDiaryPresented: .constant(false))
}
