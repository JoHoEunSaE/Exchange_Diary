//
//  InquiryView.swift
//  frontend
//
//  Created by 신인호 on 2/21/24.
//

import SwiftUI
import MessageUI

struct InquiryView: View {
    @Environment(\.dismiss) var dismiss
    @State private var agreeToCollectDeviceInfo = false
    @State private var selectedCategory = Titles.featureRequest
    @State private var isShowingMailView = false
    @State private var mailResult: Result<MFMailComposeResult, Error>? = nil

    private let categories = [
        Titles.featureRequest,
        Titles.bugReport,
        Titles.paymentIssue,
        Titles.serviceInquiry,
        Titles.others
    ]

    var body: some View {
        Form {
            Section {
                Menu {
                    ForEach(categories, id: \.self) { category in
                        Button(category) {
                            selectedCategory = category
                        }
                    }
                } label: {
                    HStack {
                        Text(selectedCategory)
                        Spacer()
                        Image(systemName: "chevron.down")
                            .foregroundStyle(Color.defaultGray)
                    }
                }
            } header: {
                Text(Titles.topicSelection)
            }
            Section {
                HStack {
                    Image(systemName: agreeToCollectDeviceInfo ? "checkmark.square" : "square")
                        .foregroundStyle(agreeToCollectDeviceInfo ? .green : .accentColor)
                    Text(Titles.agreeToCollectPersonalInfo)
                        .regularBold()
                }
                .onTapGesture {
                    self.agreeToCollectDeviceInfo.toggle()
                }
                Text(Titles.privacyPolicyAgreementText)
                    .extraSmall()
                    .lineSpacing(4)
                    .foregroundStyle(.gray)
            }
            Section {
                EmptyView()
            } footer: {
                CommonButton(Titles.sendFeedback, isDisabled: !agreeToCollectDeviceInfo) {
                    if MFMailComposeViewController.canSendMail() {
                        isShowingMailView = true
                    } else {
                        let plainTextBody = convertHtmlToPlainText(html: getEmailBody())
                        if let mailURLString = "mailto:\(Titles.exchangeDiaryMail)?subject=\(getEmailSubject())&body=\(plainTextBody)".addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed),
                           let mailURL = URL(string: mailURLString), UIApplication.shared.canOpenURL(mailURL) {
                            UIApplication.shared.open(mailURL)
                        } else {
                            AlertManager.shared.setError(Errors.mailFailure)
                        }
                    }
                }
            }
        }
        .navigationBarTitle(Titles.sendFeedback, displayMode: .inline)
        .sheet(isPresented: $isShowingMailView, onDismiss: checkMailResult) {
            MailView(
                isShowing: self.$isShowingMailView,
                result: $mailResult,
                subject: getEmailSubject(),
                body: getEmailBody()
            )
        }
    }

    private func getEmailSubject() -> String {
        return "[\(selectedCategory)] \(Titles.mailSubject)"
    }

    private func getEmailBody() -> String {
        let memberId = MyProfileManager.shared.myProfile.memberId
        let nickname = MyProfileManager.shared.myProfile.nickname
        let deviceModel = UIDevice.modelName
        let osVersion = UIDevice.current.systemVersion
        let appVersion = getAppVersion()
        let htmlBody = """
    <p>\(Titles.messageBody)</p>
    <p><b>\(Titles.inquiryContent) :</b></p>
    <br />
    <br />
    <p><b>\(Titles.inquiryImageOrVideo) :</b></p>
    <br />
    <br />
    <p>\(Titles.keepInformationRequest)</p>
    <p>
    <b>[INFORMATION]</b>
    </p>
    <p>MemberId: \(memberId)<br />
    Nickname: \(nickname)<br />
    Model: \(deviceModel)<br />
    OS Version: \(osVersion)<br />
    App Version: \(appVersion)<br />
    </p>
    <p>
      \(Titles.privacyPolicyAgreementText)
    </p>
    """

        return htmlBody
    }

    private func getAppVersion() -> String {
        if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String,
           let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String {
            return "\(version).\(build)"
        }
        return "Unable to get app version information."
    }

    func convertHtmlToPlainText(html: String) -> String {
        return html.replacingOccurrences(of: "<[^>]+>", with: "", options: .regularExpression, range: nil)
    }

    private func checkMailResult() {
        if case .success(let result) = mailResult, result == .sent {
            dismiss()
        }
    }
}

#Preview {
    InquiryView()
}
