//
//  ReportModel.swift
//  frontend
//
//  Created by Katherine JANG on 1/16/24.
//

import Foundation

enum ReportOption: String, Codable, CaseIterable {
    case spam = "SPAM"
    case sexual = "SEXUAL"
    case scam = "SCAM"
    case hate = "HATE"
    case bullying = "BULLYING"
    case violence = "VIOLENCE"
    case ipr = "IPR"
    case illegal = "ILLEGAL"
    case harmful = "HARMFUL"
    case etc = "ETC"
    
    var description: String {
        switch self {
        case .spam: return "스팸"
        case .sexual: return "나체 이미지 또는 성적 행위"
        case .scam: return "사기 또는 거짓"
        case .hate: return "혐오 발언 또는 상징"
        case .bullying: return "따돌림 또는 괴롭힘"
        case .violence: return "폭력 또는 위험한 단체"
        case .ipr: return "지적재산권 침해"
        case .illegal: return "불법 또는 규제 상품 판매"
        case .harmful: return "자살 또는 자해"
        case .etc: return "기타 문제"
        }
    }
}

struct ReportModel: Codable {
    var reportType: String
    var reason: String
}
