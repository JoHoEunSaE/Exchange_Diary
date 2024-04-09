//
//  DiaryView.swift
//  frontend
//
//  Created by Katherine JANG on 5/20/23.
//

import SwiftUI
import Kingfisher

struct DiaryCoverView: View {
    @Environment (\.colorScheme) var colorScheme
    @Binding var diaryInfo: DiaryInfoModel
    var coverImage: Binding<Data?>? = nil
    var onManipulate: Bool
    
    var body: some View {
        ZStack {
            Rectangle()
                .foregroundColor(.reverseAccentColor)
                .clipShape(RoundedCorner(corner: [.topRight, .bottomRight], radius: 10))
                .shadow(color: .black.opacity(0.1), radius: 21, x: 10, y: 10)
            VStack(spacing: onManipulate ? 15 : 10) {
                HStack {
                    Text(diaryInfo.title == "" ? Titles.diaryTitle : diaryInfo.title)
                        .serifBold(onManipulate ? 24 : 16)
                        .foregroundColor(.accentColor)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)
                    Spacer()
                }
                if diaryInfo.coverType == .color {
                    Rectangle()
                        .aspectRatio(1.0, contentMode: .fit)
                        .cornerRadius(5)
                        .foregroundColor(Color(hexWithAlpha: diaryInfo.coverData))
                        .opacity(colorScheme == .light ? 1 : 0.8)
                        .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
                } else  {
                    if onManipulate && coverImage?.wrappedValue != nil {
                        Image(uiImage: ((UIImage(data: coverImage?.wrappedValue ?? Data()) ?? UIImage(systemName: "circle"))!))
                            .centerCropped()
                            .cornerRadius(5)
                            .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
                    } else {
                        LoadImageView(diaryInfo.coverData)
                            .aspectRatio(1.0, contentMode: .fit)
                            .cornerRadius(5)
                            .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
                    }
                }
                Text(diaryInfo.groupName ?? "")
                    .serif(onManipulate ? 18 : 12)
                    .foregroundColor(.defaultGray)
                    .lineLimit(2)
            }
            .padding(onManipulate ? 18 : 12)
        }
    }
}

#Preview {
    @State var diary = DiaryInfoModel(
        diaryId: 1,
        coverType: .color,
        coverData: "#eaeaeaff",
        title: "일기장 제목",
        groupName: "groupName",
        imageData: nil,
        createdAt: "1999-12-12"
    )
    
    return DiaryCoverView(diaryInfo: $diary, coverImage: nil, onManipulate: false)
}
