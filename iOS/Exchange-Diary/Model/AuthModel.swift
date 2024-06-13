//
//  AuthModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/7/24.
//

import Foundation

struct UserAuthenticationInfo: Codable {
    var valid: String
    var oauthType: String
    var deviceToken: String
    var appleOauth: Bool?
    var unregisterReason: String?

    init() {
        self.valid = keychain.get("AccessToken") ?? "Invalid AccessToken"
        self.oauthType = UserDefaults.standard.string(forKey: "OauthType") ?? "Invalid Type"
        self.deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") ?? "Invalid deviceToken"
        self.appleOauth = self.oauthType == "APPLE"
    }
}
