//
//  NoticeManager.swift
//  frontend
//
//  Created by 신인호 on 1/14/24.
//

import Foundation
import Alamofire

final class NoticeManager: ObservableObject {
    static let shared = NoticeManager()
    
    @Published private(set) var notices: [Notice] = []
    private var pendingDeletionNoticeIds: [Int] = []
    var groupedNotices: [Date: [Notice]] {
        let sortedNotices = self.notices.sorted(by: { $0.createdAt > $1.createdAt })
        return Dictionary(grouping: sortedNotices) { notification in
            Calendar.current.startOfDay(for: notification.createdAt)
        }
    }
    
    private init() { }
    
    // MARK: 알림 요청
    // 알림 배열(notices)의 내용 다르면 갱신합니다.
    @MainActor
    func getNotices() async {
        let url = "/v1/notices"
        
        do {
            let fetchedNotices = try await getJsonAsync(url, type: [Notice].self)
            if fetchedNotices != notices {
                self.notices = fetchedNotices
            }
        } catch {
            print("[getNotices]", error)
        }
    }
    
    // MARK: 알림 삭제 대기
    // 알림뷰에서 알림 1개씩 삭제 후 삭제 대기열에 넣습니다.
    @MainActor
    func deleteNotice(_ notification: Notice) {
        self.notices.removeAll { $0.id == notification.id }
        pendingDeletionNoticeIds.append(notification.id)
    }
    
    // MARK: 알림 삭제
    // 백엔드에 요청해 삭제 대기열에 있는 알림들을 삭제합니다.
    func apiDeleteNotices() async {
        if pendingDeletionNoticeIds.isEmpty {
            return
        }

        let url = "/v1/notices/delete"
        let param = ["noticeIds" :  pendingDeletionNoticeIds]
        
        do {
            _ = try await postJsonAsync(url, param, type: Empty.self)
        } catch {
            print("[apiDeleteNotices]", error)
        }
    }
    
    func reset() {
        self.notices = []
        self.pendingDeletionNoticeIds = []
    }
}
