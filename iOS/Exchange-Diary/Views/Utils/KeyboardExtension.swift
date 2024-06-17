//
//  KeyboardExtension.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2/21/24.
//

import SwiftUI

extension View {
    /// 키보드를 숨기는 메서드.
    /// `UIApplication`의 `sendAction`을 사용하여 현재 활성화된 텍스트 입력 필드의 키보드를 숨깁니다.
    func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }

    /// 탭 제스처를 사용하여 키보드를 숨기는 뷰 수정자.
    /// 이 메서드를 사용하면 뷰의 어느 곳이든 탭할 때 키보드가 숨겨집니다.
    /// - Returns: 탭 제스처가 적용된 뷰.
    func dismissKeyboardOnTap() -> some View {
        self.gesture(
            TapGesture().onEnded { _ in
                hideKeyboard()
            }
        )
    }
}
