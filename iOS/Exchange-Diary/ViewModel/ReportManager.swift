//
//  ReportManager.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/16/24.
//

import Foundation
import Alamofire

class ReportManager: ObservableObject {
    static let shared = ReportManager()
    
    private init() {}
    
    func reportNote(noteId: Int, _ reportContent: ReportModel) async throws {
        let urlPath = "/v1/report/notes/\(noteId)"
        
        Task {
           try await postJsonAsync(urlPath, reportContent, type: Empty.self)
        }
    }
    
    func reportMember(memberId: Int, _ reportContent: ReportModel) async throws {
        let urlPath = "/v1/report/members/\(memberId)"
        
        Task {
            try await postJsonAsync(urlPath, reportContent, type: Empty.self)
        }
    }
    
}
