//
//  NavigationUtils.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 2/15/24.
//

import SwiftUI
import UIKit

struct NavigationUtils {
    static func popToRootView(animated: Bool = true) {
        findNavigationController(viewController: UIApplication.shared.connectedScenes.flatMap {
            ($0 as? UIWindowScene)?.windows ?? []
        }.first { $0.isKeyWindow}?.rootViewController)?.popToRootViewController(animated: animated)
    }
    
    static func findNavigationController(viewController: UIViewController?) -> UINavigationController? {
        guard let viewController = viewController else {
            return nil
        }
        
        if let navigationController = viewController as? UITabBarController {
            return findNavigationController(viewController: navigationController.selectedViewController)
        }
        
        if let navigationController = viewController as? UINavigationController {
            return navigationController
        }
        
        for childViewController in viewController.children {
            return findNavigationController(viewController: childViewController)
        }
        
        return nil
    }
}
