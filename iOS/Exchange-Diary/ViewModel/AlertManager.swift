//
//  AlertManager.swift
//  frontend
//
//  Created by 신인호 on 3/5/24.
//

import SwiftUI

@MainActor
final class AlertManager: ObservableObject {
    static let shared = AlertManager()
    private init() { }

    @Published var isAlertPresented = false
    @Published var title = ""
    @Published var message = ""
    @Published var cancelText = Titles.cancel
    @Published var confirmText: String? = nil
    var confirmRole: ButtonRole? = nil
    var action: () -> () = {}

    func show(
        title: String = "",
        message: String = "",
        cancelText: String = Titles.cancel,
        confirmText: String? = nil,
        confirmRole: ButtonRole? = nil,
        action: @escaping () -> () = {}
    ) {
        self.isAlertPresented = true
        self.title = title
        self.message = message
        self.cancelText = cancelText
        self.confirmText = confirmText
        self.confirmRole = confirmRole
        self.action = action
    }

    func setError(
        _ error: ResponseErrorData?,
        title: String = Titles.error,
        cancelText: String = Titles.confirm,
        confirmText: String? = nil,
        confirmRole: ButtonRole? = nil,
        action: @escaping () -> () = {}
    ) {
        guard let error else { return }

        self.isAlertPresented = true
        self.title = title
        self.message = error.message
        self.cancelText = cancelText
        self.confirmText = confirmText
        self.confirmRole = confirmRole
        self.action = action
    }

    func reset() {
        self.isAlertPresented = false
        self.title = ""
        self.message = ""
        self.cancelText = Titles.cancel
        self.confirmText = nil
        self.confirmRole = nil
        self.action = {}
    }
}
