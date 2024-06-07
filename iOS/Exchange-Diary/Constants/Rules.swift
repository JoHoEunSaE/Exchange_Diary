//
//  ValidationRule.swift
//  frontend
//
//  Created by 신인호 on 1/16/24.
//

import Foundation

struct Rules {
    // 프로필
    static let nicknameRegex = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9-_]+$"                        // 닉네임 정규식
    static let nicknameMinLength = 1                                                // 닉네임 최소 길이
    static let nicknameMaxLength = 15                                               // 닉네임 최대 길이
    static let nicknameLengthRange = (nicknameMinLength...nicknameMaxLength)
    static let statementMaxLength = 63                                              // 상태 메시지 길이
    
    // 일기장
    static let diaryTitleMaxLength = 31                                             // 일기장 제목 길이
    static let groupNameMaxLength = 15                                              // 그룹 이름 길이
    
    // 일기
    static let noteTitleMaxLength = 63                                              // 일기 제목 길이
    static let noteContentMaxLength = 4095                                          // 일기 내용 길이
    static let noteContentPadding = "\n\n\n\n"                                          // 일기 내용 높이 조정

    // 신고
    static let reportMaxLength = 255                                                // 신고 내용 길이
}
