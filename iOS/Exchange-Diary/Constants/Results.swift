//
//  Results.swift
//  Exchange-Diary
//
//  Created by 신인호 on 11/6/23.
//

import Foundation

enum AlertActionResult {
    case dismiss
    case show(ResponseErrorData)
}

enum ProfileEditResult {
    case success
    case failure(ResponseErrorData)
}
