//
//  ExploreView.swift
//  frontend
//
//  Created by Katherine JANG on 7/4/23.
//

import SwiftUI

struct ExploreView: View {
    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink(destination: SearchView(), label: {
                    HStack {
                        Text("유저검색")
                            .foregroundColor(.gray)
                            .padding(.leading, 15)
                        Spacer()
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.black)
                            .padding(.trailing, 10)
                    }
                    .background(RoundedRectangle(cornerRadius: 8).fill(Color.backgroundGray).frame(height: 42))
                    .padding(.vertical, 20)
                })
                ScrollView(showsIndicators: false) {
                    NotesGridView(items: .constant(samplePostLists))
                }
            }
            .padding(.horizontal, 20)
        }
    }
}

struct ExploreView_Previews: PreviewProvider {
    static var previews: some View {
        ExploreView()
    }
}
