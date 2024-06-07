//
//  CustomTextStyles.swift
//  frontend
//
//  Created by 신인호 on 12/8/23.
//

import SwiftUI


enum FontSize {
    case extraSmall, small, regular, large, extraLarge

    var value: CGFloat {
        switch self {
        case .extraSmall:
            return 12.0
        case .small:
            return 14.0
        case .regular:
            return 16.0
        case .large:
            return 18.0
        case .extraLarge:
            return 20.0
        }
    }
}

private let kerningPercentage: CGFloat = -0.02

// MARK: SIZE CUSTOM
struct Serif: ViewModifier {
    var size: CGFloat
    var weight: Font.NanumMyeongjo
    
    func body(content: Content) -> some View {
        content
            .font(.nanumMyeonjo(self.weight, size: self.size))
            .kerning(self.size * kerningPercentage)
    }
}

struct SansSerif: ViewModifier {
    var size: CGFloat
    var weight: Font.Weight?
    
    func body(content: Content) -> some View {
        content
            .font(.system(size: self.size, weight: weight))
            .kerning(self.size * kerningPercentage)
    }
}

extension View {
    func serif(_ size: CGFloat) -> some View {
        self.modifier(Serif(size: size, weight: .regular))
    }
    
    func serifBold(_ size: CGFloat) -> some View {
        self.modifier(Serif(size: size, weight: .bold))
    }
    
    func sansSerif(_ size: CGFloat) -> some View {
        self.modifier(SansSerif(size: size))
    }
    
    func sansSerifBold(_ size: CGFloat) -> some View {
        self.modifier(SansSerif(size: size, weight: .semibold))
    }
    
    func extraLarge() -> some View {
        sansSerif(FontSize.extraLarge.value)
    }
    
    func extraLargeBold() -> some View {
        sansSerifBold(FontSize.extraLarge.value)
    }
    
    func large() -> some View {
        sansSerif(FontSize.large.value)
    }
    
    func largeBold() -> some View {
        sansSerifBold(FontSize.large.value)
    }
    
    func regular() -> some View {
        sansSerif(FontSize.regular.value)
    }
    
    func regularBold() -> some View {
        sansSerifBold(FontSize.regular.value)
    }
    
    func small() -> some View {
        sansSerif(FontSize.small.value)
    }
    
    func smallBold() -> some View {
        sansSerifBold(FontSize.small.value)
    }
    
    func extraSmall() -> some View {
        sansSerif(FontSize.extraSmall.value)
    }
    
    func extraSmallBold() -> some View {
        sansSerifBold(FontSize.extraSmall.value)
    }
}
