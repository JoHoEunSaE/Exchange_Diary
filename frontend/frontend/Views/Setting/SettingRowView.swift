//
//  SettingRowView.swift
//  frontend
//
//  Created by 신인호 on 3/3/24.
//

import SwiftUI

struct SettingRowView: View {
    var title: String
    var icon: String = ""

    var body: some View {
        HStack(spacing: 8) {
            if icon != "" {
                Image(icon)
            }
            Text(title)
                .smallBold()
            Spacer()
            Image("chevronRightIcon")
                .opacity(0.3)
        }
    }
}

#Preview("제목") {
    SettingRowView(title: "이용 약관")
}

#Preview("제목, 아이콘") {
    SettingRowView(title: Titles.AccountManger, icon: "userCircleIcon")
}
