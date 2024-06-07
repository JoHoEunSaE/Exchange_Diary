//
//  BlockManager.swift
//  frontend
//
//  Created by 김나연 on 1/21/24.
//

import Foundation
import Alamofire

final class BlockManager: ObservableObject {
    static let shared = BlockManager()
    @Published var blockedList = [BlockModel]()
    var paginationManager = PaginationManager()

    private init() {
        Task {
            await self.setList()
        }
    }
    
    @MainActor
    func setList() {
         Task {
             self.blockedList = []
             await paginationManager.reset()
             getList()
         }
     }

    @MainActor
    func getList() {
        let urlPath = "/v1/blocks"
        
        Task {
            let newList: [BlockModel]  = await paginationManager.getNewListIfPossible(urlPath)
            self.blockedList.append(contentsOf: newList)
        }
    }

    func blockUser(memberId: Int) {
        let urlPath = "/v1/blocks/\(memberId)"

        Task {
            do {
                _ = try await postJsonAsync(urlPath, type: Empty.self)
                await setList()
            } catch {
                print(error)
            }
        }
    }

    func unblockUser(memberId: Int) {
        let urlPath = "/v1/blocks/\(memberId)"

        Task {
            do {
                _ = try await deleteJsonAsync(urlPath)
                await setList()
            } catch {
                print(error)
            }
        }
    }

    func isBlocked(memberId: Int) -> Bool {
        blockedList.map { $0.blockedUserId }.contains(memberId)
    }
    
    func reset() {
        self.blockedList.removeAll()
    }
}
