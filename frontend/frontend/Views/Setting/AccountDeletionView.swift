//
//  SignOut.swift
//  frontend
//
//  Created by 신인호 on 11/18/23.
//

import SwiftUI

struct AccountDeletionView: View {
    @Environment(\.dismiss) var dismiss
    @State var selectedReason: Titles.ReasonsForLeaving = .default
    @State private var otherReason = ""
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 30) {
                MessageView(title: Titles.goodbyeTitle, content: Titles.goodbyeContent, titleFontSize: 28, contentFontSize: 16)
                Divider()
                VStack(alignment: .leading, spacing: 20) {
                    MessageView(title: Titles.eternalFarewellTitle, content: Titles.eternalFarewellContent)
                    MessageView(title: Titles.keepInMindTitle, content: Titles.keepInMindContent)
                    MessageView(title: Titles.breakTitle, content: Titles.breakContent)
                }
                Divider()
                MessageView(title: Titles.feedbackTitle, content: Titles.feedbackContent, titleFontSize: 28, contentFontSize: 16)
                DropDownView(selectedReason: $selectedReason, otherReason: $otherReason)
                Divider()
                MessageView(title: Titles.farewellMomentTitle, content: Titles.farewellMomentContent, titleFontSize: 28, contentFontSize: 16)
                ActionButtons(buttonDisabled: (selectedReason == .default), dismiss: dismiss) {
                    let unregisterReason = selectedReason == .etc ? otherReason : selectedReason.rawValue
                    AuthenticationManager.shared.unregisterService(unregisterReason)
                }
            }
            .padding(30)
        }
        .onAppear{
            Task {
                AuthenticationManager.shared.reissueToken()
            }
        }
    }
}

private struct MessageView: View {
    let title: String
    let content: String
    var titleFontSize: CGFloat = 14
    var contentFontSize: CGFloat = 14

    var body: some View {
        VStack(alignment: .leading) {
            Text(title)
                .font(.system(size: titleFontSize, weight: .bold))
                .padding(.bottom, titleFontSize == 28 ? 16 : 2)
            Text(content)
                .font(.system(size: contentFontSize))
        }
    }
}


private struct DropDownView: View {
    @Binding var selectedReason: Titles.ReasonsForLeaving
    @Binding var otherReason: String
    
    var body: some View {
        VStack {
            Menu {
                ForEach(Titles.ReasonsForLeaving.allCases, id: \.self) { reason in
                    Button(reason.rawValue) {
                        selectedReason = reason
                    }
                }
            } label: {
                HStack {
                    Text(selectedReason.rawValue)
                        .foregroundColor(.accentColor)
                    Spacer()
                    Image(systemName: "chevron.down")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(RoundedRectangle(cornerRadius: 10).stroke(Color.lightGray))
            }
            if selectedReason == .etc {
                ZStack {
                    TextEditor(text: $otherReason)
                        .frame(minHeight: 20, maxHeight: .infinity)
                        .padding()
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(Color.lightGray, lineWidth: 1)
                        )
                    if otherReason.isEmpty {
                        Text(Titles.writeReasonPlaceholder)
                            .foregroundColor(.gray)
                            .padding(.all, 8)
                            .allowsHitTesting(false)
                    }
                }
            }
        }
    }
}

private struct ActionButtons: View {
    var buttonDisabled: Bool
    var dismiss: DismissAction
    let tabUnregister: () -> Void

    var body: some View {
        HStack {
            CommonButton(Titles.thinkMore) {
                dismiss()
            }
            CommonButton(Titles.leaveForever) {
                tabUnregister()
            }
            .pale()
            .disabled(buttonDisabled)
        }
    }
}

#Preview {
    AccountDeletionView()
}
