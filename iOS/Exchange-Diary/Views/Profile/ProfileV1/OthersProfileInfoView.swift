//
//  OthersProfileInfoView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/07/12.
//

import SwiftUI

struct OthersProfileInfoView: View {
    @State private var isFollowing = false
    @State private var isBlocked = true
    @State private var isPresented = false
    
    var body: some View {
        VStack(alignment: .leading){
            Text(Titles.user)
                .largeBold()
                .padding(.bottom, 6)
            Text(Titles.otherProfileStatement)
                .regular()
            HStack {
                HStack(spacing: 40) {
                    VStack(spacing: 8){
                        Text("12")
                            .largeBold()
                        Text(Titles.followerTitle)
                            .regular()
                    }
                    VStack(spacing: 8){
                        Text("24")
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
                Image("sampleImage")
                    .resizable()
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.lightGray, lineWidth: 1))
                    .frame(width: 60, height: 60)
            }
            .padding(.vertical, 16)
            Button {
                if isBlocked {
                    isBlocked.toggle()
                } else {
                    isFollowing.toggle()
                }
            } label: {
                Text(isBlocked ? Titles.unblockMessage : isFollowing ? Titles.followingTitle : Titles.followerTitle)
                    .frame(height: 30)
                    .frame(maxWidth: .infinity)
                    .small()
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .background(isBlocked ? Color.defaultGray : isFollowing ? Color.defaultGray : Color.defaultBlack)
                    .cornerRadius(8)
            }
        }
        .padding(.vertical, 20)
    }
}

struct OthersProfileInfoView_Previews: PreviewProvider {
    static var previews: some View {
        OthersProfileInfoView()
    }
}
