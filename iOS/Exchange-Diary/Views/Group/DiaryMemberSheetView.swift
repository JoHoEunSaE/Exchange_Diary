//
//  DiaryMemberSheetView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/08/23.
//

import SwiftUI

struct DiaryMemberSheetView: View {
    @ObservedObject var diaryManager: DiaryViewModel
    @Binding var showMemberSheet: Bool
    @Binding var isReportScreenPresented: Bool
    @Binding var selectedMemberId: Int
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text(Titles.memberList)
                    .largeBold()
                Spacer()
            }
            .padding(.horizontal ,20)
            .padding(.top, 44)
            .padding(.bottom, 15)
            ScrollView{
                DiaryMemberView(diaryManager:diaryManager, showMemberSheet: $showMemberSheet,
                                isReportScreenPresented: $isReportScreenPresented, selectedMemberId: $selectedMemberId)
            }
        }
    }
}

struct DiaryMemberSheetView_Previews: PreviewProvider {
    static var previews: some View {
        DiaryMemberSheetView(diaryManager: DiaryViewModel(diaryId: 6), showMemberSheet: .constant(false),
                             isReportScreenPresented: .constant(false), selectedMemberId: .constant(0))
    }
}
