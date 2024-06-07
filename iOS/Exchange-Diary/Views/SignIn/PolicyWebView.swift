//
//  PolicyWebView.swift
//  frontend
//
//  Created by Katherine JANG on 3/31/24.
//

import SwiftUI
import WebKit

struct PolicyWebView: UIViewRepresentable {
	var url: String
	
	func makeUIView(context: Context) -> WKWebView {
		guard let url = URL(string: url) else {
			return WKWebView()
		}
		let webView = WKWebView()
		
		webView.load(URLRequest(url: url))
		
		return webView
	}
	
	func updateUIView(_ webView: WKWebView, context: UIViewRepresentableContext<PolicyWebView>) {
		guard let url = URL(string: url) else { return }
		
		webView.load(URLRequest(url: url))
	}
}
