//
//  ClickedButtonStyle.swift
//  frontend
//
//  Created by 신인호 on 2023/09/06.
//

import SwiftUI

struct ScaleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
    }
}

struct NoneButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.98 : 1.0)
    }
}

struct MyCustomButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding(10)
            .background(configuration.isPressed ? Color.blue : Color.green)
            .foregroundColor(.white)
            .cornerRadius(8)
            .shadow(color: Color.black.opacity(0.3), radius: 3, x: 0, y: 2)
    }
}
