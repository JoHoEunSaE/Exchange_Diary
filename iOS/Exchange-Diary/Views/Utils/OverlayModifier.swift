//
//  OverlayModifier.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 2/11/24.
//

import SwiftUI

extension View {
    @ViewBuilder 
    public func overlayIf<T: View> (
        _ condition: Binding<Bool>,
        _ content: T,
        alignment: Alignment = .center
    ) -> some View {
        if condition.wrappedValue {
            self.overlay(content, alignment: alignment)
        } else {
            self
        }
    }
}
