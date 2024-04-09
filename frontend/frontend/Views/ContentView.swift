//
//  ContentView.swift
//  frontend
//
//  Created by Katherine JANG on 4/1/23.
//

import SwiftUI

struct ContentView: View {
    @AppStorage("isSignIn") private var isSignIn: Bool = false
    @ObservedObject var alertManager = AlertManager.shared

    var body: some View {
        ZStack {
            switch isSignIn {
            case true:
                MainView()
            default:
                SocialSignInView()
            }
        }
        .customAlert(alertManager: alertManager)
    }
}
