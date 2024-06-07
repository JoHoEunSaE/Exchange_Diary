//
//  CommonProfileRow.swift
//  frontend
//
//  Created by Katherine JANG on 2/26/24.
//

import SwiftUI
import Kingfisher

struct CommonProfileRow: View {
    let profileImageUrl: String?
    let nickName: String
    let showCrown: Bool
    
    init(_ nickName: String, _ profileImageUrl: String?, showCrown: Bool) {
        self.nickName = nickName
        self.profileImageUrl = profileImageUrl
        self.showCrown = showCrown
    }
    
    var body: some View {
        HStack {
            LoadProfileImage(profileImageUrl, size: 32)
                .shadow(color: .black.opacity(0.08), radius: 20, x: 5, y: 5)
            Text(nickName)
                .regular()
                .lineLimit(1)
            if showCrown {
                Image("crownIcon")
            }
        }
    }
}

#Preview {
    CommonProfileRow("", "", showCrown: false)
}
