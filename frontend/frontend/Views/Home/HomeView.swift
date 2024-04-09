//
//  HomeView.swift
//  frontend
//
//  Created by 신인호 on 2023/06/03.
//

import SwiftUI

struct HomeView: View {
    @State var isAddDiaryPresented: Bool = false
    @State var detentHeight: CGFloat = 0
    @State private var hasLoaded = false
    @EnvironmentObject var path: PathViewModel
    @ObservedObject var diaryListManager = DiaryListManager.shared

    var body: some View {
        NavigationStack(path: $path.targetDestination) {
            VStack {
                HomeTopNav
                HomeMainView
            }
            .task {
                if !hasLoaded {
                    print("home update!")
                    await diaryListManager.updateHomeView()
                    hasLoaded = true
                }
            }
            .sheet(isPresented: $isAddDiaryPresented, content: {AddDiarySheetContent})
            .navigationDestination(for: TargetViewInfo.self) { target in
                switch target.destination {
                case .diary:
                    DiaryView(diaryId: target.targetData as? Int ?? 0)
                case .addDiary:
                    let diaryInfo = target.targetData as? DiaryInfoModel ?? DiaryInfoModel()
                    AddDiaryView(diaryInfo: diaryInfo)
                case .profile:
                    MemberProfileView(memberId: target.targetData as? Int ?? 0)
                case .note:
                    let idPair = target.targetData as? DiaryNoteIdentifier ?? DiaryNoteIdentifier(noteId: 0, diaryId: 0)
                    NoteView(idPair: idPair)
                default:
                    HomeView()
                }
            }
            .overlay {
                (diaryListManager.isLoading || !hasLoaded) ? SkeletonHomeView() : nil
            }
        }
    }

    var HomeTopNav: some View {
        ZStack {
            Text(Titles.appTitle)
                .serif(18)
                .frame(maxWidth: .infinity, alignment: .center)
            HStack {
                Spacer()
                NavigationLink {
                    NoticeView()
                } label: {
                    Image("notiIcon")
                        .frame(width: 24, height: 24)
                }
            }
        }
        .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
    }
    
    var HomeMainView: some View {
        ScrollView {
            VStack(spacing: 0) {
                SlideImageView()
                DiaryListView(isAddDiaryPresented: $isAddDiaryPresented)
            }
        }
        .scrollIndicators(ScrollIndicatorVisibility.hidden)
        .refreshable {
            await diaryListManager.updateHomeView()
        }
    }

    var AddDiarySheetContent: some View {
        AddDiarySheetView(isAddDiary: $isAddDiaryPresented)
            .padding(.horizontal, 20)
            .readHeight()
            .onPreferenceChange(HeightPreferenceKey.self) { height in
                if let height {
                    self.detentHeight = height
                }
            }
            .presentationDetents([.height(self.detentHeight)])
            .presentationDragIndicator(.visible)
    }
}

#Preview {
    HomeView()
}
