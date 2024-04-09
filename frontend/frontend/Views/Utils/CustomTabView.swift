//
//  CustomTabView.swift
//  frontend
//
//  Created by 신인호 on 2023/06/03.
//

import SwiftUI

struct CustomTabView: View {

    // MARK: - Public Properties

    let numberOfPages: Int
    let currentIndex: Int


    // MARK: - Drawing Constants

    private let circleSize: CGFloat = 16
    private let circleSpacing: CGFloat = 12

    private let primaryColor = Color.black
    private let secondaryColor = Color.black.opacity(0.6)

    private let smallScale: CGFloat = 0.6


    // MARK: - Body

    var body: some View {
        HStack(spacing: circleSpacing) {
            ForEach(0..<numberOfPages) { index in // 1
                if shouldShowIndex(index) {
                    Circle()
                        .fill(currentIndex == index ? primaryColor : secondaryColor) // 2
                        .scaleEffect(currentIndex == index ? 1 : smallScale)
                        .frame(width: circleSize, height: circleSize)
                        .transition(AnyTransition.opacity.combined(with: .scale)) // 3
                        .id(index) // 4
                }
            }
        }
    }

    func shouldShowIndex(_ index: Int) -> Bool {
        ((currentIndex - 1)...(currentIndex + 1)).contains(index)
    }
}


struct CustomTabView_Previews: PreviewProvider {
    static var previews: some View {
        CustomTabView(numberOfPages: 3, currentIndex: 0)
    }
}
