//
//  UserModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 7/31/23.
//

import Foundation

//get
struct ProfileModel: Codable {
    var memberId: Int
    var nickname: String
    var statement: String
    var profileImageUrl: String?
    var profileImageData: Data?
    var followerCount: Int
    var followingCount: Int
    var isFollowing: Bool
    var email: String
    
    init() {
        memberId = 0
        nickname = ""
        statement = "안녕하세요."
        profileImageUrl = ""
        followerCount = 0
        followingCount = 0
        isFollowing = false
        email = "good42@good.com"
    }
}

//유저 목록 내에 필요한 유저 정보 구조체 -> 유저 디테일 프로필 끌어오려면 memberId 필요
struct ProfilePreview: Codable {
    var memberId: Int
    var nickname: String
    var profileImageUrl: String
    var following: Bool
}

struct BlockModel: Codable {
    var blockedUserId: Int
    var nickname: String
    var profileImageUrl: String?
}

struct BlockListModel: Codable {
    var result: [BlockModel]
    var totalLength: Int
}

struct MemberModel: Codable, Hashable {
    var memberId: Int
    var nickname: String
    var profileImageUrl: String?
    var isFollowing: Bool
    var isMaster: Bool
}

//post

struct UploadProfile: Codable {
    var nickname: String
    var statement: String
    var profileImageUrl: String?
}

struct UploadProfileText: Codable {
    var nickname: String
    var statement: String
}
