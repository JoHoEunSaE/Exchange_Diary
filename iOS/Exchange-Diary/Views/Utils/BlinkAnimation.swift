//
//  BlinkAnimation.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 5/20/23.
//

import SwiftUI

struct BlinkAnimation: ViewModifier{
    @State private var opacity: CGFloat = 0.0
    
    func body(content: Content) -> some View {
        content
            .opacity(opacity)
            .animation(
                .easeInOut(duration: 0.28).repeatForever(),
                value: opacity
            )
            .onAppear {
                opacity = 1
            }
    }
}

extension View {
    func blink() -> some View {
        modifier(BlinkAnimation())
    }
}
