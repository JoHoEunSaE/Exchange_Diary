//
//  NoteInfoView.swift
//  frontend
//
//  Created by Katherine JANG on 1/13/24.
//

import SwiftUI

struct NoteInfoView: View {
    @EnvironmentObject var path: PathViewModel
    var noteContent: NoteModel
    @Binding var titleHeight: CGFloat
    let wrapPadding: CGFloat = 8.0

    var body: some View {
        VStack(spacing: 8) {
            HStack {

            Text(noteContent.title)
                .serif(18)
                .padding(.horizontal)
                .sizeReader { size in
                    if let size {
                        titleHeight = size.height + wrapPadding
                    }
                }
            }
            HStack(spacing: 6.0) {
                InformationText(information: noteContent.createdAt.convertToLocalTime())
                if noteContent.likeCount > 0 {
                    InformationDivider()
                    HStack(spacing: 0) {
                        InformationHeart()
                        InformationText(information: String(noteContent.likeCount))
                    }
                }
            }
            Button {
                path.navigateTo(.profile, noteContent.author.memberId)
            } label: {
                HStack(spacing: 4) {
                    CommonProfileRow(noteContent.author.nickname,
                                     noteContent.author.profileImageUrl, showCrown: false)
                }
            }
            .padding(.vertical, 16)
        }
        .padding(wrapPadding)
    }
}

#Preview {
    NoteInfoView(noteContent: NoteModel(), titleHeight: .constant(20))
}
