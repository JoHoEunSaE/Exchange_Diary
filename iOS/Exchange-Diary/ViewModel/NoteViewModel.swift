//
//  NoteViewModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 12/8/23.
//

import Foundation
import Alamofire

class NoteViewModel: ObservableObject {
    @Published var noteContent: NoteModel
    @Published var noteList: [NotePreviewModel]
    @Published var isContentBlocked: Bool
    @Published var isLoading = true
    var paginationManager = PaginationManager()
    var noteId: Int
    let diaryId: Int?
    
    init(noteId: Int, diaryId: Int?) {
        self.noteList = []
        self.noteContent = NoteModel()
        self.noteList = []
        self.noteId = noteId
        self.diaryId = diaryId
        self.isContentBlocked = false
        Task {
           await setNote()
        }
    }
    
    
    @MainActor
    func getSingleNote() async throws {
        let noteId = self.noteId
        let urlPath = "/v1/notes/\(noteId)"
        
        Task {
            do {
                let note = try await getJsonAsync(urlPath, type: NoteModel.self)
                self.noteContent = note
                self.noteId = note.noteId
                self.isContentBlocked = note.isBlocked
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
    
    @MainActor
    func getNoteInDiary(_ passedNoteId: Int? = nil) {
        let noteId = passedNoteId ?? self.noteId
        let diaryId: Int = self.diaryId ?? 0
        let urlPath = "/v1/diaries/\(diaryId)/notes/\(noteId)"
        
        Task {
            do {
                let note = try await getJsonAsync(urlPath, type: NoteModel.self)
                self.noteContent = note
                self.noteId = note.noteId
                self.isContentBlocked = note.isBlocked
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }

    @MainActor
    func getPrevNote() {
        let noteId = self.noteContent.prevNoteId ?? -1
        let diaryId = self.diaryId ?? -1
        let urlPath =  "/v1/diaries/\(diaryId)/notes/\(noteId)"
        
        Task {
            do {
                let note = try await getJsonAsync(urlPath, type: NoteModel.self)
                self.noteContent = note
                self.noteId = note.noteId
                self.isContentBlocked = note.isBlocked
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
    
    @MainActor
    func getNoteList() {
        let diaryId = self.diaryId ?? -1
        let urlPath =  "/v1/diaries/\(diaryId)/notes"
        Task {
            let newList: [NotePreviewModel]  = await paginationManager.getNewListIfPossible(urlPath)
            self.noteList.append(contentsOf: newList)
        }
    }
    
    @MainActor
    func getNextNote() {
        let noteId = self.noteContent.nextNoteId ?? -1
        let diaryId = self.diaryId ?? -1
        let urlPath =  "/v1/diaries/\(diaryId)/notes/\(noteId)"
        
        Task {
            do {
                let note = try await getJsonAsync(urlPath, type: NoteModel.self)
                self.noteContent = note
                self.noteId = note.noteId
                self.isContentBlocked = note.isBlocked
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
    
    @MainActor
    func deleteNote() async throws {
        let noteId = self.noteId
        let urlPath = "/v1/notes/\(noteId)"

        _ = try await deleteJsonAsync(urlPath)
    }
    
    @MainActor
    func likeNote() {
        let noteId = self.noteId
        let urlPath = "/v1/likes/\(noteId)"
        
        Task {
            do {
                print("ㅠㄷㄹㄱisLiked: ", self.noteContent.isLiked)
                _ = try await postJsonAsync(urlPath, type: Empty.self)
                self.noteContent.isLiked = true
                print("isLiked: ", self.noteContent.isLiked)
            } catch {
                print(error)
            }
        }
    }

    @MainActor
    func dislikeNote() {
        let noteId = self.noteId
        let urlPath = "/v1/likes/\(noteId)"
        
        Task {
            _ = try await deleteJsonAsync(urlPath)
            self.noteContent.isLiked = false
        }
    }
    
// TODO: 북마크 기능은 추후 버전에서 오픈 예정
/*
    @MainActor
    func bookmarkNote() {
        let noteId = self.noteId
        let urlPath = "/v1/bookmarks/\(noteId)"
        
        Task {
            _ = try await postJsonAsync(urlPath, type: Empty.self)
            self.noteContent.isBookmarked = true
        }
    }
   
    @MainActor
    func deleteBookmarkNote() {
        let noteId = self.noteId
        let urlPath = "/v1/bookmarks/\(noteId)"
        
        Task {
            _ = try await deleteJsonAsync(urlPath)
            self.noteContent.isBookmarked = false
        }
    }
 */
    
    @MainActor
    func editNote(_ newNote: NoteContentModel) {
        let noteId = self.noteId
        let urlPath = "/v1/notes/\(noteId)"
        
        Task {
            guard let editedNote = try await patchJsonAsync(urlPath, type: NoteModel.self) else {
                return
            }
            self.noteContent = editedNote
        }
    }
    
    func reportNote() {
        let noteId = self.noteId
        let urlPath = "/v1/report/notes/\(noteId)"
        
        Task {
            _ = try await postJsonAsync(urlPath, type: Empty.self)
        }
    }
    
    @MainActor
    func setNote() async {
        async let noteInDiary: () = getNoteInDiary()
        async let noteList: () = getNoteList()
        _ = await (noteInDiary, noteList)

        if isLoading {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                self.isLoading = false
            }
        }
    }
    
}
