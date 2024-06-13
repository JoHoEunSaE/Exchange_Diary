//
//  APIRequest.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 9/6/23.
//

import Foundation
import Alamofire

let urlHost = (Bundle.main.infoDictionary?["API_URL"] as? String ?? "invalidURL")

// MARK: -

/// 비동기적으로 JSON 데이터를 가져와 Decodable 타입으로 디코딩하는 함수.
/// - Parameters:
///   - urlPath: 요청을 보낼 URL 경로.
///   - parameters: 요청에 사용될 매개변수. 기본값은 빈 딕셔너리입니다.
///   - type: 디코딩할 데이터의 타입.
/// - Returns: 디코딩된 데이터 객체.
/// - Throws: 네트워크 요청 실패 또는 데이터 디코딩 실패 시 오류를 발생시킵니다.
func getJsonAsync<T, E>(_ urlPath: String, _ parameters: T? = [:] as [String: Any]?, type: E.Type) async throws -> E where E: Decodable {
    let baseURL = urlHost + urlPath

    // 토큰 유효성 검사
    guard let token = getAccessToken() else {
        UserDefaults.standard.set(false, forKey: "isSignIn")
        throw ResponseErrorData(statusCode: 1, code: "", message: "토큰이 만료되었습니다")
    }
    // 날짜 형식(Date)에 맞춘 디코더 설정
    let decoder = JSONDecoder()
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    decoder.dateDecodingStrategy = .formatted(dateFormatter)

    // Alamofire를 이용한 HTTP GET 요청
    let header: HTTPHeaders = [.authorization(bearerToken: token)]
    let request = AF.request(baseURL, method: .get, parameters: parameters as? [String: Any], encoding: URLEncoding.default, headers: header)
    let dataTask = request.validate().serializingDecodable(type, decoder: decoder)

    // 비동기적으로 응답 처리
    // await print(dataTask.response.response)
    switch await dataTask.result {
    case .success(let value):
        return value
    case .failure(let error):
        print("er", error)
        guard let data = await dataTask.response.data else {
            throw ResponseErrorData(statusCode: 1, code: "", message: "에러 메시지가 없습니다")
        }
        print("⛔️get 에서 실패했습니다...\n \(prettyPrintJson(data))")
        let errorResponse = try JSONDecoder().decode(ResponseErrorData.self, from: data)
        print(String(data: data, encoding: .utf8) ?? "No error message")
        throw errorResponse
    }
}
// MARK: -

/// 비동기적으로 POST 요청을 보내고 JSON 데이터를 `Decodable` 타입으로 디코딩하는 함수.
/// - Parameters:
///   - urlPath: 요청을 보낼 URL 경로.
///   - param: 요청에 포함할 매개변수. `Encodable` 타입입니다. 기본값은 빈 딕셔너리입니다.
///   - type: 응답 데이터를 디코딩할 타입.
/// - Returns: 디코딩된 타입 `E`의 옵셔널 인스턴스를 반환합니다.
/// - Throws: 네트워크 요청 실패 또는 데이터 디코딩 실패 시 오류를 발생시킵니다.
func postJsonAsync<T, E>(_ urlPath: String, _ param: T? = [:] as [String: String], type: E.Type) async throws -> E? where E:Decodable, T:Encodable {
    guard let url = URL(string: urlHost + urlPath) else { fatalError("missingURL") }
    guard let token = getAccessToken() else {
        UserDefaults.standard.set(false , forKey: "isSignIn")
        fatalError() // 재로그인 필요 userDefaults 값 변경
    }
    let header: HTTPHeaders = [.authorization(bearerToken: token)]
    let request = AF.request(url, method: .post, parameters: param, encoder: JSONParameterEncoder.default, headers: header)
    let dataTask = request.validate().serializingDecodable(type, emptyResponseCodes: [200, 201, 204])
    
    switch await dataTask.result {
    case .success(let value):
        return value
    case .failure:
        guard let data = await dataTask.response.data else {
            throw ResponseErrorData(statusCode: 1, code: "NONE", message: "에러 메시지가 없습니다")
        }
        print("⛔️post 에서 실패했습니다...\n \(prettyPrintJson(data))")
        let errorResponse = try JSONDecoder().decode(ResponseErrorData.self, from: data)
        throw errorResponse
    }
}

