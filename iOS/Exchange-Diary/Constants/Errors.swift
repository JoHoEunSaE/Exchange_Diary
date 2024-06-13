//
//  Errors.swift
//  Exchange-Diary
//
//  Created by 신인호 on 10/22/23.
//

import Foundation

enum APIError: Error {
    case responseError
    case transportError
    case decodingError
}

/// 응답 실패 시 받는 데이터
struct ResponseErrorData: Decodable, Error {
    var statusCode: Int = 0
    var code: String = "TEST"
    var message: String = "TEST MESSAGE"
}

struct Errors {
    static let mailFailure = ResponseErrorData(statusCode: 900, code: "MAIL_FAILURE", message: "이메일 설정을 확인하고 다시 시도해주세요.")
}
