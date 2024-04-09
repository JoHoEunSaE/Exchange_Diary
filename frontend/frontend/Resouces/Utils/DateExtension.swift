//
//  DateExtension.swift
//  frontend
//
//  Created by Katherine JANG on 8/3/23.
//

import Foundation

extension Date {
    var yearToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "y"
        return dateFormatter.string(from: self)
    }
    var monthToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "M"
        return dateFormatter.string(from: self)
    }
    var dayToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "d"
        return dateFormatter.string(from: self)
    }
    var hourToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "h"
        return dateFormatter.string(from: self)
    }
    var minuteToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "m"
        return dateFormatter.string(from: self)
    }
    var secondToString: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "s"
        return dateFormatter.string(from: self)
    }
    var MM: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM"
        return dateFormatter.string(from: self)
    }
    var dd: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd"
        return dateFormatter.string(from: self)
    }
    func toString(_ format: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "ko_KR")
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }
}

extension Date {
    var yearToInt: Int {
        let cal = Calendar.current
        return cal.component(.year, from: self)
    }
    var monthToInt: Int {
        let cal = Calendar.current
        return cal.component(.month, from: self)
    }
    var dayToInt: Int {
        let cal = Calendar.current
        return cal.component(.day, from: self)
    }
    var weekdayToInt: Int {
        let cal = Calendar.current
        return cal.component(.weekday, from: self)
    }
    var hourToInt: Int {
        let cal = Calendar.current
        return cal.component(.hour, from: self)
    }
    var minuteToInt: Int {
        let cal = Calendar.current
        return cal.component(.minute, from: self)
    }
    var secondToInt: Int {
        let cal = Calendar.current
        return cal.component(.second, from: self)
    }
}

extension Date {
    var millisecondsSince1970: Int {
        Int((self.timeIntervalSince1970 * 1000.0).rounded())
    }
    init(milliseconds: Int) {
        self = Date(timeIntervalSince1970: TimeInterval(milliseconds))
    }
}
