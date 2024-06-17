//
//  InviteSheetView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/09/05.
//

import SwiftUI

struct InviteSheetView: View {
    @State private var isShowingSaved = false
    @ObservedObject var diaryManager: DiaryViewModel
    
    var body: some View {
        VStack(alignment: .leading) {
            Button {
                copyToClipboard(text: diaryManager.invitationCode)
                isShowingSaved = true
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    isShowingSaved = false
                }
            } label: {
                ZStack(alignment: .center) {
                    Image("Ticket")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                    VStack {
                        if isShowingSaved == false {
                            Text(diaryManager.invitationCode)
                                .extraLargeBold()
                                .foregroundColor(.accentColor)
                            Text(Titles.copyCode)
                                .small()
                                .foregroundColor(.accentColor)
                        } else  {
                            Label(Titles.copyCodeComplete, systemImage: "checkmark")
                                .regularBold()
                                .foregroundColor(.accentColor)
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.bottom, 10)
            }
            .buttonStyle(ScaleButtonStyle())
            
            ShareLink(item: diaryManager.invitationCode) {
                Text(Titles.share)
                    .foregroundColor(.accentColor)
                    .padding(13)
                    .smallBold()
                    .frame(maxWidth: .infinity)
                    .background(Color.lightGray)
                    .cornerRadius(10)
            }
            .buttonStyle(NoneButtonStyle())
            Spacer()
        }
        .padding(EdgeInsets(top: 40, leading: 20, bottom: 20, trailing: 20))
        .onAppear {
            do {
                try diaryManager.getInvitationCode()
            } catch {
                AlertManager.shared.setError(error as? ResponseErrorData)
            }
        }
    }
    
    
    private func copyToClipboard(text: String) {
        let pasteboard = UIPasteboard.general
        pasteboard.string = text
    }
}

#Preview {
    InviteSheetView(diaryManager: DiaryViewModel(diaryId: 0))
}

