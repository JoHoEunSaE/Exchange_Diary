//
//  BottomSheetDetentsExtension.swift
//  frontend
//
//  Created by Katherine JANG on 7/11/23.
//

import Foundation
import SwiftUI

struct HeightPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat?
    
    static func reduce(value: inout CGFloat?, nextValue: () -> CGFloat?){
        guard let nextValue = nextValue() else {return}
        value = nextValue
    }
}


private struct ReadHeightModifier: ViewModifier {
    private var sizeView: some View {
        GeometryReader {geometry in
            Color.clear.preference(key: HeightPreferenceKey.self, value: geometry.size.height + 100)
        }
    }
    func body(content: Content) -> some View {
        content.background(sizeView)
    }
}

extension View {
    func readHeight() -> some View {
        withAnimation{
            self.modifier(ReadHeightModifier())
        }
    }
}
