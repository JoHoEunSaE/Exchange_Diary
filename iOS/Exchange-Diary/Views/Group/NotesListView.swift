//
//  NotesListView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 6/10/23.
//

import SwiftUI

struct NotesListView: View {
    @EnvironmentObject var path: PathViewModel
    var noteLists: [NotePreviewModel]
    let upLoadLists: @MainActor () -> ()
    let tabNote: (Int) -> ()

    @State private var sortOption: SortOption = .latest
    @State private var isMenuPresented: Bool = false

    var body: some View {
        VStack {
            NotesListOptionView(
                sortOption: $sortOption,
                isMenuPresented: $isMenuPresented
            )

            LazyVStack {
                if noteLists.count > 0 {
                    let noteList = noteLists.sorted(by: {
                        sortOption == .latest ? $0.createdAt > $1.createdAt : $0.createdAt < $1.createdAt
                    })
                    ForEach(Array(noteList.enumerated()), id: \.offset) { index,  notePreview in
                        Button {
                            tabNote(notePreview.noteId)
                        } label: {
                            if !notePreview.isBlocked {
                                NoteListRowView(notePreview: notePreview)
                                    .frame(height: 60)
                            } else {
                                BlockedContentRow()
                            }
                        }
                        .padding(.bottom, 12)
                        .onAppear {
                            if index == noteList.count - 1 {
                                upLoadLists()
                            }
                        }
                    }
                } else {
                    /// FIXME padding 대신 Geometry
                    EmptyPostSuggestion()
                        .padding(.top, 80)
                }
            }
            .navigationTitle("")
        }
    }
}

struct NoteListRowView: View {
    var notePreview: NotePreviewModel

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(notePreview.title.trimmingCharacters(in: .whitespacesAndNewlines))
                    .foregroundColor(.accentColor)
                    .lineLimit(1)
                    .bold()
                    .regular()
                Text(notePreview.preview.trimmingCharacters(in: .whitespacesAndNewlines))
                    .foregroundColor(.accentColor)
                    .lineLimit(1)
                    .small()
                HStack(spacing: 6.0) {
                    InformationText(information: notePreview.author?.nickname ?? "author")
                    InformationDivider()
                    InformationText(information: notePreview.createdAt.convertToLocalTime())
                    if notePreview.likeCount > 0 {
                        InformationDivider()
                        HStack(spacing: 0) {
                            InformationHeart()
                            InformationText(information: String(notePreview.likeCount))
                        }
                    }
                }
            }
            Spacer()
            if notePreview.thumbnailUrl != nil {
                LoadPreviewImage(notePreview.thumbnailUrl)
                    .frame(width: 60, height: 60)
                    .cornerRadius(10)
                    .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
            }
        }
    }
}

struct EmptyPostSuggestion: View {
    var body: some View {
        ZStack(alignment: .center) {
            VStack {
                Image("pencilIcon")
                    .resizable()
                    .frame(width: 19, height: 19)
                Text(Titles.makeFirstNote)
                    .regular()
                    .foregroundColor(.gray)
            }
        }
    }
}
