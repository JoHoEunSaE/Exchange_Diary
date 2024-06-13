//
//  DiaryViewModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 11/28/23.
//

import Foundation
import SwiftUI
import Alamofire

class DiaryViewModel: ObservableObject {
    @Published var diaryInfo: DiaryInfoModel {
        didSet {
            checkMaster()
        }
    }
    @Published var membersInfo: [MemberModel]
    @Published var noteList: [NotePreviewModel]
    @Published var invitationCode: String
    @Published var isLoading = true
    var paginationManager = PaginationManager()
    let diaryId: Int
    var isMaster: Bool
    
    init(diaryId: Int) {
        self.diaryInfo = DiaryInfoModel()
        self.membersInfo = []
        self.noteList = []
        self.invitationCode = "InvitationCode"
        self.diaryId = diaryId
        self.isMaster = false
        Task {
            await setDiary()
        }
    }
    init() {
        self.diaryInfo = DiaryInfoModel()
        self.membersInfo = []
        self.noteList = []
        self.invitationCode = "InvitationCode"
        self.diaryId = -1
        self.isMaster = false
    }
    
    func checkMaster() {
        self.isMaster = diaryInfo.masterMemberId == MyProfileManager.shared.myProfile.memberId
    }
    
    //다이어리 정보 조회
    @MainActor
    func getDiaryInfo() throws{
        let urlPath = "/v1/diaries/\(self.diaryId)/members/me"
        Task {
            let diary = try await getJsonAsync(urlPath, type: DiaryInfoModel.self)
            self.diaryInfo = diary
            checkMaster()
        }
    }
    
    //다이어리 멤버 조회
    @MainActor
    func getDiaryMembers() throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/members"
        Task {
            let members = try await getJsonAsync(urlPath, type: [MemberModel].self)
            self.membersInfo = members
            print("members: ", members)
        }
    }
    
    //다이어리 초대 코드 조회
    @MainActor
    func getInvitationCode() throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/invitation"
        Task {
            let code = try await getJsonAsync(urlPath, type: InvitationCode.self)
            self.invitationCode = code.invitationCode
        }
    }
    
    //노트 목록 조회
    @MainActor
    func getNoteList() {
        let urlPath =  "/v1/diaries/\(diaryId)/notes"
        Task {
            let newList: [NotePreviewModel]  = await paginationManager.getNewListIfPossible(urlPath)
            self.noteList.append(contentsOf: newList)
        }
    }
    
    
    //다이어리 나가기
    func leaveDiary() async throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/members/me"
        Task {
            try await deleteJsonAsync(urlPath)
            await DiaryListManager.shared.updateHomeView()
        }
    }
    
    //다이어리 삭제 (주인 권한)
    func deleteDiary() async throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)"
        Task {
            try await deleteJsonAsync(urlPath)
            await DiaryListManager.shared.updateHomeView()
        }
    }
    
    //멤버 추방 (주인 권한)
    @MainActor
    func deleteMemberInDiary(memberId: Int) throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/members/\(memberId)"
        Task {
            do {
                try await deleteJsonAsync(urlPath)
                guard let removeIndex = membersInfo.firstIndex(where: { $0.memberId == memberId }) else {
                    return
                }
                self.membersInfo.remove(at: removeIndex)
            }
        }
    }
    
    //일기 뜯어내기
    func removeNote(noteId: Int){
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/notes/\(noteId)"
        Task {
            _ = try await patchJsonAsync(urlPath, type: Empty.self)
        }
    }
    
    // 일기장 주인 변경
    func changeMasterOfDiary(targetMemeberId: Int) async throws {
        let diaryId = self.diaryId
        let urlPath = "/v1/diaries/\(diaryId)/master/members/\(targetMemeberId)"
        Task {
            _ = try await patchJsonAsync(urlPath, type: Empty.self)
        }
    }
    
    //다이어리 편집
    @MainActor
    func editDiary(_ editedDiary: DiaryInfoModel, imageData: Data?) async throws {
        let imageUploadManager = ImageUploadManager.shared
        let diaryId = editedDiary.diaryId ?? 0
        let urlPath =  "/v1/diaries/\(diaryId)"
        let diaryInfo = DiaryInfoModel(coverType: editedDiary.coverType,
                                       coverData: editedDiary.coverData,
                                       title: editedDiary.title,
                                       groupName: editedDiary.groupName)
        if diaryInfo.coverType == .image && imageData != nil {
            try await ImageUploadManager.shared.uploadImage(imageData: imageData, imagePath: diaryInfo.coverData)
        }
        guard let newDiaryInfo = try await patchJsonAsync(urlPath, diaryInfo, type: DiaryInfoModel.self) else {
            return
        }
        self.diaryInfo = newDiaryInfo
        await DiaryListManager.shared.updateHomeView()
    }
    
    // 다이어리 가져오기
    @MainActor
    func setDiary() {
        Task {
            do {
                try self.getDiaryInfo()
                try self.getDiaryMembers()
                if !self.noteList.isEmpty {
                    self.noteList.removeAll()
                }
                self.getNoteList()
                await paginationManager.reset()
                if isLoading {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        self.isLoading = false
                    }
                }
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
}
