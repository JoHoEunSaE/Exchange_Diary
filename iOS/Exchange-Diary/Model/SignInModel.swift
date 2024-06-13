//
//  SignInModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 10/11/23.
//

import Foundation

struct SignInResult: Codable {
    let accessToken: String
}

struct AuthModel: Codable {
    let valid: String
    let oauthType: String
    let deviceToken: String
}
