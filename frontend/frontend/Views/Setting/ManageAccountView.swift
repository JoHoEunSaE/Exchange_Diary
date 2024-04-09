//
//  ManageAccountView.swift
//  frontend
//
//  Created by Katherine JANG on 1/7/24.
//

import SwiftUI

struct ManageAccountView: View {
    @Environment(\.dismiss) var dismiss
    @State var isLogoutAlertPresented = false
    let profileManager = MyProfileManager.shared.myProfile
    let oauthTypes = ["apple", "google", "kakao", "naver"]
       let myOauthType = UserDefaults.standard.string(forKey: "OauthType") ?? ""
    
    var body: some View {
        ScrollView {
            VStack(alignment: .center, spacing: 26) {
                VStack {
                    Text(profileManager.email)
                        .regularBold()
                        .padding(.bottom, 8)
                    HStack {
                        ForEach(oauthTypes, id: \.self) { oauthType in
                            Image(imageName(for: oauthType))
                        }
                    }
                }
                .padding(.vertical, 30)
                NavigationLink {
                    ProfileEditView()
                } label: {
                    SettingRowView(title: Titles.profileEdit, icon: "userCircleIcon")
                }
                Divider()
                Button {
                    isLogoutAlertPresented = true
                } label : {
                    SettingRowView(title: Titles.logout, icon: "logoutIcon")
                }
                NavigationLink {
                    AccountDeletionView()
                        .navigationTitle(Titles.accountDeletion)
                } label: {
                    SettingRowView(title: Titles.accountDeletion, icon: "eraserIcon")
                }
                Spacer()
            }
        }
        .padding(.horizontal, 20)
        .alert(Titles.logout, isPresented: $isLogoutAlertPresented) {
            Button(Titles.cancel, role: .cancel, action: {})
            Button(role: .destructive) {
                AuthenticationManager.shared.signOut()
            } label: {
                Text(Titles.logout)
            }
        } message: {
            Text(Titles.logoutConfirm)
        }
    }
    
    private func imageName(for snsType: String) -> String {
        return myOauthType.uppercased() == snsType.uppercased() ? "\(snsType)Fill" : snsType
    }
}

#Preview {
    ManageAccountView()
}
