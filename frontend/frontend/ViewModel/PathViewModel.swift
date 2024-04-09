//
//  PathViewModel.swift
//  frontend
//
//  Created by Katherine JANG on 7/23/23.
//

import Foundation
import SwiftUI

class PathViewModel: ObservableObject {
    @Published var targetDestination: [TargetViewInfo] = []
    
    @MainActor
    func navigateTo(_ newTarget: TargetDestination, _ targetData: AnyHashable? = nil) {
        Task {
            self.targetDestination.append(TargetViewInfo(newTarget, targetData))
        }
    }
    
    @MainActor
    func goBack() {
        if !targetDestination.isEmpty {
            self.targetDestination.removeLast()
        }
    }
    
    @MainActor
    func goRoot() {
        if !targetDestination.isEmpty {
            self.targetDestination.removeAll()
        }
    }
}
