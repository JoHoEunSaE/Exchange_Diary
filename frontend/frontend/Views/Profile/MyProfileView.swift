//
//  MyProfileView.swift
//  frontend
//
//  Created by 신인호 on 1/21/24.
//

import SwiftUI

struct MyProfileView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject var myProfileManager = MyProfileManager.shared
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack {
                    ProfileContentView(profile: $myProfileManager.myProfile)
                    SettingView()
                        .padding(.horizontal, 20)
                    Spacer()
                    BannerView()
                }
                Spacer()
            }
            .scrollIndicators(.hidden)
        }
    }
}

#Preview {
    MyProfileView()
}
