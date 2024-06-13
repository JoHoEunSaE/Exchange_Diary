//
//  NavigationStackModel.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 11/24/23.
//

import Foundation

enum TargetDestination {
    case diary
    case note
    case addDiary
    case createNote
    case profile
    case profileEdit
}

struct TargetViewInfo: Hashable {
    let destination: TargetDestination
    let targetData: AnyHashable?
    
    init<T: Hashable>(_ destination: TargetDestination, _ targetData: T?) {
        self.destination = destination
        self.targetData = AnyHashable(targetData)
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(destination)
        hasher.combine(targetData)
    }
    
    static func == (lhs: TargetViewInfo, rhs: TargetViewInfo) -> Bool {
        return lhs.destination == rhs.destination
    }
}
