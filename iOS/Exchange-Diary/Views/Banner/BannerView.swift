//
//  BannerView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/07/05.
//

import SwiftUI

struct BannerView: View {
    @Environment(\.openURL) private var openURL
    var imageURL: String?
    var bannerURL: String = "\(urlHost)/v1/redirect/guide"
    @State private var isPresented = false
    
    var body: some View {
        VStack {
            Button {
                if let url = URL(string: bannerURL) {
                    openURL(url)
                }
            } label: {
                if let urlString = imageURL, let url = URL(string: urlString) {
                    AsyncImage(url: url) { image in
                        BannerImageView(image: image)
                    } placeholder: {
                        BannerImageView(image: Image("DefaultBanner"))
                    }
                } else {
                    BannerImageView(image: Image("DefaultBanner"))
                }
            }
        }
        .padding(.bottom, 8.0)
    }
}

struct BannerImageView: View {
    let image: Image
    let bannerWidth = UIScreen.main.bounds.size.width - 40.0
    let bannerHeight: CGFloat = 96
    
    var body: some View {
        image
            .resizable()
            .aspectRatio(contentMode: .fill)
            .frame(width: bannerWidth, height: bannerHeight)
            .cornerRadius(8)
            .shadow(color: .black.opacity(0.15), radius: 4, x: 0, y: 2)
    }
}

#Preview {
    BannerView(imageURL: "https://picsum.photos/400/100", bannerURL: "https://picsum.photos")
}
