//
//  CommonButtonLabelView.swift
//  frontend
//
//  Created by 신인호 on 1/7/24.
//

import SwiftUI

enum ButtonSize {
    case large, small
}

struct CommonButtonLabel: View {
    var text = "버튼"
    var backgroundColor: Color = .accentColor
    var foregroundColor: Color = .reverseAccentColor
    var size: ButtonSize = .large
    
    var body: some View {
        Text(text)
            .smallBold()
            .frame(maxWidth: .infinity)
            .foregroundColor(foregroundColor)
            .padding(size == .large ? 16 : 10)
            .background(backgroundColor)
            .cornerRadius(10)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color.lightGray, lineWidth: 1)
            )
    }
}

#Preview {
    CommonButtonLabel()
}
