//
//  CustomTextFieldStyle.swift
//  frontend
//
//  Created by 신인호 on 2023/05/20.
//

import SwiftUI

struct CustomTextFieldStyle: TextFieldStyle {
    var isValid: Bool = true
    var lineColor: Color = .line
    var height: CGFloat = 40.0

    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .frame(height: height)
            .frame(maxWidth: .infinity)
            .foregroundColor(.accentColor)
            .overlay {
                Rectangle()
                    .frame(height: 1)
                    .foregroundColor(isValid ? lineColor : .red)
                    .padding(.top, height)
            }
            .padding(.bottom, 8)
    }
}
