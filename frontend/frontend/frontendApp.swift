//
//  frontendApp.swift
//  frontend
//
//  Created by Katherine JANG on 4/1/23.
//

import SwiftUI
import Firebase
import FirebaseMessaging
import KakaoSDKCommon
import KakaoSDKAuth
import NaverThirdPartyLogin
import GoogleSignIn
import Security

@main
struct frontendApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDeleegate
    let persistenceController = PersistenceController.shared
    let kakaoAppKey = Bundle.main.infoDictionary?["KAKAO_APPKEY"] as? String ?? "WrongKey"
    
    init() {
        KakaoSDK.initSDK(appKey: kakaoAppKey)
        NaverThirdPartyLoginConnection.getSharedInstance()?.isNaverAppOauthEnable = true
        NaverThirdPartyLoginConnection.getSharedInstance().isInAppOauthEnable = true
        //네이버 로그인 세로모드 고정
        NaverThirdPartyLoginConnection.getSharedInstance().setOnlyPortraitSupportInIphone(true)
        NaverThirdPartyLoginConnection.getSharedInstance().serviceUrlScheme = kServiceAppUrlScheme
        NaverThirdPartyLoginConnection.getSharedInstance().consumerKey = kConsumerKey
        NaverThirdPartyLoginConnection.getSharedInstance().consumerSecret = kConsumerSecret
        NaverThirdPartyLoginConnection.getSharedInstance().appName = kServiceAppName
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL{url in
                    print("url", url)
                    if (AuthApi.isKakaoTalkLoginUrl(url)) {
                        _ = AuthController.handleOpenUrl(url: url)
                    } else if url.absoluteString.contains("google"){
                        GIDSignIn.sharedInstance.handle(url)
                        print("google", url)
                    }
                    else {
                        NaverThirdPartyLoginConnection
                            .getSharedInstance()
                            .receiveAccessToken(url)
                        print("naver", url)
                    }
                }
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate{
    
    let gcmMessageIDKey = "gcm.message_id"
    
    // 앱이 켜졌을 때
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        
        let authOption: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization( options: authOption, completionHandler: {_, _ in})
        application.registerForRemoteNotifications()
        
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        
        GoogleSignIn.GIDSignIn.sharedInstance.restorePreviousSignIn{ user, error in
            if error != nil || user == nil {
                print("not signIn")
            } else {
                print("signed In")
            }
        }
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data){
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func application (_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        var handled: Bool

        handled = GIDSignIn.sharedInstance.handle(url)
        if handled{
            return true
        }
        return false
    }
    
}

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        let deviceToken
        : [String: String] = ["token": fcmToken ?? ""]
        UserDefaults.standard.set(fcmToken, forKey: "DeviceToken")
        print("🎫 fcmToken: ", fcmToken ?? "")
    }
}


extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound, .badge])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void){
        completionHandler()
    }
}

// MARK: BackButton Custom
extension UINavigationController {
    open override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        
        let backButtonImage = UIImage(named: "chevronLeftIcon")
        
        UINavigationBar.appearance().backIndicatorImage = backButtonImage
        UINavigationBar.appearance().backIndicatorTransitionMaskImage = backButtonImage
        navigationBar.topItem?.backButtonDisplayMode = .minimal
    }
}
