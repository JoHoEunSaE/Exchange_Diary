//
//  ShimmeringModifier.swift
//  frontend
//
//  Created by 신인호 on 2/4/24.
//

import SwiftUI

struct ShimmeringModifier: ViewModifier {
    @State private var phase: CGFloat = 0
    
    func body(content: Content) -> some View {
        content
            .overlay(
                Shimmer()
                    .opacity(0.6)
            )
            .onAppear {
                withAnimation(Animation.linear(duration: 1).repeatForever(autoreverses: false)) {
                    phase = UIScreen.main.bounds.width * 3
                }
            }
    }
    
    func Shimmer() -> some View {
        LinearGradient(gradient: Gradient(colors: [Color.clear, Color.white.opacity(0.8), Color.clear]), startPoint: .init(x: 0.00, y: 0.50), endPoint: .init(x: 1.00, y: 0.50))
            .offset(x: phase - UIScreen.main.bounds.width)
            .blendMode(.softLight)
    }
}

extension View {
    func shimmering() -> some View {
        self.modifier(ShimmeringModifier())
    }
}
