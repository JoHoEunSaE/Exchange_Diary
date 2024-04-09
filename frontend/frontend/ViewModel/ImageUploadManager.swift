//
//  ImageManager.swift
//  frontend
//
//  Created by Katherine JANG on 11/19/23.
//

import Foundation
import Alamofire
import SwiftUI
import PhotosUI

enum imagePath {
    case profile
    case diary
    case note
}


class ImageUploadManager: ObservableObject {
    static let shared = ImageUploadManager()
    var compressedImage: Data? = nil
    var preSignedURL: String = ""
    
    func uploadImage(imageData: Data?, imagePath: String?) async throws {
        guard (imageData != nil) else { return }
        self.compressedImage = await compressImage(imageData ?? Data())
        try await getPreSignedURL(imagePath)
        try await s3ImageUpload()
    }
    
    func getPreSignedURL(_ imagePath: String?) async throws {
         guard (imagePath != nil) else { return }
         let url = urlHost + "/v1/images/presigned-url"
         guard let token = getAccessToken() else { return }
         let header: HTTPHeaders = [.authorization(bearerToken: token)]
         let AFRequest = AF.request(url, method: .post, parameters: ["imageUrl" : imagePath], encoding: JSONEncoding.default, headers: header)
         let dataTask = AFRequest.validate().serializingString()
         
         switch await dataTask.result {
         case .success(let url) :
             self.preSignedURL = url
         
         case .failure(let error):
             print("getPreSignedURL Failed: \(error)")
             guard let data = await dataTask.response.data else {
                 throw ResponseErrorData(statusCode: 1, code: "", message: "")
             }
             let errorResponse = try JSONDecoder().decode(ResponseErrorData.self, from: data)
             throw errorResponse
         }
     }
     
    
    func s3ImageUpload() async throws {
        guard (self.compressedImage != nil) else { return }
        guard let url = URL(string: self.preSignedURL) else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("image/jpeg", forHTTPHeaderField: "Content-Type")
        request.httpBody = compressedImage
        
        let serializer = DataResponseSerializer(emptyResponseCodes: Set([200, 204, 205]))
        let AFRequest = AF.request(request)
        let dataTask = AFRequest.validate().serializingResponse(using: serializer)

        switch await dataTask.result {
        case .success:
            guard let response = await dataTask.response.response else {
                self.compressedImage = nil
                throw ResponseErrorData(statusCode: 2, code: "", message: "")
            }
            self.compressedImage = nil
        case .failure(let error):
            print("upload filaed", error)
            guard let data = await dataTask.response.data else {
                throw ResponseErrorData(statusCode: 1, code: "", message: "")
            }
            print("Error Data", data)
            self.compressedImage = nil
            let errorResponse = ResponseErrorData(statusCode: 1, code: "", message: "S3Upload failed")
            throw errorResponse
        }
    }
    
    func compressImage(_ imageData: Data) async -> Data? {
        if let uiImage = UIImage(data: imageData)?.aspectFittedToHeight(400) {
            let jpegImage = uiImage.jpegData(compressionQuality: 1)
            getImageSize(imageData)
            getImageSize(uiImage.pngData() ?? Data())
            getImageSize(jpegImage ?? Data())
            return jpegImage
        }
        return nil
    }
}

func getImagePath(_ imageName: String, _ path: imagePath) -> String {
    var imagePath: String
    let profilePath = (Bundle.main.infoDictionary?["PROFILE_IMAGE_PATH"] as? String ?? "invalidPath/")
    let diaryPath = (Bundle.main.infoDictionary?["DIARY_COVER_PATH"] as? String ?? "invalidPath/")
    let notePath = (Bundle.main.infoDictionary?["NOTE_IMAGE_PATH"] as? String ?? "invalidPath/")
    
    switch path {
    case .diary :
        imagePath = diaryPath + UUID().uuidString + imageName + ".jpeg"
    case .note :
        imagePath = notePath + UUID().uuidString + imageName + ".jpeg"
    case .profile:
        imagePath = profilePath  + UUID().uuidString + imageName + ".jpeg"
    }
    return imagePath
}


func getImageSize(_ imageData: Data) {
    let bcf = ByteCountFormatter()
    bcf.allowedUnits = [.useMB] // optional: restricts the units to MB only
    bcf.countStyle = .file
    let string = bcf.string(fromByteCount: Int64(imageData.count))
    print("ImageSizeeeeet: \(string)")
}
