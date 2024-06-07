//
//  TestModel.swift
//  frontend
//
//  Created by Katherine JANG on 8/3/23.
//

import Foundation

struct nunn: Codable {
    let testNum: Int
}

struct Test: Codable {
    let userId: Int
    let id: Int
    let title: String
    let completed: Bool
    let test: nunn
}

final class MockParser {
    static func load<D: Codable>(_ type: D.Type, from resourceName: String) -> D? {
        guard let path = Bundle.main.path(forResource: resourceName, ofType: "json") else {
            print("invalid mock data path")
            return nil
        }
        guard let jsonString = try? String(contentsOfFile: path) else {
            print("invalid jsonString")
            return nil
        }
        print(jsonString)
        
        let decoder = JSONDecoder()
        let data = jsonString.data(using: .utf8)
        print(data as Any)
        
        guard let data = data else {
            print("failed initialize data")
            return nil
        }
        return try? decoder.decode(type, from: data)
    }
}

final class TestManager {
    static let shared = TestManager()
    private init() {}
}

extension TestManager {
    func getTestMock() -> [Test] {
        return MockParser.load([Test].self, from: "test") ?? []
    }
}

