//
//  SlideImageView.swift
//  frontend
//
//  Created by 신인호 on 2023/06/10.
//

import SwiftUI

struct SlideImageView: View {
    @EnvironmentObject var path: PathViewModel
    let slideHeight: CGFloat = 374
    @ObservedObject var recentNoteManager = DiaryListManager.shared
    @ObservedObject var noteCreatManager = CreateNoteViewModel.shared
    @State private var slideIndex: Int = 0
    
    var body: some View {
        TabView {
            ForEach(Array(recentNoteManager.recentNotesPreview.enumerated()), id: \.offset) { index, preview in
                ZStack {
                    LoadImageView(preview.thumbnailUrl)
                    Rectangle()
                        .fill(.black)
                        .opacity(0.3)
                    VStack {
                        // 일기장 네비게이션 버튼
                        Button {
                            openDiary(recentNoteManager.recentNotesPreview[index].diaryId)
                        } label : {
                            Text("\(preview.diaryTitle)\(preview.groupName.map { " - \($0)" } ?? "")")
                                .small()
                                .foregroundColor(.white)
                                .padding(.vertical, 4)
                                .frame(maxWidth: .infinity)
                        }
                        Spacer()
                        VStack(alignment: .leading) {
                            // 일기 네비게이션 버튼
                            Button {
                                openNote(
                                    diaryId: recentNoteManager.recentNotesPreview[index].diaryId,
                                    noteId: recentNoteManager.recentNotesPreview[index].noteId
                                )
                            } label: {
                                VStack(alignment: .leading) {
                                    Spacer()
                                    Text(preview.title)
                                        .serifBold(28)
                                        .foregroundColor(.white)
                                        .padding(.bottom, 8)
                                        .lineLimit(1)
                                    Text(preview.preview)
                                        .small()
                                        .multilineTextAlignment(.leading)
                                        .foregroundColor(.white)
                                        .lineSpacing(6.0)
                                        .lineLimit(2)
                                }
                                .padding(.vertical, 10)
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }
                            // 프로필 네비게이션 버튼
                            Button {
                                openProfile(memberId: preview.author.memberId)
                            } label : {
                                HStack(spacing: 4) {
                                    LoadProfileImage(preview.author.profileImageUrl, size: 24)
                                        .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
                                    Text(preview.author.nickname)
                                        .smallBold()
                                        .foregroundColor(.white)
                                        .lineLimit(1)
                                }
                                .padding(.vertical, 4)
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }
                        }
                    }
                    .padding(EdgeInsets(top: 20, leading: 20, bottom: 40, trailing: 20))
                }
                .tag(index)
            }
        }
        .tabViewStyle(PageTabViewStyle(indexDisplayMode: .automatic))
        .frame(height: UIScreen.main.bounds.size.width)
    }
    
    private func openDiary(_ diaryId: Int) {
        guard diaryId != -1 else {
            noteCreatManager.isCreateNotePresented.toggle()
            return
        }
        path.navigateTo(.diary, diaryId)
    }
    
    private func openNote(diaryId: Int, noteId: Int) {
        guard diaryId != -1 || noteId != -1 else {
            noteCreatManager.isCreateNotePresented.toggle()
            return
        }
        let idPair = DiaryNoteIdentifier(noteId: noteId, diaryId: diaryId)
        path.navigateTo(.diary, diaryId)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
            path.navigateTo(.note, idPair)
        }
    }
    
    private func openProfile(memberId: Int) {
        guard memberId != -1 else {
            noteCreatManager.isCreateNotePresented.toggle()
            return
        }
        path.navigateTo(.profile, memberId)
    }
}


#Preview {
    SlideImageView()
}
