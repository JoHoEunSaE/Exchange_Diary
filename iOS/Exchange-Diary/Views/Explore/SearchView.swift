//
//  SearchView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 7/4/23.
//

import SwiftUI

struct userPreview: Hashable{
    var uuid: UUID
    var userName: String
    var profileImage: String
}

var sampleSearch: [userPreview] = [
    userPreview(uuid: UUID(), userName: "aaaa", profileImage: "fleuron"),
    userPreview(uuid: UUID(), userName: "aabb", profileImage: "fleuron"),
    userPreview(uuid: UUID(), userName: "aacc", profileImage: "fleuron"),
    userPreview(uuid: UUID(), userName: "aadd", profileImage: "fleuron")
]

struct SearchView: View {
    @State var searchText: String = ""
    var body: some View {
        ZStack {
            Color.white
                .ignoresSafeArea()
            VStack {
                HStack {
                    HStack {
                        TextField("유저 검색",
                                  text: $searchText)
                        Button {
                            
                        } label: {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding()
                    .background(RoundedRectangle(cornerRadius: 8).fill(Color.backgroundGray).frame(height: 42))
                }
                .padding(.horizontal, 20)
                if sampleSearch.count == 0 {
                    Text("사용자 이름을 검색해주세요")
                        .foregroundColor(.gray)
                    
                } else {
                    ScrollView(showsIndicators: false) {
                        VStack(alignment: .leading){
                            ForEach(sampleSearch, id: \.self) { item in
                                HStack {
                                    Image(systemName: "person.circle")
                                        .resizable()
                                        .frame(width: 32, height: 32)
                                    Text(item.userName)
                                        .large()
                                    Spacer()
                                }
                                .padding(EdgeInsets(top: 10, leading: 20, bottom: 0, trailing: 0))
                            }
                            Spacer()
                        }
                    }
                }
            }
        }
    }
}

struct SearchView_Previews: PreviewProvider {
    static var previews: some View {
        SearchView(searchText: "")
    }
}