// MARK: -
func putJsonAsync<T, E>(_ urlPath: String, _ param: T? = [:] as [String: String], type: E.Type) async throws -> E? where E:Decodable, T: Encodable {
    guard let url = URL(string: urlHost + urlPath) else { fatalError("missingURL") }
    guard let token = getAccessToken() else {
        UserDefaults.standard.set(false , forKey: "isSignIn")
        fatalError() // 재로그인 필요 userDefaults 값 변경
    }
    let header: HTTPHeaders = [.authorization(bearerToken: token)]
    let request = AF.request(url, method: .put, parameters: param, encoder: JSONParameterEncoder.default, headers: header)
    let dataTask = request.validate().serializingDecodable(type)
    
    switch await dataTask.result {
    case .success(let value):
        return value
    case .failure:
        guard let data = await dataTask.response.data else {
            throw ResponseErrorData(statusCode: 1, code: "NONE", message: "에러 메시지가 없습니다")
        }
        print("⛔️put 에서 실패했습니다...\n \(prettyPrintJson(data))")
        let errorResponse = try JSONDecoder().decode(ResponseErrorData.self, from: data)
        throw errorResponse
    }
}

// MARK: -

func patchJsonAsync<T, E>(_ urlPath: String, _ param: T? = [:] as [String: String], type: E.Type) async throws -> E? where E:Decodable, T:Encodable {
    guard let url = URL(string: urlHost + urlPath) else { fatalError("missingURL") }
    guard let token = getAccessToken() else {
        UserDefaults.standard.set(false , forKey: "isSignIn")
        fatalError() // 재로그인 필요 userDefaults 값 변경
    }
    let header: HTTPHeaders = [.authorization(bearerToken: token)]
    let request = AF.request(url, method: .patch, parameters: param, encoder: JSONParameterEncoder.default, headers: header)
    let dataTask = request.validate().serializingDecodable(type, emptyResponseCodes: [201, 204])
    
    switch await dataTask.result {
    case .success(let value):
        return value
    case .failure:
        guard let data = await dataTask.response.data else {
            throw ResponseErrorData(statusCode: 1, code: "NONE", message: "에러 메시지가 없습니다")
        }
        print("⛔️patch에서 실패했습니다...\n \(prettyPrintJson(data))")
        let errorResponse  = try JSONDecoder().decode(ResponseErrorData.self, from: data)
        throw errorResponse
    }
}


func deleteJsonAsync(_ urlPath: String) async throws {
    guard let url = URL(string: urlHost + urlPath) else { fatalError("missingURL") }
    guard let token = getAccessToken() else {
        UserDefaults.standard.set(false , forKey: "isSignIn")
        fatalError() // 재로그인 필요 userDefaults 값 변경
    }
    let header: HTTPHeaders = [.authorization(bearerToken: token)]
    let request = AF.request(url, method: .delete, headers: header)
    let dataTask = request.validate().serializingDecodable(Empty.self, emptyResponseCodes: [200, 204])
    

    switch await dataTask.result {
    case .success:
        break
    case .failure:
        guard let data = await dataTask.response.data else {
            throw ResponseErrorData(statusCode: 1, code: "NONE", message: "에러 메시지가 없습니다")
        }
        print("⛔️delete에서 실패했습니다...\n \(prettyPrintJson(data))")
        guard let errorResponse = try? JSONDecoder().decode(ResponseErrorData.self, from: data) else {
                throw ResponseErrorData(statusCode: 1, code: "FAIL_DECONDING", message: "역직렬화 실패")
        }
        
        throw errorResponse
    }
}
