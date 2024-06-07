//
//  NoteMainView.swift
//  frontend
//
//  Created by Katherine JANG on 1/14/24.
//

import SwiftUI

struct NoteMainView: View {
    @ObservedObject var noteManager: NoteViewModel
    @EnvironmentObject var path: PathViewModel

    @Binding var showList: Bool
    @State private var isLiked: Bool = false
    @State private var scrollOffset: CGFloat = 0.0
    @State private var imageSize: CGFloat = 380.0
    @State var isShownReportScreen = false
    @State var showTitle = false

    var maxImageSize: CGFloat = 380.0
    var minImageSize: CGFloat = 50.0
    var wrapPadding: CGFloat = 20.0
    @State var titleHeight: CGFloat = .zero
    var titleOffset: CGFloat { -(titleHeight + wrapPadding) }

    var body: some View {
        ScrollView {
            GeometryReader { geo in
                Color.clear
                    .preference(
                        key: OffsetPreferenceKey.self,
                        value: geo.frame(in: .named("scroll")).minY
                    )
            }
            .frame(height: 0)
            VStack(alignment: .center) {
                NoteInfoView(noteContent: noteManager.noteContent, titleHeight: $titleHeight)
                NoteBodyView(noteContent: noteManager.noteContent)
            }
            .padding(.vertical, wrapPadding)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    TopRightButton
                }
            }
            .overlayIf($noteManager.isContentBlocked, BlockedContentView(){
                noteManager.isContentBlocked = false
            })
        }
        .scrollDisabled(noteManager.isContentBlocked)
        .coordinateSpaceModifier(name: "scroll")
        .onPreferenceChange(OffsetPreferenceKey.self) { offset in
            showTitle = offset < titleOffset
        }
        .navigationTitle(showTitle ? noteManager.noteContent.title : "")
        .fullScreenCover(isPresented: $isShownReportScreen) {
            ReportOptionView(isReportScreenPresented: $isShownReportScreen,
                             noteId: noteManager.noteContent.noteId)
        }
        .sheet(isPresented: $showList) {
            VStack(spacing: 15) {
                HStack {
                    Text(Titles.noteList)
                        .largeBold()
                    Spacer()
                }
                Divider()
                    .overlay(Color.line)
                ScrollView(showsIndicators: false) {
                    NotesListView(
                        noteLists: noteManager.noteList,
                        upLoadLists: noteManager.getNoteList
                    ) { noteId in
                        noteManager.getNoteInDiary(noteId)
                        self.showList = false
                    }
                }
            }
            .padding([.top, .horizontal], 20)
            .presentationDetents([.medium,.large])
            .edgesIgnoringSafeArea(.all)
            .presentationDragIndicator(.visible)
        }
    }

    var TopRightButton: some View {
        Menu {
            if noteManager.noteContent.author.memberId == MyProfileManager.shared.myProfile.memberId {
                Button(role: .destructive, action: {
                    Task {
                        do {
                            try await noteManager.deleteNote()
                            path.goBack()
                        } catch {
                            AlertManager.shared.setError(error as? ResponseErrorData)
                        }
                    }
                }) {
                    Text(Titles.delete)
                }
            } else {
                Button(role: .destructive, action: {
                    self.isShownReportScreen = true
                }) {
                    Text(Titles.report)
                }
            }
        } label: {
            Image(systemName: "ellipsis")
                .frame(width: 32, height: 32)
        }
    }
}

#Preview {
    NoteMainView(noteManager: NoteViewModel(noteId: 0, diaryId: 0), showList: .constant(false))
}

