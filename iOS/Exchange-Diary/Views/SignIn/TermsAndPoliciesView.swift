//
//  TermsAndPoliciesView.swift
//  frontend
//
//  Created by Katherine JANG on 3/29/24.
//

import SwiftUI
import Combine

enum ArticleType {
	case ageCondition
	case privacyPolicy
	case useTerm
	
	var explanation: String {
		switch self {
		case .ageCondition:
			return "(필수) 만 14세 이상입니다"
		case .privacyPolicy:
			return "(필수) 개인정보 수집 및 이용 동의"
		case .useTerm:
			return "(필수) 서비스 이용 약관 동의"
		}
	}
	var url: String {
		switch self {
		case .ageCondition:
			return ""
		case .privacyPolicy:
			return "/v1/redirect/terms"
		case .useTerm:
			return "/v1/redirect/privacy"
		}
	}
}


// TODO: 워딩 확인 후 title로 분리, 디자인 확인
struct TermsAndPoliciesView: View {
	@State var allAgree: Bool = false // 전체동의
	@State var ageCondition: Bool = false // 14세 이상
	@State var privacyPolicy: Bool = false  // 개인정보 수집 및 이용
	@State var useTerms: Bool = false // 이용약관
	@State var showTerms: Bool = false
	@State var showPolicy: Bool = false
	
	let authManager = AuthenticationManager.shared
	
	var body: some View {
		ZStack {
			Color.defaultBackground
			VStack(alignment: .leading, spacing: 20){
				Text("약관 동의가 \n필요해요")
					.sansSerifBold(28)
					.lineSpacing(10)
					.padding(.vertical, 20)
				Toggle("전체동의", isOn: $allAgree)
					.largeBold()
					.toggleStyle(CheckboxToggleStyle())
				Divider()
				checkToggleWithDetails(article: ArticleType.privacyPolicy, isOn: $privacyPolicy, showWebView: $showPolicy)
//				checkToggleWithDetails(article: ArticleType.useTerm, isOn: $useTerms, showWebView: $showTerms)
				checkToggleWithDetails(article: ArticleType.ageCondition, isOn: $ageCondition, showWebView: $showTerms)
				Spacer()
				CommonButton("계정생성") {
					authManager.signInWithoutParam()
				}
				.large()
				.disabled(!ageCondition || !privacyPolicy )
			}
		}
//		.sheet(isPresented: $showTerms) {
//			PolicyWebView(url: URLs.termURL)
//		}
		.sheet(isPresented: $showPolicy) {
			PolicyWebView(url: URLs.policyURL)
		}
		.padding(30)
		.onChange(of: allAgree) { agreement in
			ageCondition = agreement
			privacyPolicy = agreement
		}
	}
	
	func checkAllAgree() {
		allAgree = ageCondition && privacyPolicy
	}
}

fileprivate struct checkToggleWithDetails: View {
	let article: ArticleType
	@Binding var isOn: Bool
	@Binding var showWebView: Bool
	
	var body: some View {
		HStack {
			Toggle(article.explanation, isOn: $isOn)
				.toggleStyle(CheckboxToggleStyle())
			if article != .ageCondition {
				Button{
					showWebView = true
				} label: {
					Text("보기")
						.small()
						.underline()
						.foregroundStyle(Color.defaultGray)
				}
			}
			Spacer()
		}
	}
}


struct CheckboxToggleStyle: ToggleStyle {
	@Environment(\.isEnabled) var isEnabled

	func makeBody(configuration: Configuration) -> some View {
		Button(action: {
			configuration.isOn.toggle()
		}, label: {
			HStack(alignment: .center){
				Image(systemName: configuration.isOn ? "checkmark.square" : "square")
					.foregroundStyle(configuration.isOn ? .green : .accentColor)
					.font(.system(size: 20))
				configuration.label
			}
		})
	}
}

#Preview {
	TermsAndPoliciesView()
}

