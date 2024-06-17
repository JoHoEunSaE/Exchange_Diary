//
//  ConditionalModifier.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 2/24/24.
//

import Foundation
import SwiftUI

extension View {
    @ViewBuilder func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}

