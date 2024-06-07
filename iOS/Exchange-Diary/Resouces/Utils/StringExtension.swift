//
//  StringExtension.swift
//  frontend
//
//  Created by 김나연 on 2/1/24.
//

import Foundation

extension String {
    func isBlank() -> Bool {
        self.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    func convertToLocalTime() -> String {
        let koreanFormatter = DateFormatter()
        koreanFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        koreanFormatter.timeZone = TimeZone(abbreviation: "KST") // 한국 시간대 설정

        // 백엔드에서 받은 날짜-시간 문자열
        let koreanDateString = self

        // 문자열을 Date 객체로 변환
        if let koreanDate = koreanFormatter.date(from: koreanDateString) {
            // 사용자의 지역 시간대로 DateFormatter 설정
            let localFormatter = DateFormatter()
            localFormatter.dateFormat = "yyyy.MM.dd HH:mm"
            localFormatter.timeZone = TimeZone.current // 사용자의 현재 시간대

            // 지역 시간대에 맞는 날짜-시간 문자열 생성
            let localDateString = localFormatter.string(from: koreanDate)
            return localDateString // 사용자의 지역 시간대에 맞는 날짜-시간 출력
        } else {
            return "yyyy.MM.dd HH:mm"
        }
    }
}
