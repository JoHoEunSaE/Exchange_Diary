//
//  ManageBlockView.swift
//  frontend
//
//  Created by Katherine JANG on 1/7/24.
//

import Foundation
import SwiftUI

struct ManageBlockView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject private var blockManager = BlockManager.shared
    @State private var selectedUser: BlockModel?
    @State private var isUnblockAlertPresented: Bool = false
    @State private var blockedList: [BlockModel] = BlockManager.shared.blockedList

    private let rowPadding = EdgeInsets(top: 5.0, leading: 20.0, bottom: 5.0, trailing: 20.0)

    var body: some View {
        ScrollView(.vertical) {
            if blockedList.count == 0 {
                Text(Titles.noBlockedListMessage)
                    .foregroundStyle(Color.defaultGray)
                    .padding()
            } else {
                LazyVStack(spacing: 15) {
                    ForEach(Array(blockedList.enumerated()), id: \.offset) { (index, user) in
                        BlockListRowView(user: user) {
                            selectedUser = user
                            isUnblockAlertPresented = true
                        }
                        .padding(rowPadding)
                        .onAppear {
                            if index == blockedList.count - 1 {
                                blockManager.getList()
                            }
                        }
                    }
                    Spacer()
                }
            }
        }
        .alert(
            "\(selectedUser?.nickname ?? "")\(Titles.blockConfirmMessage)",
            isPresented: $isUnblockAlertPresented
        ) {
            Button(role: .cancel) {
                isUnblockAlertPresented = false
            } label: {
                Text(Titles.cancel)
            }
            Button() {
                guard let selectedUser else {
                    return
                }
                blockManager.unblockUser(memberId: selectedUser.blockedUserId)
            } label: {
                Text(Titles.unblockAlertButton)
            }
        }
        .refreshable {
            blockManager.setList()
        }
        .onReceive(blockManager.$blockedList) { blockedList in
            // manager에서 blockedList를 set할 때 배열을 비우고 다시 넣다보니 전부 없어졌다가 나와서 깜빡거리는 현상이 있었습니다. 그걸 방지하기 위한 return 입니다.
            if blockedList.isEmpty && self.blockedList.count > 1 {
                return
            }
            self.blockedList = blockedList
        }
    }
}

struct BlockListRowView: View {
    let user: BlockModel
    let onButtonTap: () -> Void

    private let cornerRadius = 8.0
    private let imageSize = 32.0
    private let buttonSize = CGSize(width: 74.0, height: 28.0)

    var body: some View {
        HStack(alignment: .center) {
            CommonProfileRow(user.nickname, user.profileImageUrl, showCrown: false)
            Spacer()
            Button {
                onButtonTap()
            } label: {
                Text(Titles.unblockMessage)
                    .small()
                    .foregroundStyle(Color.reverseAccentColor)
            }
            .frame(width: buttonSize.width, height: buttonSize.height)
            .background(Color.accentColor)
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
        }
    }
}
