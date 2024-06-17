//
//  OnboardingAddDiaryView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 1/6/24.
//

import SwiftUI

struct OnboardingAddDiaryView: View {
    @Binding var selectedTab: Int
    @State private var isAddDiaryPresented = false
    
    var body: some View {
        VStack {
            VStack(spacing: 20) {
                Text(Titles.addFirstDiary)
                    .multilineTextAlignment(.center)
                    .serifBold(28)
                    .lineSpacing(10)
                Text(Titles.clickOnDiaryCover)
                    .smallBold()
                    .foregroundStyle(Color.defaultGray)
            }
            .padding(.vertical, 96)
            Button {
                isAddDiaryPresented.toggle()
            } label: {
                AddDiaryButtonView()
                    .frame(width: 140, height: 210)
                    .padding(.horizontal, 20)
            }
            Spacer()
            CommonButton(Titles.doItLater) {
                withAnimation {
                    selectedTab = 2
                }
            }
            .pale()
        }
        .padding(20)
        .navigationDestination(isPresented: $isAddDiaryPresented) {
            AddDiaryView(isOnboarding: true) {
                selectedTab = 2
            }
        }
    }
}

#Preview {
    OnboardingAddDiaryView(selectedTab: .constant(1))
}
