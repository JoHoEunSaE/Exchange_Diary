//
//  Fonts.swift
//  frontend
//
//  Created by Katherine JANG on 5/20/23.
//

import Foundation
import SwiftUI

extension Font {
    enum NanumMyeongjo {
        case regular
        case bold

        var value: String {
            switch self {
            case .regular:
                return "NanumMyeongjo"
            case .bold:
                return "NanumMyeongjoBold"
            }
        }
    }

    static func nanumMyeonjo(_ type: NanumMyeongjo, size: CGFloat = 17) -> Font {
        return .custom(type.value, size: size)
    }
}
