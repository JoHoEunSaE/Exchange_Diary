//
//  SampleDatas.swift
//  frontend
//
//  Created by Katherine JANG on 6/3/23.
//

import Foundation


struct Post: Identifiable, Hashable {
    var id = UUID()
    var title: String
    var summary: String
    var writer: String
    var imageName: String
    
    init(title: String, summary: String, writer: String, imageName: String) {
        self.title = title
        self.summary = summary
        self.writer = writer
        self.imageName = imageName
    }
}

var samplePostLists: [Post] = [
    Post(title: "Apple notary service update", summary: "As announced last year at WWDC, if you notarize you Mac software with the Apple notary service...", writer: "Apple", imageName: "sampleImage"),
    Post(title: "swifty developing", summary: "Get ready for an action-packed online experince at WWDC23. Join ...", writer: "Apple", imageName: "sampleImage"),
    Post(title: "WWDC is coming June 5", summary: "Mark your calendars June 5 through 9 for and exhilarating...", writer: "Apple", imageName: "sampleImage"),
    Post(title: "Get ready with the latest beta releases", summary: "The beta version of iOS 16.5... ", writer: "Apple", imageName: "sampleImage"),
    Post(title: "Meet with App Store experts", summary: "Join us for online sessions Febuary 29 April 13 to learn about...", writer: "Apple", imageName: "sampleImage"),
    Post(title: "Ask Apple Q&As and survey", summary: "Thank you to everyone who joined us during three great weeks ...", writer: "Apple", imageName: "sampleImage"),
    Post(title: "Get your appls for the holidays", summary: "The busiest season on the App Sotre is almost here! Make sure ...", writer: "Apple", imageName: "sampleImage")
]
