//
//  prettyPrintJson.swift
//  frontend
//
//  Created by 신인호 on 1/29/24.
//

import Foundation

/// JSON 데이터를 예쁘게 포맷팅하여 출력하기 위한 함수입니다.
/// 이 함수는 주어진 `Data` 객체를 JSON으로 파싱하고, 이를 예쁘게 포맷팅된 문자열로 변환합니다.
///
/// 변환이 성공하면 예쁘게 포맷팅된 JSON 문자열을 반환합니다.
///
/// 변환에 실패하면, 원본 데이터의 문자열 표현을 반환합니다.
///
/// - Parameter data: 예쁘게 포맷팅할 JSON 데이터를 포함하는 `Data` 객체입니다.
/// - Returns: 포맷팅된 JSON 문자열 또는 원본 데이터의 문자열 표현입니다.
func prettyPrintJson(_ data: Data?) -> String {
    guard let data = data else { return "데이터가 없습니다." }

    do {
        let jsonObject = try JSONSerialization.jsonObject(with: data, options: [])
        let prettyData = try JSONSerialization.data(withJSONObject: jsonObject, options: [.prettyPrinted])
        if let prettyPrintedString = String(data: prettyData, encoding: .utf8) {
            return prettyPrintedString
        }
    } catch {
        print("JSON 포맷팅 중 오류 발생: \(error)")
    }

    // 포맷팅에 실패할 경우 원본 데이터의 문자열 표현을 반환합니다.
    return String(decoding: data, as: UTF8.self)
}
