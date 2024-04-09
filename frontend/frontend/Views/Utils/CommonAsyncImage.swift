//
//  CommonAsyncImage.swift
//  frontend
//
//  Created by 신인호 on 2023/08/17.
//

import SwiftUI
import UIKit
import Kingfisher

struct CommonAsyncImage: View {
    let from: String?
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        if let urlString = from, let url = URL(string: urlString) {
            AsyncImage(url: url) { phase in
                switch phase {
                case .empty:
                    ZStack {
//                        Color.defaultBlack
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: Color.white))
                    }
                case .success(let image):
                    image.centerCropped()
                default:
                    LinearGradient.defaultGradient(colorScheme)
                }
            }
        } else {
            LinearGradient.defaultGradient(colorScheme)
        }
    }
}

struct LoadImageView: View {
    @Environment(\.colorScheme) var colorScheme
    let imageUrl: URL?
    
    init(_ imageUrl: String?) {
        self.imageUrl = URL(string: imageUrl ?? "")
    }
    
    var body: some View {
        KFImage(imageUrl)
            .placeholder {
                ZStack {
                    ProgressView()
                    LinearGradient.defaultGradient(colorScheme)
                }
            }
            .serialize(as: .JPEG, jpegCompressionQuality: 0.8)
            .loadDiskFileSynchronously(true)
            .centerCropped()
    }
}

struct LoadPreviewImage: View {
    @Environment(\.colorScheme) var colorScheme
    let imageUrl: URL?
    
    init(_ imageUrl: String?) {
        self.imageUrl = URL(string: imageUrl ?? "")
    }
    
    var body: some View {
        KFImage(imageUrl)
            .serialize(as: .JPEG, jpegCompressionQuality: 0.7)
            .loadDiskFileSynchronously(true)
            .placeholder {
                ZStack {
                    ProgressView()
                    LinearGradient.defaultGradient(colorScheme)
                }
            }
            .downsampling(size: CGSize(width: 180, height: 180))
            .cancelOnDisappear(true)
            .centerCropped()
    }
}

struct LoadProfileImage: View {
    @Environment(\.colorScheme) var colorScheme
    
    let imageUrl: URL?
    let size: CGFloat
    
    init(_ imageUrl: String?, size: Float) {
        self.imageUrl = URL(string: imageUrl ?? "")
        self.size = CGFloat(size)
    }
    
    var body: some View {
        KFImage(imageUrl)
            .resizable()
            .serialize(as: .JPEG, jpegCompressionQuality: 0.8)
            .loadDiskFileSynchronously(true)
            .placeholder {
                LinearGradient.defaultGradient(colorScheme)
            }
            .downsampling(size: CGSize(width: size * 3, height: size * 3))
            .centerCropped()
            .frame(width: size, height: size)
            .clipShape(Circle())
    }
    
}
