//
//  PaginationManager.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/18/24.
//

import Foundation

struct PaginationModel<T: Codable>: Codable {
    let result: [T]
    let totalLength: Int
}

class PaginationManager: ObservableObject {

    var currentPage: Int {
        currentLength / pageSize
    }
    var urlPath: String
    let pageSize: Int
    var currentLength: Int
    var totalLength: Int
    
    init(pageSize: Int = 10, currentLength: Int = 0, totalLength: Int = 99) {
         self.urlPath = ""
         self.pageSize = pageSize == 0 ? 999 : pageSize
         self.currentLength = currentLength
         self.totalLength = totalLength
     }
    
    func isThreshold(_ index: Int) -> Bool {
        return (index + 1) % pageSize == 0
    }
    
    private func getNewListByPage<T>(_ urlPath: String) async throws -> [T] where T:Codable {
        let param = ["page" : self.currentPage, "size": self.pageSize] as [String : Any]
        let paginationList = try await getJsonAsync(urlPath, param, type: PaginationModel<T>.self)
        self.totalLength = paginationList.totalLength
        let newList  = paginationList.result
        self.currentLength += newList.count

        return newList
    }
    
    @MainActor
    func getNewListIfPossible<T>(_ urlPath: String) async -> [T] where T: Codable {
        var newList: [T] = []
        do {
            if currentLength < totalLength {
                newList = try await getNewListByPage(urlPath)
            }
        } catch {
            AlertManager.shared.setError(error as? ResponseErrorData)
        }
        return newList
    }
    
    func reset() async {
        self.urlPath = "urlPath"
        self.currentLength = 0
        self.totalLength = 1
    }
}


