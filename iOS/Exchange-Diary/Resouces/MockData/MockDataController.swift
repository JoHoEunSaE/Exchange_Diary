////
////  MockDataController.swift
////  Exchange-Diary
////
////  Created by Katherine JANG on 8/2/23.
////
//
//import Foundation
//
//
//class MockDataController: ObservableObject {
//    @Published var note: NoteModel
//    @Published var diaires: [DiaryModel]
//
//    init() {
//        self.note = NoteModel(note: NoteContentModel(title: "title", content: "content", author: Author(memberId: 0, profileImgUrl: "", nickname: ""), images: [], createdAt: "", updatedAt: "", visibleScope: ""), isBookmarked: false, isLiked: false, reactionCount: 0, nextNoteId: 0, prevNoteId: 0)
//        self.diaires = []
//    }
//
//    func getNoteData() {
//        guard let note = MockParser.load(NoteModel.self, from: "NoteData") else {
//            print("note decoding failed")
//            return
//        }
//        self.note = note
//    }
//
//    func getProfileDetail() {
//        guard let userInfo  = MockParser.load(ProfileDetail.self, from: "UserData")else {
//            print("profileInfo decoding failed")
//            return
//        }
//    }
//
//    func getDiaryList() {
//        guard let diaryList = MockParser.load([DiaryModel].self, from: "DiaryData") else {
//            print("diaryList decoding failed")
//            return
//        }
//    }
//
//
//
//}
