//
//  UserProfileView.swift
//  frontend
//
//  Created by 신인호 on 1/21/24.
//

import SwiftUI

struct MemberProfileView: View {
    @Environment(\.dismiss) var dismiss
    @EnvironmentObject var path: PathViewModel
    @StateObject var profileManager = MemberProfileManager()
    @ObservedObject var blockManager = BlockManager.shared
    @State var isReportScreenPresented = false
    let memberId: Int
    
    var body: some View {
        VStack {
            ProfileContentView(profile: $profileManager.profile)
            Spacer()
            BannerView()
                .padding(.horizontal, 20)
        }
        .fullScreenCover(isPresented: $isReportScreenPresented) {
            ReportOptionView(isReportScreenPresented: $isReportScreenPresented,
                             memberId: memberId)
        }
        .onAppear {
            Task {
                try await profileManager.getProfileById(self.memberId)
            }
        }
        .toolbar {
            if MyProfileManager.shared.isNotMe(memberId) {
                let isBlocked = blockManager.isBlocked(memberId: memberId)
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button (isBlocked ? Titles.unblockMessage : Titles.blockMessage, role: .destructive) {
                            if isBlocked {
                                blockManager.unblockUser(memberId: memberId)
                            } else {
                                blockManager.blockUser(memberId: memberId)
                            }
                        }
                        Button(Titles.report, role: .destructive) {
                            isReportScreenPresented = true
                        }
                    } label: {
                        Image(systemName: "ellipsis")
                            .frame(width: 32, height: 32)
                    }
                }
            }
        }
    }
}

#Preview {
    MemberProfileView(memberId: 0)
}
