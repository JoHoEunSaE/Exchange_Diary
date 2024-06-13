//
//  CoordinateSpaceModifier.swift
//  Exchange-Diary
//
//  Created by 신인호 on 3/28/24.
//

import SwiftUI

struct CoordinateSpaceModifier: ViewModifier {
    var name: String = ""

    func body(content: Content) -> some View {
        if #available(iOS 17.0, *) {
            content.coordinateSpace(.named(name))
        } else {
            content.coordinateSpace(name: name)
        }
    }
}

extension View {
    func coordinateSpaceModifier(name: String) -> some View {
        self.modifier(CoordinateSpaceModifier(name: name))
    }
}

