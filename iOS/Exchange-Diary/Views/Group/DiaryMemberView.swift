//
//  DiaryMemberView.swift
//  frontend
//
//  Created by Katherine JANG on 6/3/23.
//

import SwiftUI

struct DiaryMemberView: View {
    @EnvironmentObject var path: PathViewModel
    @ObservedObject var diaryManager: DiaryViewModel
    @ObservedObject var blockManager = BlockManager.shared
    @Binding var showMemberSheet: Bool
    @Binding var isReportScreenPresented: Bool
    @Binding var selectedMemberId: Int

    var body: some View {
        VStack(alignment: .leading) {
            ForEach(diaryManager.membersInfo, id: \.self) { member in
                HStack {
                    Button {
                        path.navigateTo(.profile, member.memberId)
                        showMemberSheet = false
                    } label: {
                        CommonProfileRow(member.nickname, member.profileImageUrl,
                                         showCrown: member.isMaster == true)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    Spacer()
                    if MyProfileManager.shared.isNotMe(member.memberId) {
                        let isBlocked = blockManager.isBlocked(memberId: member.memberId)

                        Menu {
                            Button (isBlocked ? Titles.unblockMessage : Titles.blockMessage, role: .destructive) {
                                if isBlocked {
                                    blockManager.unblockUser(memberId: member.memberId)
                                } else {
                                    blockManager.blockUser(memberId: member.memberId)
                                }
                            }
                            Button(Titles.report, role: .destructive) {
                                selectedMemberId = member.memberId
                                print("selectedMember: ", selectedMemberId)
                                showMemberSheet = false
                                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                    isReportScreenPresented = true
                                }
                            }
                            if diaryManager.isMaster {
                                Divider()
                                Button(Titles.kick,  role: .destructive) {
                                    showMemberSheet = false
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                        AlertManager.shared.show(
                                            title: Titles.memberKick,
                                            message: String(format: Titles.memberKickMessage, member.nickname),
                                            confirmText: Titles.kick,
                                            confirmRole: .destructive
                                        ) {
                                            do {
                                                try diaryManager.deleteMemberInDiary(memberId: member.memberId)
                                            } catch {
                                                DispatchQueue.main.asyncAfter(deadline: .now()) {
                                                    AlertManager.shared.setError(error as? ResponseErrorData)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } label: {
                            Image(systemName: "ellipsis")
                                .frame(width: 32, height: 32)
                        }
                    }
                }
                .padding(.vertical, 2)
            }
        }
        .padding(.horizontal, 20)
    }
}

#Preview {
    DiaryMemberView(diaryManager: DiaryViewModel(diaryId: 6), showMemberSheet: .constant(false),
                    isReportScreenPresented: .constant(false), selectedMemberId: .constant(0))
}
