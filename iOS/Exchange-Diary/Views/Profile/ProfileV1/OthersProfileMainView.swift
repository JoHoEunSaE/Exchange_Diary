//
//  OthersProfileMainView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/07/12.
//

import SwiftUI

struct OthersProfileMainView: View {
    @State var selectedPost = 1
    @State private var viewMode: ViewMode = .list
    @State private var sortOption: SortOption = .latest
    @State private var isMenuExpanded: Bool = false
    
    @State var noteList = samplePostLists
    
    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .center){
                OthersProfileInfoView()
                BannerView()
                    .padding(.bottom, 10)
                NotesListOptionView(
//                    viewMode: $viewMode,
                    sortOption: $sortOption,
                    isMenuPresented: $isMenuExpanded
                )
//                if viewMode == .list {
//                    NotesListView(items: $noteList)
//                } else {
//                    NotesGridView(items: $noteList)
//                }
            }
            .padding(20)
        }
        .onTapGesture {
            isMenuExpanded = false
        }
        .overlay{
            if isMenuExpanded {
                Color.white.opacity(0.001)
                    .ignoresSafeArea()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .onTapGesture {
                        isMenuExpanded = false
                    }
            }
        }
    }
}

enum ViewMode {
    case grid
    case list
}

enum SortOption: CaseIterable {
    case latest
    case oldest

    var title: String {
        switch self {
        case .latest:
            "최신 순"
        case .oldest:
            "오래된 순"
        }
    }
}

struct OthersProfileMainView_Previews: PreviewProvider {
    static var previews: some View {
        OthersProfileMainView()
    }
}
