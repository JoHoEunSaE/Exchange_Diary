//
//  NoteView.swift
//  frontend
//
//  Created by Katherine JANG on 6/10/23.
//

import SwiftUI

struct NoteView: View {
    @StateObject var noteManager: NoteViewModel
    @State private var showList: Bool = false
    @State var toolbarPresented: Bool = false
    let IdPair : DiaryNoteIdentifier

    init(idPair: DiaryNoteIdentifier) {
        self.IdPair = idPair
        self._noteManager = StateObject(wrappedValue: NoteViewModel(noteId: idPair.noteId, diaryId: idPair.diaryId))
    }

    var body: some View {
        NoteMainView(noteManager: noteManager, showList: $showList)
            .toolbar(.hidden, for: .tabBar)
            .toolbar(toolbarPresented ? .visible : .hidden,
                     for: .bottomBar)
            .toolbar {
                ToolbarItemGroup(placement: .bottomBar) {
                    NoteToolBarView(noteManager: noteManager, showList: $showList)
                }
            }
            .onTapGesture {
                withAnimation(.easeInOut(duration: 1)) {
                    toolbarPresented.toggle()
                }
            }
            .overlay {
                noteManager.isLoading ? SkeleltonNoteView() : nil
            }
    }
}

struct OffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0.0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value += nextValue()
    }
}

#Preview {
    NoteView(idPair: DiaryNoteIdentifier(noteId: 0, diaryId: 0))
}

