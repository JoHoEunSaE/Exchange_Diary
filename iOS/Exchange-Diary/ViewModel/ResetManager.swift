//
//  ResetManager.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 2/16/24.
//

/*
 우선 resetManager라는 이름이 조금 맘에 안들고.. 그렇다고 핸들러를 따로 안 빼고 authManager에 넣기에는
 목적이 조금 다른 것 같아 우선 분리하지만 이름 추천 받습니다
 */
 
import Foundation
import SwiftUI
import KeychainSwift

/*
 초기화 해야할 목록
 - Keychain(token)
 - UserDefaults (OauthType)
 - shared instance (diaryList, blockList, userProfile, noticeList)
 */

class ResetManager {
    static let shared = ResetManager()
    
    private init() {}
    
    func resetData() {
        keychain.set("", forKey: "AccessToken")
        UserDefaults.standard.set("", forKey: "OauthType")
        DiaryListManager.shared.reset()
        BlockManager.shared.reset()
        MyProfileManager.shared.reset()
        NoticeManager.shared.reset()
    }
}
