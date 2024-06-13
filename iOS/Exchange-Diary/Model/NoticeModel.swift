//
//  NoticeModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 8/1/23.
//

import Foundation

enum NoticeType: String, Codable {
    case announcement = "ANNOUNCEMENT"          // "%s", "%s" 공지사항의 경우 별도로 제목을 설정하여 사용합니다.
//    case newNote = "DIARY_NOTE_FROM_TO"        // "새 일기", "%member님이 %diary에 일기를 남겼습니다."
    case newMember = "DIARY_MEMBER_FROM_TO"     // "새 멤버", "%member님이 %diary에 가입하셨습니다."
//    case like = "NOTE_LIKE_FROM_TO"             // "일기 좋아요", "%member님이 회원님의 %note를 좋아합니다."
    case kick = "DIARY_MEMBER_KICK_FROM"        // "일기장 추방", "%member 일기장에서 추방되었습니다.",
    case masterChange = "DIARY_MASTER_CHANGED_TO" // "방장 변경", "%member님이 %diary의 방장이 되었습니다."
    case follow = "FOLLOW_CREATE_FROM"          // "새 팔로우", "%member님이 회원님을 팔로우했습니다."
}

struct Notice: Codable, Equatable {
    let id: Int
    let noticeType: NoticeType
    let title: String
    let content: String
    let createdAt: Date
}
