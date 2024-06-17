//
//  MyProfileViewModel.swift
//  Exchange-Diary
//
//  Created by 신인호 on 10/22/23.
//

import Foundation
import Alamofire

final class MyProfileManager: ObservableObject {
    static let shared = MyProfileManager()
    private init() {}
    
    @Published var myProfile: ProfileModel = ProfileModel()
    
    @MainActor
    func getMyProfile() {
        let url: String = "/v1/members/me/profile"

        Task {
            do {
                let profile = try await getJsonAsync(url, type: ProfileModel.self)
                self.myProfile = profile
            }  catch let error as ResponseErrorData {
                AlertManager.shared.setError(error)
                AuthenticationManager.shared.signOut()
            } catch {
                print("[[ MainView onAppear ]]\n \(error)")
                AuthenticationManager.shared.signOut()
            }
        }
    }
    
    @MainActor
    func editMyProfile(profile: UploadProfile, profileImage: Data?) async throws {
        let imageUploadManager = ImageUploadManager.shared
        if profileImage != nil {
            try await ImageUploadManager.shared.uploadImage(imageData: profileImage, imagePath: profile.profileImageUrl)
        }
        guard let newProfile = try await patchJsonAsync("/v1/members/me/profile", profile, type: UploadProfile.self) else {
            print("image upload info xxxx")
            return
        }
        self.myProfile.nickname = newProfile.nickname
        self.myProfile.statement = newProfile.statement
        self.myProfile.profileImageUrl = newProfile.profileImageUrl
    }
    
    // MARK: 내 아이디와 동일한지 비교
    func isMe(_ memberId: Int) -> Bool {
        self.myProfile.memberId == memberId
    }
    
    // MARK: 내 아이디와 불일치하는지 비교
    func isNotMe(_ memberId: Int) -> Bool {
        self.myProfile.memberId != memberId
    }
    
    func reset() {
        self.myProfile = ProfileModel()
    }
}

