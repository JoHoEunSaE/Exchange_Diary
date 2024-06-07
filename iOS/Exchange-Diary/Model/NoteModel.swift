//
//  NoteModel.swift
//  frontend
//
//  Created by Katherine JANG on 7/31/23.
//

import Foundation

struct DiaryNoteIdentifier: Hashable {
    var noteId: Int
    var diaryId: Int
}

//noteContent 내에서 필요한 구조체 author, images
struct Author: Codable, Hashable {
    var memberId: Int
    var nickname: String
    var profileImageUrl: String?

    init() {
        self.memberId = -1
        self.nickname = "조은사이"
        self.profileImageUrl = ""
    }
}

struct Images: Codable {
    var imageIndex: Int
    var imageUrl: String?

    init() {
        self.imageIndex = 0
        self.imageUrl = ""
    }
}

struct CreateNoteModel: Codable {
    var title: String = "title"
    var content: String = "content"
    var imageUrls: [String] = []
    var visibleScope: String = "PRIVATE"
}

struct NoteContentModel: Codable {
    var title: String
    var content: String
    var author: Author
    var imageList: [Images]
    var createdAt: String
    var updatedAt: String
    var visibleScope: String

    init() {
        self.title = ""
        self.content = ""
        self.author = Author()
        self.imageList = []
        self.createdAt = "2023.12.08"
        self.updatedAt = "2023.12.08"
        self.visibleScope = "PUBLIC"
    }
}

struct NoteModel: Codable {
    var noteId: Int
    var diaryId: Int
    var title: String
    var content: String
    var author: Author
    var imageList: [Images]
    var createdAt: String
    var updatedAt: String
    var likeCount: Int
    var nextNoteId: Int?
    var prevNoteId: Int?
    var isLiked: Bool
    var isBookmarked: Bool
    var isBlocked: Bool
    
    init() {
        self.noteId = 0
        self.diaryId = 0
        self.title = ""
        self.content = "content"
        self.author = Author()
        self.imageList = []
        self.createdAt = "2024.01.01"
        self.updatedAt = "2024.01.02"
        self.likeCount = 0
        self.nextNoteId = 2
        self.prevNoteId = 3
        self.isLiked = false
        self.isBookmarked = false
        self.isBlocked = false
    }
}

// -> diary내 NoteList
struct NotePreviewContent: Codable {
    var result: [NotePreviewModel]
    var totalLength: Int
}

//해당 noteID에 해당하는 note 열람
struct NoteDetail: Codable {
    var noteId: Int
    var title: String
    var content: String
    var author: Author
    var imageList: [Images]
    var createdAt: String
    var updatedAt: String
    var likeCount: Int
    var visibleScope: String
    var liked: Bool
    var bookmarked: Bool
}

// 본인 NoteList에서 사용, notePReviewContent
struct NotePreviewModel: Codable, Hashable {
    var noteId: Int
    var diaryId: Int
    var author: Author?
    var title: String
    var preview: String
    var thumbnailUrl: String?
    var createdAt: String
    var visibleScope: String
    var groupName: String?
    var hasRead: Bool?
    var likeCount: Int
    var isBlocked: Bool
}

struct RecentNotePreviewModel: Codable, Hashable {
    var diaryId: Int
    var noteId : Int
    var diaryTitle: String
    var groupName: String?
    var author: Author
    var preview: String
    var title: String
    var thumbnailUrl: String?
    var createdAt: String
    var visibleScope: String
}

let welcomeNote: RecentNotePreviewModel = RecentNotePreviewModel(
    diaryId: -1,
    noteId: -1, 
    diaryTitle: "공유일기",
    groupName: "조은사이",
    author: Author(),
    preview: "안녕하세요. 조은사이 팀의 따뜻한 일기장에 오신 것을 환영합니다. 서로의 감정을 나누고, 이해하며, 소중한 시간을 만들어보세요. 당신의 생각을 기록하고, 경험을 이야기하며, 친구들과 함께 서로의 이야기에 공감하는 시간을 가져보세요. 각각의 감정이 모여 하나의 아름다운 책이 쓰여지길 응원합니다.",
    title: "첫 일기를 써볼까요",
    thumbnailUrl: "",
    createdAt: "2023-10-24T10:20:10.447Z",
    visibleScope: "PUBLIC"
)
