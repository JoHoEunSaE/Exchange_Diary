//
//  AlertModifier.swift
//  frontend
//
//  Created by 신인호 on 3/6/24.
//

import SwiftUI

struct AlertModifier: ViewModifier {
    @ObservedObject var alertManager: AlertManager

    func body(content: Content) -> some View {
        content
            .alert(alertManager.title, isPresented: $alertManager.isAlertPresented) {
                Button(alertManager.cancelText, role: .cancel) {
                    alertManager.reset()
                }
                if let confirmText = alertManager.confirmText {
                    Button(confirmText, role: alertManager.confirmRole) {
                        alertManager.action()
                    }
                }
            } message: {
                Text(alertManager.message)
            }
    }
}

extension View {
    func customAlert(alertManager: AlertManager) -> some View {
        self.modifier(AlertModifier(alertManager: alertManager))
    }
}
