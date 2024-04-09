//
//  ProfileInfoView.swift
//  frontend
//
//  Created by Katherine JANG on 6/19/23.
//

import SwiftUI

struct MyProfileInfoView: View {
    @State private var isPresented = false
    @ObservedObject var myProfileManager = MyProfileManager.shared
    
    var body: some View {
        VStack(alignment: .leading){
            HStack {
                Text(myProfileManager.myProfile.nickname)
                    .largeBold()
                    .padding(.bottom, 6)
                Spacer()
                Menu {
                    NavigationLink(Titles.settings) {
                        SettingView()
                            .navigationTitle(Titles.settings)
                    }
                } label: {
                    Image(systemName: "ellipsis")
                        .frame(width: 32, height: 32)
                }
            }
            
            Text(myProfileManager.myProfile.statement)
                .regular()
            HStack {
                HStack(spacing: 40) {
                    VStack(spacing: 8){
                        Text(String(myProfileManager.myProfile.followerCount))
                            .largeBold()
                        Text(Titles.followerTitle)
                            .regular()
                    }
                    VStack(spacing: 8){
                        Text(String(myProfileManager.myProfile.followingCount))
                            .largeBold()
                        Text(Titles.followingTitle)
                            .regular()
                    }
                }
                .sheet(isPresented: $isPresented) {
                    CustomSheetView(isPresented: $isPresented) {
                        FollowList()
                    }
                }
                .onTapGesture {
                    isPresented = true
                }
                Spacer()
                LoadProfileImage(myProfileManager.myProfile.profileImageUrl, size: 60)
                    .overlay(Circle().stroke(Color.lightGray, lineWidth: 1))
            }
            .padding(.top, 16)
        }
        .padding(.vertical, 20)
    }
}

struct ProfileInfoView_Previews: PreviewProvider {
    static var previews: some View {
        MyProfileInfoView()
    }
}
