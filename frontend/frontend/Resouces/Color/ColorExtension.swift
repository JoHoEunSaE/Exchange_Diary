//
//  ColorExtension.swift
//  frontend
//
//  Created by Katherine JANG on 5/20/23.
//

import Foundation
import SwiftUI

extension Color {
    static let defaultBlack = Color(hex: "333333")
    static let defaultGray = Color(hex: "ADADAD")
    static let lightGray = Color("lightGrayColor")
    static let coverSelectGray = Color("CoverSelectColor")
    static let defaultYellow = Color(hex: "FFDE54")
    static let defaultMandarin = Color(hex: "FFB800")
    static let defaultOrange = Color(hex: "FF7D1A")
    static let defaultGreen = Color(hex: "85E0A3")
    static let defaultBlue = Color(hex: "0D99FF")
    static let defaultRed = Color(hex: "FF453A")
    static let defaultIndigo = Color(hex: "0A66C2")
    static let defaultPurple = Color(hex: "B18CFF")
    static let backgroundGray = Color(hex: "EEF1F1")
    static let strokeGray = Color(hex: "DADADA")
    static let defaultBackground = Color("BackgroundColor")
    static let reverseAccentColor = Color("ReverseAccentColor")
    static let line = Color("LineColor")
    static let checkBlue = Color(hex: "0D99FF")
    static let sheet = Color("SheetColor")
    static let clearButton = Color("ClearButtonColor")
    static let skeletonColor = Color("SkeletonColor")
    static let skeletonColorDark = Color("SkeletonColorDark")
}

extension Color{
    init(hex:String) {
        let scanner = Scanner(string:hex)
        _ = scanner.scanString("#")
        
        var rgb: UInt64 = 0
        scanner.scanHexInt64(&rgb)
        
        let r = Double((rgb >> 16) & 0xFF) / 255.0
        let g = Double((rgb >> 8) & 0xFF) / 255.0
        let b = Double((rgb >> 0) & 0xFF) / 255.0
        self.init(red: r, green: g, blue:b)
    }
    
    init?(hexWithAlpha: String) {
        var hexSanitized = hexWithAlpha.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")

        var rgba: UInt64 = 0

        Scanner(string: hexSanitized).scanHexInt64(&rgba)

        let red = Double((rgba & 0xFF000000) >> 24) / 255.0
        let green = Double((rgba & 0x00FF0000) >> 16) / 255.0
        let blue = Double((rgba & 0x0000FF00) >> 8) / 255.0
        let alpha = Double(rgba & 0x000000FF) / 255.0

        self.init(.sRGB, red: red, green: green, blue: blue, opacity: alpha)
    }
    
    var uiColor: UIColor { .init(self) }
}

extension LinearGradient {
    static func defaultGradient(_ colorScheme: ColorScheme) -> LinearGradient {
        if colorScheme == .dark {
            return LinearGradient(
                stops: [
                Gradient.Stop(color: Color(red: 0.79, green: 0.53, blue: 0.53), location: 0.00),
                Gradient.Stop(color: Color(red: 0.16, green: 0.33, blue: 0.43), location: 1.00),
                ],
                startPoint: UnitPoint(x: 0, y: 0.46),
                endPoint: UnitPoint(x: 1, y: 0.46)
            )
        } else {
            return LinearGradient(
                stops: [
                    Gradient.Stop(color: Color(red: 1, green: 0.68, blue: 0.68), location: 0.00),
                    Gradient.Stop(color: Color(red: 0.58, green: 0.9, blue: 0.97), location: 1.00),
                ],
                startPoint: UnitPoint(x: 0.03, y: 0.5),
                endPoint: UnitPoint(x: 0.95, y: 0.5)
            )
        }
    }
}

extension UIColor {
    var rgba: (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat) {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0

        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        return (red, green, blue, alpha)
    }
}

extension UIColor {
    var hexStringWithOpacity: String? {
        guard let components = cgColor.components, components.count >= 4 else {
            return nil
        }

        let colorInt = components.map { Int(CGFloat(Float($0) * 255).rounded()) }
        let red = colorInt[0]
        let green = colorInt[1]
        let blue = colorInt[2]
        let opacity = colorInt[3]
 
        return String(format: "#%02X%02X%02X%02X", red, green, blue, opacity)
    }
}

var defaultColors: [Color] = [Color.white, Color.gray, Color.black, Color.defaultYellow, Color.defaultMandarin, Color.defaultOrange, Color.defaultGreen, Color.defaultBlue, Color.defaultIndigo, Color.defaultPurple]

extension Color {
    static func limitedTextColor(text: String, max: Int) -> Color {
        text.count > max ? Color.red : Color.defaultGray
    }

    static func random(randomOpacity: Bool = false) -> Color {
        Color(
            red: .random(in: 0...1),
            green: .random(in: 0...1),
            blue: .random(in: 0...1),
            opacity: randomOpacity ? .random(in: 0...1) : 1
        )
    }
}
