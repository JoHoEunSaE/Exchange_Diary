//
//  DiaryListManager.swift
//  Exchange-Diary
//
//  Created by 김나연 on 1/6/24.
//

import Foundation
import Alamofire

final class DiaryListManager: ObservableObject {
    static let shared = DiaryListManager()
    @Published var diaryList: [DiaryInfoModel] = [] {
        didSet {
            if diaryList.isEmpty {
                self.selectedDiary = nil
            }
        }
    }
    @Published var selectedDiary: DiaryInfoModel?
    @Published var recentNotesPreview: [RecentNotePreviewModel] = []
    @Published var isLoading = true

    private init() { }

    @MainActor
    func updateHomeView() async {
        async let diaryList: () = self.getDiaryList()
        async let recentNotes: () = self.getRecentNotesPreview()
        _ = await (diaryList, recentNotes)

        if isLoading {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                self.isLoading = false
            }
        }
    }

    // MARK: diaryList
    @MainActor
    func getDiaryList() async {
        let urlPath = "/v1/diaries/members/me"

        do {
            self.diaryList = try await getJsonAsync(urlPath, type: [DiaryInfoModel].self)
        } catch {
            print(error)
            print("get diary List failed")
        }
    }

    @MainActor
    func createNewDiary(newDiary: DiaryInfoModel, image: Data?) async throws {
        let imageUploadManager = ImageUploadManager.shared
        let url = "/v1/diaries"

        if newDiary.coverType == .image {
            try await ImageUploadManager.shared.uploadImage(imageData: image, imagePath: newDiary.coverData)
        }
        guard let _ = try await postJsonAsync(url, newDiary, type: DiaryInfoModel.self) else {
            print("no new diary info")
            return
        }
        await self.getDiaryList()
    }

    @MainActor
    func addNewDiaryByCode(code: String) async {
        let url = "/v1/diaries/{diaryId}/invitation"
        let param = ["code" :  code]

        do {
            _ = try await postJsonAsync(url, param, type: String.self)
            await self.getDiaryList()
        } catch {}
    }

    // MARK: selected Diary
    func selectDiary(_ diary: DiaryInfoModel) {
        selectedDiary = diary
    }

    func deselectDiary() {
        selectedDiary = nil
    }

    // MARK: recentNotes
    @MainActor
    func getRecentNotesPreview() async {
        let urlPath = "/v1/diaries/members/me/new-notes"

        do {
            self.recentNotesPreview = try await getJsonAsync(urlPath, type: [RecentNotePreviewModel].self)
        } catch {
            print(error)
            print("get recent note preview failed")
            if recentNotesPreview.count == 0 {
                recentNotesPreview.append(welcomeNote)
            }
        }
        if recentNotesPreview.count == 0 {
            recentNotesPreview.append(welcomeNote)
        }
    }
    
    @MainActor
    func joinDiary(_ invitationCode: String, _ diaryId: Int) {
        let urlPath = "/v1/diaries/\(diaryId)/invitation"
        let param = ["code" : invitationCode]
        
        Task {
            do {
                guard let newDiary = try await postJsonAsync(urlPath, param, type: DiaryInfoModel.self) else {
                    return
                }
                self.diaryList.append(newDiary)
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
    
    func getDiaryInfoWithCode(_ invitationCode: String) async -> DiaryInfoModel? {
        let urlPath = "/v1/diaries/invitation"
        let param = ["code" : invitationCode]
        var diaryPreview = DiaryInfoModel()
        
        do {
            diaryPreview = try await postJsonAsync(urlPath, param, type: DiaryInfoModel.self) ?? DiaryInfoModel()
        } catch {
            let errorResponse = error as? ResponseErrorData
            if errorResponse?.statusCode == 400 {
                return nil
            } else {
                await AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
        return diaryPreview
    }
    
    func reset() {
        self.diaryList = []
        self.selectedDiary = nil
        self.recentNotesPreview = []
    }
}
