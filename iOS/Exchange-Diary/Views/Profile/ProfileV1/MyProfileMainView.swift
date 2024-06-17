//
//  ProfileMainView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 6/19/23.
//

import SwiftUI

struct MyProfileMainView: View {
    @StateObject var path = PathViewModel()
    @State var selectedPost = 1
    @State private var viewMode: ViewMode = .list
    @State private var sortOption: SortOption = .latest
    @State private var isMenuPresented: Bool = false
    @State var noteList = samplePostLists
    
    var body: some View {
        NavigationStack(path: $path.targetDestination) {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .center){
                    MyProfileInfoView()
                    profileEditButton
                    BannerView()
                        .padding(.bottom, 20)
                    HStack {
                        Button {
                            selectedPost = 1
                        } label: {
                            VStack {
                                Text(Titles.myNote)
                                    .fontWeight(selectedPost == 1 ? .bold : .regular)
                                    .regular()
                                    .foregroundColor(selectedPost == 1 ? Color.accentColor : .gray)
                                Divider()
                                    .frame(minHeight: 1.5)
                                    .overlay(selectedPost == 1 ? Color.accentColor : Color.defaultBackground)
                            }
                        }
                        Button {
                            selectedPost = 2
                        } label: {
                            VStack {
                                Text(Titles.scrappedNote)
                                    .fontWeight(selectedPost == 2 ? .bold : .regular)
                                    .regular()
                                    .foregroundColor(selectedPost == 2 ? Color.accentColor : .gray)
                                Divider()
                                    .frame(minHeight: 1.5)
                                    .overlay(selectedPost == 2 ? Color.accentColor :  Color.defaultBackground)
                            }
                        }
                    }
                    .padding(.bottom, 16)
                    NotesListOptionView(
//                        viewMode: $viewMode,
                        sortOption: $sortOption,
                        isMenuPresented: $isMenuPresented
                    )
                    //                    if viewMode == .list {
                    //                        NotesListView(items: $noteList)
                    //                    } else {
                    //                        NotesGridView(items: $noteList)
                    //                    }
                }
            }
            .padding(.horizontal, 20)
            .overlay {
                if isMenuPresented {
                    Color.white.opacity(0.001)
                        .ignoresSafeArea()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .onTapGesture {
                            isMenuPresented = false
                        }
                }
            }
            .navigationDestination(for: TargetViewInfo.self) { target in
                switch target.destination {
                case .diary:
                    DiaryView(diaryId: target.targetData as! Int)
                case .profile:
                    ProfileView(memberId: target.targetData as! Int)
                case .note:
                    NoteView(idPair: target.targetData as? DiaryNoteIdentifier ??
                             DiaryNoteIdentifier(noteId: 0, diaryId: 0))
                case .profileEdit:
                    ProfileEditView()
                default:
                    HomeView()
                }
            }
        }
    }
    
    var profileEditButton: some View {
        Button {
            path.navigateTo(.profileEdit, nil as Int?)
        } label: {
            Text(Titles.profileEdit)
                .frame(height: 30)
                .frame(maxWidth: .infinity)
                .smallBold()
                .foregroundColor(Color.reverseAccentColor)
                .background(Color.defaultBlack)
                .cornerRadius(8)
                .padding(.bottom, 20)
        }
        .navigationDestination(for: TargetDestination.self) { _ in
            ProfileEditView()
        }
    }
}

struct MyProfileMainView_Previews: PreviewProvider {
    static var previews: some View {
        MyProfileMainView()
    }
}
