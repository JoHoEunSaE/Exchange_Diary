//
//  ProfileContentView.swift
//  frontend
//
//  Created by 신인호 on 1/11/24.
//

import SwiftUI

struct ProfileContentView: View {
    @Binding var profile: ProfileModel
    
    var body: some View {
        VStack {
            LoadProfileImage(profile.profileImageUrl, size: 80)
                .overlay(Circle().stroke(Color.lightGray, lineWidth: 1))
            Text(profile.nickname)
                .sansSerifBold(18)
                .padding(8)
            VStack(alignment: .leading, spacing: 10) {
                Text(Titles.statementPlaceholder)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .smallBold()
                    .foregroundStyle(Color.defaultGray)
                Text(profile.statement)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .small()
            }
            .padding(20)
            .background(Color.line)
            .cornerRadius(8)
        }
        .padding(20)
    }
}

#Preview {
    ProfileContentView(profile: .constant(MemberProfileManager().profile))
}
