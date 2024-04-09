//
//  CustomPageTabViewStyle.swift
//  frontend
//
//  Created by 신인호 on 2023/06/03.
//

import SwiftUI

struct CustomPageTabViewStyle: TabViewStyle {
    func makeBody(configuration: Configuration) -> some View {
            TabView(selection: configuration.selection) {
                configuration.content
            }
            .indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))
            
            HStack(spacing: 8) {
                ForEach(configuration.items.indices) { index in
                    Rectangle()
                        .foregroundColor(index == configuration.selectedIndex ? .white : .gray)
                        .frame(width: 10, height: 10)
                        .cornerRadius(5)
                }
            }
            .padding(.vertical, 8)
            .padding(.horizontal, 16)
    }
}
