//
//  InformationView.swift
//  frontend
//
//  Created by 신인호 on 3/3/24.
//

import SwiftUI
import Kingfisher

struct InformationView: View {
	@State var showPolicy: Bool = false
	@State var showTerm: Bool = false
	
    var body: some View {

        VStack(alignment: .center, spacing: 26) {
			Button {
				self.showPolicy = true
			} label: {
				SettingRowView(title: "개인정보 처리 방침")
			}
			Button {
				self.showTerm = true
			} label: {
				SettingRowView(title: "서비스 이용약관")
			}
            Divider()
            Button {
                KingfisherManager.shared.cache.clearCache()
            } label : {
                SettingRowView(title: "캐시 삭제", icon: "trashIcon")
            }
            Spacer()
        }
        .padding(20)
		.sheet(isPresented: $showPolicy) {
			PolicyWebView(url: "https://johoeunsae.notion.site/d83d1906cea84bf5948d89566a51c178")
		}
		.sheet(isPresented: $showTerm) {
			PolicyWebView(url: "https://johoeunsae.notion.site/4faab6170ec64fc185c1184f6a5aa5e8")
		}
    }
}

#Preview {
    InformationView()
}
