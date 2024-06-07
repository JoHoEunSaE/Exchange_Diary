//
//  DiaryModel.swift
//  frontend
//
//  Created by Katherine JANG on 7/31/23.
//

import Foundation


struct DiaryInfoModel: Codable, Hashable {
    var diaryId: Int?
    var coverType: CoverType = .color
    var coverData: String = "#FFFFFFFF"
    var title: String = ""
    var groupName: String?
    var imageData: String?
    var masterMemberId: Int?
    var createdAt: String?
}

enum CoverType: String, Codable {
    case color = "COLOR"
    case image = "IMAGE"
}

struct ErrorReason: Codable {
    var statusCode: Int
    var code: String
    var message: String
}

struct ErrorJson : Codable {
    var success: Bool
    var errorReason: ErrorReason
    var timeStamp: String
}

struct InvitationCode: Codable {
    var invitationCode: String
    var expiredAt: String
}
