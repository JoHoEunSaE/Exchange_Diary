//
//  BindingExtension.swift
//  Exchange-Diary
//
//  Created by 신인호 on 10/22/23.
//

import SwiftUI

/// 주어진 Bool 값에 대한 Binding을 생성하는 함수
///
/// - Parameters:
///   - value: 생성하려는 Binding의 초기값 (Bool)
/// - Returns: 주어진 초기값을 가지는 Binding<Bool> 객체
func bindingTo(_ value: Bool) -> Binding<Bool> {
    Binding<Bool>(
        get: { value },
        set: { _ in }
    )
}
