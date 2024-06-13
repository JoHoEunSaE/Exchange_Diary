//
//  SettingNavigationRow.swift
//  Exchange-Diary
//
//  Created by 신인호 on 3/3/24.
//

import SwiftUI

struct SettingNavigationRow<Content: View>: View {
    var title: String
    var icon: String
    var destination: () -> Content

    var body: some View {
        NavigationLink(destination: self.destination()) {
            SettingRowView(title: title, icon: icon)
        }
    }
}

#Preview {
    SettingNavigationRow(title: "정보", icon: "infoIcon") {
        InformationView()
    }
}
