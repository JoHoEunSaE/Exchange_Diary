//
//  TypingTextView.swift
//  frontend
//
//  Created by 신인호 on 1/12/24.
//

import SwiftUI

/// `TypingAnimationView`는 텍스트를 타이핑 애니메이션 효과와 함께 표시하는 뷰입니다.
///
/// - Parameters:
///   - text: 타이핑 애니메이션으로 표시할 텍스트
///
/// 이 뷰는 주어진 `text` 문자열을 한 글자씩 순차적으로 표시하며, 커서가 깜박이는 효과를 제공합니다.
///
/// 뷰 생성 방법:
/// ```
/// TypingAnimationView(text: "여기에 표시할 텍스트를 입력하세요.")
/// ```
///
/// 주요 기능:
/// - 타이핑 애니메이션: 문자열을 한 글자씩 순차적으로 표시합니다.
/// - 커서 깜박임: 타이핑 중 커서가 깜박입니다.
/// - 지연 시작: 1초의 지연 후에 타이핑이 시작됩니다.
///
/// 사용 예:
/// ```
/// TypingAnimationView(text: "함께 쓰는 매일이 특별한 추억이 됩니다.")
/// ```
/// 이 예제에서는 "함께 쓰는 매일이 특별한 추억이 됩니다."라는 텍스트를 타이핑 애니메이션으로 표시합니다.
struct TypingAnimationView: View {
    let text: String
    @State private var displayedText: String = ""
    @State private var cursorOpacity: Double = 1.0
    private let typingSpeed: TimeInterval = 0.1 // 타이핑 속도
    private let cursorBlinkSpeed: TimeInterval = 0.5 // 커서 깜박임
    private let delay = 1.0 // 지연 시작
    
    // 폰트 설정 (원하는 폰트 크기로 조정 가능)
    private var font: UIFont {
        UIFont.systemFont(ofSize: 14)
    }
    
    var body: some View {
        ZStack(alignment: .leading) {
            Text(displayedText)
                .font(Font(font)) // SwiftUI Font로 변환
                .foregroundStyle(Color.defaultGray)
                .frame(height: font.lineHeight)
            RoundedRectangle(cornerRadius: 2)
                .frame(width: 2, height: font.lineHeight) // 커서 높이 조정
                .opacity(cursorOpacity)
                .offset(x: textWidth(for: displayedText), y: 0)
                .foregroundStyle(Color.defaultBlue)
        }
        .onAppear {
            startTypingAnimation()
        }
    }
    
    // 타이핑 애니메이션 시작
    private func startTypingAnimation() {
        cursorBlink()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + self.delay) {
            var index = self.text.startIndex
            Timer.scheduledTimer(withTimeInterval: self.typingSpeed, repeats: true) { timer in
                if index < self.text.endIndex {
                    let nextIndex = self.text.index(after: index)
                    self.displayedText += String(self.text[index])
                    index = nextIndex
                } else {
                    timer.invalidate()
                }
            }
        }
    }
    
    //MARK: 커서 깜박임 효과
    private func cursorBlink() {
        Timer.scheduledTimer(withTimeInterval: cursorBlinkSpeed, repeats: true) { timer in
            self.cursorOpacity = self.cursorOpacity == 1.0 ? 0.0 : 1.0
        }
    }
    
    //MARK: 텍스트 너비 계산
    private func textWidth(for text: String) -> CGFloat {
        let attributes = [NSAttributedString.Key.font: font]
        let size = (text as NSString).size(withAttributes: attributes)
        return size.width
    }
}

#Preview {
    TypingAnimationView(text: "함께 쓰는 매일이 특별한 추억이 됩니다.")
}
