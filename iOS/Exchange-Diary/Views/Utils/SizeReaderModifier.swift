//
//  WidthReaderModifier.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 6/27/23.
//

import SwiftUI

extension View {
    func sizeReader(_ sizeChange: @escaping (CGSize?) -> Void) -> some View {
        modifier(SizeReaderModifier(sizeChange: sizeChange))
    }
}

fileprivate struct SizePreferenceKey: PreferenceKey {
    static var defaultValue: CGSize? = nil

    static func reduce(value: inout CGSize?, nextValue: () -> CGSize?) {
        value = nextValue()
    }
}

fileprivate struct SizeReaderModifier: ViewModifier {
    let sizeChange: (CGSize?) -> Void

    func body(content: Content) -> some View {
        content
            .background(
                GeometryReader { geometry in
                    Color.clear.preference(key: SizePreferenceKey.self, value: geometry.size)
                }
            )
            .onPreferenceChange(SizePreferenceKey.self) { newSize in
                self.sizeChange(newSize)
            }
    }
}
