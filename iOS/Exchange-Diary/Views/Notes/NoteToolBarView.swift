//
//  NoteToolBarView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/12/24.
//

import SwiftUI

struct NoteToolBarView: View {
    @ObservedObject var noteManager: NoteViewModel
    @Binding var showList: Bool
    
    let buttonHeight: CGFloat = 40.0
    
    var body: some View {
        HStack {
            Button {
                noteManager.getPrevNote()
                HapticManager.shared.impact(style: .light)
            } label: {
                Image(systemName: "chevron.backward")
                    .renderingMode(.template)
					.foregroundColor(.accentColor)
					.opacity(noteManager.noteContent.prevNoteId != nil ? 1.0 : 0.5)
            }
            .disabled(noteManager.noteContent.prevNoteId == nil)
            .frame(maxWidth: .infinity, idealHeight: buttonHeight)
            
            Button {
                if noteManager.noteContent.isLiked {
                    noteManager.dislikeNote()
                } else {
                    noteManager.likeNote()
                }
                HapticManager.shared.impact(style: .light)
            } label: {
                Image(systemName: noteManager.noteContent.isLiked ? "heart.fill" : "heart")
                    .foregroundColor(.accentColor)
            }
            .frame(maxWidth: .infinity, idealHeight: buttonHeight)
			
// TODO: 북마크 기능은 추후 버전에서 오픈 예정
/*
            Button {
                if noteManager.noteContent.isBookmarked {
                   noteManager.deleteBookmarkNote()
                } else {
                   noteManager.bookmarkNote()
                }
                HapticManager.shared.impact(style: .light)
            } label: {
                Image(systemName: noteManager.noteContent.isBookmarked ? "bookmark.fill" : "bookmark")
                    .foregroundColor(.accentColor)
            }
            .frame(maxWidth: .infinity, idealHeight: buttonHeight)
*/
		
            Button {
                showList = true
                HapticManager.shared.impact(style: .light)
            } label: {
                Image(systemName: "list.bullet")
                    .foregroundColor(.accentColor)
            }
            .frame(maxWidth: .infinity, idealHeight: buttonHeight)
            Button {
                 noteManager.getNextNote()
                 HapticManager.shared.impact(style: .light)
            } label: {
                Image(systemName: "chevron.forward")
					.renderingMode(.template)
					.foregroundColor(.accentColor)
					.opacity(noteManager.noteContent.nextNoteId != nil ? 1.0 : 0.5)
                    .foregroundColor(.accentColor)
            }
            .disabled(noteManager.noteContent.nextNoteId == nil)
            .frame(maxWidth: .infinity, idealHeight: buttonHeight)
        }
        .padding(.horizontal, -10)
    }
}

#Preview {
    NoteToolBarView(noteManager: NoteViewModel(noteId: 0, diaryId: 1), showList: .constant(false))
}
