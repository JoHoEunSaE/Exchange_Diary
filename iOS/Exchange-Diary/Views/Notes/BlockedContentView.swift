//
//  BlockedContentView.swift
//  frontend
//
//  Created by Katherine JANG on 2/11/24.
//

import SwiftUI

struct BlockedContentView: View {
    let showContent: () -> Void
    
    var body: some View {
        GeometryReader { _ in
            ZStack(alignment: .center){
                Color.defaultBackground
                VStack(spacing: 8) {
                    Image("hideIcon")
                        .resizable()
                        .frame(width: 18.69, height: 16)
                    Text(Titles.blockedContent)
                    CommonButton("눌러서 확인하기") {
                        showContent()
                    }
                    .pale()
                    .small()
                    .frame(width: 160)
                    .padding(.top, 30)
                }
                .foregroundColor(.defaultGray)
                .padding(.bottom, 160)
            }
            .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.size.height)
        }
    }
}

struct BlockedContentRow: View {
    var body: some View {
        HStack(spacing: 8) {
            Image("hideIcon")
            Text(Titles.blockedContent)
                .small()
            Spacer()
        }
        .foregroundStyle(Color.defaultGray)
        .frame(height: 60)
    }
}

#Preview {
    BlockedContentView(showContent: {})
}
