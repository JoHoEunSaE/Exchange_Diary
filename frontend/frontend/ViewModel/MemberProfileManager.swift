//
//  MemberProfileManager.swift
//  frontend
//
//  Created by 신인호 on 1/21/24.
//

import Foundation


final class MemberProfileManager: ObservableObject {
    @Published var profile: ProfileModel
    private var memberId: Int
    
    init() {
        self.memberId = 0
        self.profile = ProfileModel()
    }
    
    init(_ memberId: Int) {
        self.memberId = memberId
        self.profile = ProfileModel()
    }
    
    @MainActor
    func getProfile() async throws {
        let url: String = "/v1/members/\(self.memberId)/profile"
        let profile = try await getJsonAsync(url, type: ProfileModel.self)
        self.profile = profile
    }
    
    @MainActor
    func getProfileById(_ memberId: Int) async throws {
        let url: String = "/v1/members/\(memberId)/profile"
        let profile = try await getJsonAsync(url, type: ProfileModel.self)
        self.profile = profile
    }
}
