//
//  SkeletonHomeView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2/3/24.
//

import SwiftUI

struct SkeletonHomeView: View {
    private let diaryHeight = 210.0
    private let verticalPadding = 30.0
    private let horizontalPadding = 20.0
    
    var body: some View {
        VStack {
            SkeletonHomeTopView()
            ScrollView {
                VStack(spacing: 0) {
                    SkeletonSlideImageView()
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 16.0) {
                            SkeletonDiaryCoverView()
                            SkeletonDiaryCoverView()
                            SkeletonDiaryCoverView()
                        }
                        .padding(.horizontal, horizontalPadding)
                    }
                    .frame(height: diaryHeight + verticalPadding * 2.0)
                }
            }
        }
        .shimmering()
        .background(Color.defaultBackground)
    }
}

struct SkeletonHomeTopView: View {
    var body: some View {
        ZStack {
            Text(Titles.appTitle)
                .serif(18)
                .frame(maxWidth: .infinity, alignment: .center)
                .foregroundStyle(Color.accentColor)
            HStack {
                Spacer()
                Button {
                } label: {
                    Image("notiIcon")
                        .frame(width: 24, height: 24)
                }
            }
        }
        .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
    }
}

struct SkeletonSlideImageView: View {
    var body: some View {
        Rectangle()
            .fill(Color.skeletonColorDark)
            .frame(height: UIScreen.main.bounds.size.width)
    }
}

struct SkeletonDiaryCoverView: View {
    private let diaryWidth = 140.0
    private let diaryHeight = 210.0
    private let verticalPadding = 30.0
    
    var body: some View {
        Rectangle()
            .fill(Color.reverseAccentColor)
            .clipShape(RoundedCorner(corner: [.topRight, .bottomRight], radius: 10))
            .frame(width: diaryWidth, height: diaryHeight)
            .shadow(color: .black.opacity(0.1), radius: 21, x: 10, y: 10)
            .padding(.vertical, verticalPadding)
    }
}

#Preview {
    SkeletonHomeView()
}
