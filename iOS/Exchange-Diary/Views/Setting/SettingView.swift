//
//  SettingView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/7/24.
//

import SwiftUI

struct SettingView: View {
    @Environment(\.dismiss) var dismiss
    @State private var isMailSheetPresented = false
    @State private var isAlertPresented = false

    var body: some View {
        VStack(alignment: .center, spacing: 26) {
            SettingNavigationRow(title: Titles.AccountManger, icon: "userCircleIcon") {
                ManageAccountView()
                    .navigationTitle(Titles.AccountManger)
            }
            SettingNavigationRow(title: Titles.blockList, icon: "blockIcon") {
                ManageBlockView()
                    .navigationTitle(Titles.blockList)
            }
            SettingLinkRow(
                title: Titles.userGuide,
                icon: "bookIcon",
                url: "\(urlHost)/v1/redirect/guide"
            )
            SettingNavigationRow(title: Titles.sendFeedback, icon: "editMessageIcon") {
                InquiryView()
            }
            SettingNavigationRow(title: Titles.information, icon: "infoIcon") {
                InformationView()
                    .navigationTitle(Titles.information)
            }
            Divider()
            Text(Titles.copyrightText)
                .sansSerif(10)
                .foregroundStyle(Color.defaultGray)
                .padding(.bottom, 16)
        }
    }
}

#Preview {
    SettingView()
}
