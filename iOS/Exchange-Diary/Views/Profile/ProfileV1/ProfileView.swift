//
//  ProfileView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 10/31/23.
//

import SwiftUI

struct ProfileView: View {
    @ObservedObject private var myProfileManager = MyProfileManager.shared
    var memberId: Int
    
    var body: some View {
        if myProfileManager.myProfile.memberId == memberId {
            MyProfileMainView()
        } else {
            OthersProfileMainView()
        }
    }
}

#Preview {
    ProfileView(memberId: 123)
}
