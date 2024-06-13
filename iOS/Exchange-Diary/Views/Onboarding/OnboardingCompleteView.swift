//
//  OnboardingCompleteView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 1/7/24.
//

import SwiftUI

struct OnboardingCompleteView: View {
    @Binding var isFadeOut: Bool
    
    var body: some View {
        VStack {
            VStack(spacing: 20) {
                Text(Titles.welcomeTitle)
                    .multilineTextAlignment(.center)
                    .serifBold(28)
                    .lineSpacing(10)
                Text(Titles.welcomeSubtitle)
                    .smallBold()
                    .foregroundStyle(Color.defaultGray)
                    .multilineTextAlignment(.center)
                    .lineSpacing(10)
            }
            .padding(.vertical, 96)
            Spacer()
            CommonButton(Titles.goToHome) {
                withAnimation(.linear(duration: 2)) {
                    isFadeOut = true
                }
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    AuthenticationManager.shared.isSignUp = false
                    UserDefaults.standard.set(true, forKey: "isSignIn")
                }
            }
        }
        .padding(20)
    }
}

#Preview {
    OnboardingCompleteView(isFadeOut: .constant(false))
}
