//
//  FollowList.swift
//  frontend
//
//  Created by Katherine JANG on 6/22/23.
//

import SwiftUI

struct FollowList: View {
    @State var isFollowing:Bool  = true
    var body: some View {
            VStack {
                HStack {
                    Button {
                        isFollowing = true
                    } label: {
                        VStack {
                            Text(Titles.followerTitle)
                                .regularBold()
                                .foregroundColor(Color.accentColor)
                                .fontWeight(isFollowing ? .semibold : .light)
                            Divider()
                                .frame(minHeight: 1.5)
                                .overlay(isFollowing  ? Color.accentColor : Color.sheet)
                        }
                    }
                    Button {
                        isFollowing = false
                    } label: {
                        VStack {
                            Text(Titles.followingTitle)
                                .regularBold()
                                .foregroundColor(Color.accentColor)
                                .fontWeight(!isFollowing ? .semibold : .light)
                            Divider()
                                .frame(minHeight: 1.5)
                                .overlay(!isFollowing  ? Color.accentColor : Color.sheet)
                        }
                    }
                    
                }.padding(20)
                ScrollView{
//                    DiaryMemberView()
                }
            }
    }
}

struct FollowList_Previews: PreviewProvider {
    static var previews: some View {
        FollowList()
    }
}
