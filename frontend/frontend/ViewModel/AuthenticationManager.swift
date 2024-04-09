//
//  AuthenticationManager.swift
//  frontend
//
//  Created by Katherine JANG on 11/21/23.
//

import Foundation
import SwiftUI
import Alamofire
import KeychainSwift
import KakaoSDKCommon
import KakaoSDKAuth
import KakaoSDKUser
import GoogleSignIn
import GoogleSignInSwift
import NaverThirdPartyLogin

let keychain = KeychainSwift()

class AuthenticationManager: NSObject, ObservableObject {
    static let shared = AuthenticationManager()
    var userAuth: UserAuthenticationInfo
    @Published var isSignUp: Bool
    
    private override init() {
        self.userAuth = UserAuthenticationInfo()
        self.isSignUp = false
        super.init()
    }
	
	func setUserAuth(valid: String, oauthType: String) {
		self.userAuth.valid = valid
		self.userAuth.oauthType = oauthType
		self.userAuth.appleOauth = oauthType == "APPLE"
		self.userAuth.deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") ?? "InvalidToken"
	}
    
    func signIn(idToken: String, oauthType: String) {
        let url = (Bundle.main.infoDictionary?["API_URL"] as? String ?? "wrong") + "/v1/auth/login"
        let deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") ?? "InvalidToken"
        let param = ["valid": idToken, "oauthType": oauthType, "deviceToken": deviceToken, "appleOauth" : oauthType == "APPLE"] as [String : Any]
        
        AF.request(url, method: .post, parameters: param, encoding: JSONEncoding(options: []), headers: [])
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
                    self.userAuth.oauthType = oauthType
                    self.userAuth.valid = idToken
                    UserDefaults.standard.setValue(oauthType, forKey: "OauthType")
                case .failure(let error) :
                    print(error)
                    let statusCode = response.response?.statusCode ?? 0
                }
            }
    }
	
	func signInWithoutParam() {
		let url = (Bundle.main.infoDictionary?["API_URL"] as? String ?? "wrong") + "/v1/auth/login"
		let deviceToken = UserDefaults.standard.string(forKey: "DeviceToken") ?? "InvalidToken"
		let param = ["valid": userAuth.valid, "oauthType": userAuth.oauthType, "deviceToken": deviceToken, "appleOauth" : userAuth.appleOauth] as [String : Any]
		
		AF.request(url, method: .post, parameters: param, encoding: JSONEncoding(options: []), headers: [])
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
					UserDefaults.standard.setValue(self.userAuth.oauthType, forKey: "OauthType")
				case .failure(let error) :
					print(error)
					let statusCode = response.response?.statusCode ?? 0
				}
			}
	}
    
    func saveTokenToKeychain(token: String) {
        keychain.set(token, forKey: "AccessToken")
    }
    
    func getReissuedRefreshTokenKakao()  {
        self.userAuth.unregisterReason = ""
        self.userAuth.appleOauth = false
            AuthApi.shared.refreshToken { (info, error) in
                guard error == nil else { return }
                self.userAuth.valid = info?.refreshToken ?? "invalidToken"
            }
    }
    
    func getReissuedRefreshTokenGoogle()  {
        guard let currentUser = GIDSignIn.sharedInstance.currentUser else { return }
        currentUser.refreshTokensIfNeeded{ (user, error) in
            guard error == nil else { return }
            guard let user = user else { return }
            self.userAuth.valid = user.accessToken.tokenString
        }
    }
    
    func getReissuedAccessTokenNaver() {
        let instance = NaverThirdPartyLoginConnection.getSharedInstance()
        self.userAuth.valid = instance?.accessToken ?? "invalidToken"
    }
    
    func signOutKakao() {
        if AuthApi.hasToken() {
            UserApi.shared.logout {error in
                guard error == nil else { return }
            }
        }
    }
    
    func signOutGoogle() {
        let instance = GIDSignIn.sharedInstance
        instance.signOut()
    }
    
    func signOutNaver() {
        let instance = NaverThirdPartyLoginConnection.getSharedInstance()
        instance?.resetToken()
    }
}


extension AuthenticationManager: UIApplicationDelegate, NaverThirdPartyLoginConnectionDelegate {
    func oauth20ConnectionDidFinishRequestACTokenWithRefreshToken() {
        var accessToken: String = ""
        accessToken = NaverThirdPartyLoginConnection().accessToken
        self.signIn(idToken: accessToken, oauthType: "NAVER")
    }
    
    func oauth20ConnectionDidFinishDeleteToken() {}
    
    func oauth20Connection(_ oauthConnection: NaverThirdPartyLoginConnection!, didFailWithError error: Error!) {}
    
    func oauth20ConnectionDidFinishRequestACTokenWithAuthCode() {
        var accessToken: String = ""
        accessToken = NaverThirdPartyLoginConnection().accessToken
        self.signIn(idToken: accessToken, oauthType: "NAVER")
    }
    
    func getUserInfoFromNaver(accessToken: String) {}
}

extension AuthenticationManager {
    @MainActor
    func signOut() {
        switch self.userAuth.oauthType{
        case "KAKAO":
            self.signOutKakao()
        case "NAVER":
            self.signOutNaver()
        case "GOOGLE":
            self.signOutGoogle()
        default:
            break
        }
        ResetManager.shared.resetData()
        UserDefaults.standard.set(false, forKey: "isSignIn")
    }
}

extension AuthenticationManager {
    func reissueToken() {
        switch self.userAuth.oauthType{
        case "KAKAO":
            self.getReissuedRefreshTokenKakao()
        case "NAVER":
            self.getReissuedAccessTokenNaver()
        case "GOOGLE":
            self.getReissuedRefreshTokenGoogle()
        default:
            break
        }
    }
    
    @MainActor
    func unregisterService(_ unregisterReason: String) {
        Task {
            do {
                self.userAuth.unregisterReason = unregisterReason
                self.userAuth.oauthType = UserDefaults.standard.string(forKey: "OauthType") ?? "invalid"
                self.userAuth.appleOauth = UserDefaults.standard.string(forKey: "OauthType") == "APPLE"
                _ = try await postJsonAsync("/v1/auth/unregister", self.userAuth , type: Empty.self)
                ResetManager.shared.resetData()
                UserDefaults.standard.set(false, forKey: "isSignIn")
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
}

func getAccessToken() -> String? {
    let token = keychain.get("AccessToken")
    return token
}
