//
//  DelegateMasterView.swift
//  frontend
//
//  Created by Katherine JANG on 12/28/23.
//

import SwiftUI

struct DelegateMasterView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject var diaryManager: DiaryViewModel
    @State var selectedMemberId: Int = 1
    let isLeaveDiary: Bool
    
    var body: some View {
        VStack(spacing: 10) {
            if diaryManager.membersInfo.count == 1 {
                Text(Titles.delegateMasterAlert)
                    .regular()
                    .foregroundStyle(Color.defaultGray)
            } else {
                ForEach (diaryManager.membersInfo, id: \.self) { member in
                    if member.memberId != diaryManager.diaryInfo.masterMemberId {
                        Button(action: {
                            selectedMemberId = member.memberId
                        }) {
                            MemberListRow(member: member, isSelectedMember: selectedMemberId == member.memberId)
                        }
                    }
                }
            }
            Spacer()
        }
        .toolbar {
            TopbarTitle("대표 변경")
            ToolbarItem(placement: .topBarTrailing) {
                Button(Titles.complete) {
                    Task {
                        do {
                            try await diaryManager.changeMasterOfDiary(targetMemeberId: selectedMemberId)
                            diaryManager.diaryInfo.masterMemberId = selectedMemberId
                            diaryManager.isMaster = false
                            dismiss()
                        } catch {
                            AlertManager.shared.setError(error as? ResponseErrorData)
                        }
                    }
                }
                .disabled(diaryManager.membersInfo.count == 1)
            }
        }
        .padding(EdgeInsets(top: 30, leading: 10, bottom: 0, trailing: 10))
    }
}

struct MemberListRow: View {
    let member: MemberModel
    let isSelectedMember: Bool
    var body: some View {
        HStack(alignment: .center) {
            LoadProfileImage(member.profileImageUrl, size: 32)
                .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
            Text(member.nickname)
                .regular()
            Spacer()
            Circle()
                .strokeBorder(.gray, lineWidth: 2)
                .frame(width: 20, height: 20)
                .overlay {
                    if isSelectedMember {
                        Circle()
                            .strokeBorder(.blue, lineWidth: 7)
                    }
                }
        }
    }
}

#Preview {
    DelegateMasterView(diaryManager: DiaryViewModel(diaryId: 6), selectedMemberId: 2, isLeaveDiary: false)
}
