//
//  Binding.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 10/18/23.
//

import Foundation
import SwiftUI


///
extension Binding {
    /// `onChange` 함수는 `Binding` 값이 변경될 때 마다 특정 작업을 수행하도록 합니다.
    /// 이 함수는 `handler` 클로저를 매개변수로 받으며, `Binding` 값의 변화를 감지하고 처리할 수 있습니다.
    ///
    /// - Parameter handler: 값이 변경될 때 호출될 클로저입니다.
    ///   이 클로저는 `Value` 타입의 매개변수를 받으며, 반환 값이 없습니다.
    /// - Returns: 변경 감지 기능이 추가된 `Binding<Value>` 인스턴스를 반환합니다.
    ///
    /// 사용 예시:
    /// ```
    /// @State private var textValue: String = ""
    ///
    /// var body: some View {
    ///     TextField("Enter text", text: $textValue.onChange(textChanged))
    /// }
    ///
    /// func textChanged(to newValue: String) {
    ///     print("Text has changed to: \(newValue)")
    /// }
    /// ```
    /// 이 예시에서는 사용자가 `TextField`에 입력할 때마다 `textChanged` 함수가 호출되어
    /// 콘솔에 새로운 텍스트 값을 출력합니다.
    func onChange(_ handler: @escaping (Value) -> Void) -> Binding<Value> {
        Binding(
            get: { self.wrappedValue },
            set: { newValue in
                self.wrappedValue = newValue
                handler(newValue)
            }
        )
    }
}


extension Binding where Value == String? {
    /// 변환 함수: `Binding<String?>`을 `Binding<String>`으로 변환합니다.
    /// `nil` 값은 주어진 기본값(defaultValue)으로 대체됩니다.
    ///
    /// - Parameter defaultValue: `nil`인 경우 사용할 기본값, 기본값은 빈 문자열입니다.
    /// - Returns: `Binding<String>` 인스턴스를 반환합니다.
    func nonOptional(default defaultValue: String = "") -> Binding<String> {
        Binding<String>(
            get: { self.wrappedValue ?? defaultValue },
            set: { newValue in self.wrappedValue = newValue }
        )
    }
}
