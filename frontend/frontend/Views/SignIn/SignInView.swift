//
//  SignInView.swift
//  frontend
//
//  Created by Katherine JANG on 5/17/23.
//

import Foundation
import SwiftUI
import Alamofire
import AuthenticationServices
import KakaoSDKCommon
import KakaoSDKAuth
import KakaoSDKUser
import GoogleSignIn
import GoogleSignInSwift
import NaverThirdPartyLogin


struct SocialSignInView: View {
    @StateObject var path = PathViewModel()
    @ObservedObject var authManager = AuthenticationManager.shared
	

    var body: some View {
        NavigationStack {
            if authManager.isSignUp {
                OnboardingView()
                    .transition(.opacity)
            } else {
                SignInView()
            }
        }
        .animation(.easeInOut(duration: 0.5), value: authManager.isSignUp)
    }
}

struct SignInView: View {
    @Environment(\.colorScheme) var colorScheme
	@State var showTermsAndPolicyView: Bool = false

    var body: some View {
        VStack {
            Text(Titles.appTitle)
                .serif(24)
            TypingAnimationView(text: Titles.greetings.randomElement() ?? "함께 쓰는 매일이 특별한 추억이 됩니다.")
            VStack {
				AppleSigninButton(showTermsAndPolicyView: $showTermsAndPolicyView)
                    .frame(width: 194, height: 44)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .inset(by: 1)
                            .stroke(colorScheme == .dark ? Color.white.opacity(0.5): Color.clear)
                    )
                GoogleSigninButton()
                KakaoSigninButton()
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .inset(by: 2)
                            .stroke(Color.black.opacity(0.1))
                    )
                NaverSigninButton()
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .inset(by: 2)
                            .stroke(Color.black.opacity(0.1))
                    )
            }
            .padding(.top, 140)
        }
		.fullScreenCover(isPresented: $showTermsAndPolicyView) {
			TermsAndPoliciesView()
		}
    }
}

struct AppleSigninButton: View {
    let authManager = AuthenticationManager.shared
	@Binding var showTermsAndPolicyView: Bool
    
    var body: some View {
        SignInWithAppleButton(onRequest: {request in
            request.requestedScopes = [.fullName, .email]
        }, onCompletion: {result in
            switch result {
            case .success(let authResults):
                switch authResults.credential {
                case let appleIDCredential as ASAuthorizationAppleIDCredential:
                    guard let authCode = String(data: appleIDCredential.authorizationCode!, encoding: .utf8) else { return }
					if let _ = appleIDCredential.fullName, let _ = appleIDCredential.email {
						//회원가입 시
						authManager.setUserAuth(valid: authCode, oauthType: "APPLE")
						self.showTermsAndPolicyView = true
					} else {
						//최초 가입 이후 로그인 시
						authManager.signIn(idToken: authCode, oauthType: "APPLE")
					}
                default:
                    break
                }
            case .failure(let error):
                print(error)
            }
        })
    }
}

struct KakaoSigninButton: View {
    let authManager = AuthenticationManager.shared
    
    var body: some View {
        Button {
            if UserApi.isKakaoTalkLoginAvailable() {
                UserApi.shared.loginWithKakaoAccount{(oauthToken, error) in
                    guard let idToken = oauthToken?.idToken else { return }
                    authManager.signIn(idToken: idToken, oauthType: "KAKAO")
                }
            } else {
                UserApi.shared.loginWithKakaoAccount{ (oauthToken, error) in
                    guard let idToken = oauthToken?.idToken else { return }
                    authManager.signIn(idToken: idToken, oauthType: "KAKAO")
                }
            }
        } label: {
            Image("SignInKakao")
        }
    }
}

struct GoogleSigninButton: View {
    let authManager = AuthenticationManager.shared
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        Button {
            guard let presentingViewController = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.rootViewController else {return}
            GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { (signInResult, error) in
                guard let res = signInResult else {
                    // 유저가 google로 로그인 선택 후에 웹뷰 닫으면 해당 분기로 처리됨
                    print(error as Any)
                    return
                }
                let idToken = res.user.idToken?.tokenString ?? "wrong"
                authManager.signIn(idToken: idToken, oauthType: "GOOGLE")
            }
        } label: {
            ZStack {
                Image("SignInGoogle")
                RoundedRectangle(cornerRadius: 8)
                    .stroke(colorScheme == .light ? Color.defaultBlack.opacity(0.2) : Color.clear)
                    .foregroundColor(.clear)
                    .frame(width: 194, height: 44)
            }
        }
    }
}

struct NaverSigninButton: View {
    let authManager = AuthenticationManager.shared
    
    var body: some View {
        Button{
            NaverThirdPartyLoginConnection.getSharedInstance().delegate = authManager.self
            NaverThirdPartyLoginConnection
                .getSharedInstance()
                .requestThirdPartyLogin()
        } label: {
            Image("SignInNaver")
        }
    }
}
