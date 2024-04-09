//
//  SingIn.swift
//  frontend
//
//  Created by Katherine JANG on 8/30/23.
//

import Foundation
import Alamofire
import KeychainSwift
import NaverThirdPartyLogin
import KakaoSDKUser
import KakaoSDKCommon
import KakaoSDKAuth

let keychain = KeychainSwift()

class SignInViewModel: NSObject, ObservableObject {
    @Published var isSignUp: Bool = false
    
    func signIn(idToken: String, oauthType: String) {
        let url = (Bundle.main.infoDictionary?["API_URL"] as? String ?? "wrong") + "/v1/auth/login"
        let deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") ?? "InvalidToken"
        let params = ["valid": idToken, "oauthType": oauthType, "deviceToken": deviceToken, "appleOauth" : oauthType == "APPLE"] as [String : Any]
        
        AF.request(url, method: .post, parameters: params, encoding: JSONEncoding(options: []), headers: [])
            .responseDecodable(of: SignInResult.self) { response in
                switch response.result {
                case .success(_) :
                    if response.response?.statusCode == 201 {
                        self.isSignUp = true
                    } else {
                        self.isSignUp = false
                        UserDefaults.standard.set(true, forKey: "isSignIn")
                    }
                    guard let token = response.value?.accessToken else { return }
                    self.saveTokenToKeychain(token: token)
                case .failure(_) :
                    let statusCode = response.response?.statusCode ?? 0
                }
            }
    }
    
    func saveTokenToKeychain(token: String) {
        keychain.set(token, forKey: "AccessToken")
    }
}

extension SignInViewModel: UIApplicationDelegate, NaverThirdPartyLoginConnectionDelegate {
    func oauth20ConnectionDidFinishRequestACTokenWithRefreshToken() {}
    
    func oauth20ConnectionDidFinishDeleteToken() {}
    
    func oauth20Connection(_ oauthConnection: NaverThirdPartyLoginConnection!, didFailWithError error: Error!) {}
    
    func oauth20ConnectionDidFinishRequestACTokenWithAuthCode() {
        var accessToken: String = ""
        accessToken = NaverThirdPartyLoginConnection().accessToken
        self.signIn(idToken: accessToken, oauthType: "NAVER")
    }
    
    func getUserInfoFromNaver(accessToken: String) {}
}


func getAccessToken() -> String? {
    let token = keychain.get("AccessToken")
    return token
}
