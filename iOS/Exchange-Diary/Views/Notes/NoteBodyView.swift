//
//  NoteTextView.swift
//  frontend
//
//  Created by Katherine JANG on 1/13/24.
//

import SwiftUI
import struct Kingfisher.KFImage

struct NoteBodyView: View {
    @Environment(\.colorScheme) var colorScheme
    var imageSize: CGFloat = UIScreen.main.bounds.width
    var noteContent: NoteModel

    var body: some View {
        VStack {
            if !noteContent.imageList.isEmpty {
                LoadImageView(noteContent.imageList.first?.imageUrl ?? "")
                    .frame(width: imageSize, height: imageSize)
            }
            Text(noteContent.content + Rules.noteContentPadding)
                .frame(maxWidth: .infinity, alignment: .leading)
                .lineSpacing(12)
                .regular()
                .padding(.vertical, 24)
                .padding(.horizontal, 20)
        }
    }
}

#Preview {
    NoteBodyView(noteContent: NoteModel())
}
