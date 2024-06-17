//
//  OnboardingView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 1/6/24.
//

import SwiftUI

struct OnboardingView: View {
    @State private var selectedTab = 0
    @State private var isFadeOut = false
    private let totalTabs = 2
    
    var body: some View {
            VStack {
                ProgressIndicator(selectedTab: $selectedTab, totalTabs: totalTabs)
                    .frame(height: 5)
                    .padding(.horizontal)
                TabView(selection: $selectedTab) {
                    OnboardingEditNicknameView(selectedTab: $selectedTab)
                        .tag(0)
                        .toolbar(.hidden, for: .tabBar)
                    OnboardingAddDiaryView(selectedTab: $selectedTab)
                        .tag(1)
                        .toolbar(.hidden, for: .tabBar)
                    OnboardingCompleteView(isFadeOut: $isFadeOut)
                        .tag(2)
                        .toolbar(.hidden, for: .tabBar)
                }
            }
            .opacity(isFadeOut ? 0 : 1)
    }
}

struct ProgressIndicator: View {
    @Binding var selectedTab: Int
    var totalTabs: Int
    
    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Rectangle()
                    .foregroundColor(Color.gray.opacity(0.3))
                    .cornerRadius(5)
                Rectangle()
                    .foregroundColor(Color.accentColor)
                    .frame(width: geometry.size.width * CGFloat(selectedTab) / CGFloat(totalTabs))
                    .cornerRadius(5)
            }
        }
        .cornerRadius(2.5)
    }
}

#Preview {
    OnboardingView()
}
