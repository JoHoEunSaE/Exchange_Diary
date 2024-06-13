//
//  GridListView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/07/12.
//

import SwiftUI


struct NotesGridView: View {
    @Binding var items: [Post]
    
    let gridColumns = [GridItem(.flexible()), GridItem(.flexible())] // 2열로 설정
    
    var body: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(columns: gridColumns, spacing: 6) {
                ForEach(items, id: \.self) { item in
                    ZStack(alignment: .topLeading) {
                        Image(item.imageName)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .cornerRadius(8)
                            .colorMultiply(Color.gray.opacity(0.9))
                        VStack(alignment: .leading, spacing: 16) {
                            Text(item.title)
                                .small()
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                            Text(item.summary)
                                .small()
                                .foregroundColor(.white)
                                .frame(width: 90, height: 90, alignment: .topLeading)
                        }
                        .padding(12)
                    }
                }
            }
        }
    }
    
}


struct NotesGridView_Previews: PreviewProvider {
    @State static var dummyItems = samplePostLists
    
    static var previews: some View {
        NotesGridView(items: $dummyItems)
    }
}
