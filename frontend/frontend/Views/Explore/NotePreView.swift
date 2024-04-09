//
//  NotePreView.swift
//  frontend
//
//  Created by Katherine JANG on 7/4/23.
//

import SwiftUI

struct testNote {
    var id: UUID
    var title: String
    var preview: String
    var tumbNailUrl: String
}

var sampleNote = testNote(id: UUID(), title: "테스트 글입니다", preview: "테스트를 위한 임시 노트입니다 어쩌구 저ㄱ쩌구 이러쿵 저러쿵 냠냠챱챱...", tumbNailUrl: "BackgroundImage")

struct NotePreView: View {
    var isRecommandedNote: Bool
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 8)
//                .fill(Image(sampleNote.tumbNailUrl))
//            .foregroundColor(.clear)
//            .background(Color.black.opacity(0.3))
//            .fill(Image(sampleNote.tumbNailUrl))
//            .background(Image(sampleNote.tumbNailUrl).resizable())
            
            VStack(alignment: .leading) {
                if isRecommandedNote {
                    Text("인기 상승 중")
                        .foregroundColor(.defaultGray)
                        .smallBold()
                    Text("오늘의 글")
                        .foregroundColor(.white)
                        .sansSerifBold(24)
                }
                Spacer()
                Text(sampleNote.title)
                    .foregroundColor(.white)
                    .serif(24)
                Text(sampleNote.preview)
                    .foregroundColor(.white)
            }
       }
    }
}

struct NotePreView_Previews: PreviewProvider {
    static var previews: some View {
        NotePreView(isRecommandedNote: true)
    }
}
