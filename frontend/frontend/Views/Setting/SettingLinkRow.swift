//
//  SettingLinkRow.swift
//  frontend
//
//  Created by 신인호 on 3/3/24.
//

import SwiftUI
import Kingfisher

struct SettingLinkRow: View {
    @Environment(\.openURL) private var openURL
    var title: String
    var icon: String = ""
    var url: String

    var body: some View {
        Button {
            if let url = URL(string: url) {
                openURL(url)
            }
        } label: {
            SettingRowView(title: title, icon: icon)
        }
    }
}

#Preview {
    @Environment(\.openURL) var openURL

    return SettingLinkRow(title: "이용약관", icon: "infoIcon", url: "https://google.com")
}
