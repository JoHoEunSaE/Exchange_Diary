//
//  CommonButton.swift
//  frontend
//
//  Created by 신인호 on 2023/09/05.
//

import SwiftUI

/// `CommonButton` View는  재사용 가능한 기본 버튼 컴포넌트입니다.
///
/// - Parameters:
///   - text: 버튼에 표시될 텍스트
///   - isDisabled: 버튼이 비활성화 되어있는지 여부 (기본값 `false`)
///   - action: 버튼 클릭 시 수행할 클로저
///
/// 버튼 생성 방법:
/// ```
/// CommonButton("버튼 텍스트") {
///     // 버튼 클릭 시 수행할 액션
/// }
/// ```
///
/// 버튼 스타일링:
/// - `foregroundColor`: 버튼의 전경색을 설정합니다.
/// - `backgroundColor`: 버튼의 배경색을 설정합니다.
/// - `pale`: 버튼을 연한 스타일로 설정합니다.
///
/// 사용 예:
/// ```
/// CommonButton("로그인", isDisabled: true) {
///     print("로그인 버튼 클릭")
/// }
/// .foregroundColor(.white)
/// .backgroundColor(.blue)
/// .pale()
/// ```
/// 이 예제에서는 "로그인"이라는 텍스트를 가진 버튼을 생성하고, 비활성화 상태로 만듭니다.
/// 또한, 버튼에 흰색 전경색과 파란색 배경색을 적용하고, `pale` 메서드를 사용하여 스타일을 수정합니다.
struct CommonButton: View {
    private var text: String
    private var isDisabled = false
    private var foregroundColor: Color = .reverseAccentColor
    private var backgroundColor: Color = .accentColor
    private var size: ButtonSize = .large
    private var action: () -> Void
    
    init(_ text: String, isDisabled: Bool = false, size: ButtonSize = .large, action: @escaping () -> Void = {}) {
        self.text = text
        self.isDisabled = isDisabled
        self.action = action
        self.size = size
    }
    
    var body: some View {
        Button {
            action()
        } label: {
            CommonButtonLabel(
                text: text,
                backgroundColor: isDisabled ? Color.defaultGray : backgroundColor,
                foregroundColor: foregroundColor,
                size: size
            )
        }
        .disabled(isDisabled)
        .buttonStyle(ScaleButtonStyle())
    }
    
    func foregroundColor(_ color: Color) -> Self {
        var view = self
        
        view.foregroundColor = color
        
        return view
    }
    
    func backgroundColor(_ color: Color) -> Self {
        var view = self
        
        view.backgroundColor = color
        
        return view
    }
    
    func pale() -> Self {
        var view = self
        
        view.backgroundColor = .reverseAccentColor
        view.foregroundColor = .accentColor
        
        return view
    }
    
    func small() -> Self {
        var view = self
        
        view.size = .small
        
        return view
    }
    
}

#Preview("활성화 버튼") {
    var buttonText = "버튼"
    
    return CommonButton(buttonText) {
        print("버튼을 누릅니다.")
    }
}

#Preview("비활성화 버튼") {
    var buttonText = "버튼 비활성화"
    
    return CommonButton(buttonText, isDisabled: true) {
        print("버튼을 누릅니다.")
    }
}


#Preview("스킵 버튼") {
    var buttonText = "건너뛰기"
    
    return CommonButton(buttonText) {
        print("버튼을 누릅니다.")
    }
    .pale()
}
