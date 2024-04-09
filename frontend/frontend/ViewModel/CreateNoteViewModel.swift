//
//  CreateNoteViewModel.swift
//  frontend
//
//  Created by 신인호 on 2/1/24.
//

import Foundation
import SwiftUI
import PhotosUI
import Alamofire

class CreateNoteViewModel: ObservableObject {
    static let shared = CreateNoteViewModel()
    @Published var isCreateNotePresented = false

    private(set) var newNote = CreateNoteModel()
    
    private init() { return }
    
    private func setContent(title: String?, content: String?) {
        if let title {
            newNote.title = title
        }
        
        if let content {
            newNote.content = content
        }
    }
    
    private func setImage(image: PhotosPickerItem?, imageData: Data?) async throws {
        let imageManager = ImageUploadManager.shared
        
        guard let image, let imageData else {
            return
        }
        
        newNote.imageUrls.append(getImagePath(image.itemIdentifier ?? "noteImage", .note))
        try await imageManager.uploadImage(imageData: imageData, imagePath: newNote.imageUrls.first)
    }
    
    private func createNote() async throws {
        guard let selectedDiary = DiaryListManager.shared.selectedDiary else {
            return
        }
        
        let url = "/v1/diaries/\(selectedDiary.diaryId ?? 0)/notes"
        
        _ = try await postJsonAsync(url, newNote, type: Empty.self)
    }
    
    func clearContent() {
        newNote = CreateNoteModel()
    }
    
    func postNote(title: String?, content: String?, image: PhotosPickerItem?, imageData: Data?) async throws {
        setContent(title: title, content: content)
        try await setImage(image: image, imageData: imageData)
        try await createNote()
    }
}
