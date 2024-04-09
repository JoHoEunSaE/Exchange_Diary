//
//  NotiView.swift
//  frontend
//
//  Created by Katherine JANG on 6/29/23.
//

import SwiftUI

struct NoticeView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject var noticeManager = NoticeManager.shared

    var body: some View {
        VStack {
            BannerView()
                .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
            
            if noticeManager.notices.isEmpty {
                Spacer()
                Image("notiIconFill")
                    .opacity(0.3)
                Text(Titles.noticeEmpty)
                    .small()
                    .foregroundColor(Color.gray)
                Spacer()
            } else {
                List {
                    ForEach(noticeManager.groupedNotices.keys.sorted(by: { $0 > $1 }), id: \.self) { date in
                        Section(header: sectionHeader(for: date)) {
                            ForEach(noticeManager.groupedNotices[date]!, id: \.id) { notification in
                                NotificationRow(notification: notification)
                                    .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                                        Button {
                                            noticeManager.deleteNotice(notification)
                                        } label: {
                                            Image(systemName: "trash")
                                        }
                                        .tint(.red)
                                    }
                                    .listRowSeparator(.hidden)
                            }
                        }
                    }
                    HStack {
                        Spacer()
                        Text(Titles.noticeDeleteRule)
                            .extraSmall()
                            .foregroundColor(Color.defaultGray)
                            .listRowSeparator(.hidden)
                        Spacer()
                    }
                    .padding(40)
                    .listRowSeparator(.hidden)
                    .allowsHitTesting(false)
                }
                .listStyle(PlainListStyle())
            }
        }
        .toolbar(.hidden, for: .tabBar)
        .navigationTitle(Titles.notice)
        .navigationBarTitleDisplayMode(.inline)
        .task {
            await noticeManager.getNotices()
        }
        .onDisappear {
            Task {
                await noticeManager.apiDeleteNotices()
            }
        }
        .refreshable {
            Task {
                await noticeManager.apiDeleteNotices()
                await noticeManager.getNotices()
            }
        }
    }
    
    private func sectionHeader(for date: Date) -> some View {
        let formatter = DateFormatter()
        formatter.dateFormat = "MM.dd"
        let dateString = formatter.string(from: date)
        
        return Text(dateString)
            .largeBold()
            .foregroundColor(Color.accentColor)
            .padding(.vertical, 4)
    }
}

struct NotificationRow: View {
    @EnvironmentObject var path: PathViewModel
    let notification: Notice
    
    var body: some View {
        
        HStack(alignment: .top) {
            Image("Logo")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .clipShape(Circle())
                .frame(width: 20, height: 20)
            VStack(alignment: .leading, spacing: 6) {
                Text(notification.title)
                    .regularBold()
                    .foregroundStyle(Color.accentColor)
                parseMessage(notification.content)
                    .small()
                    .foregroundStyle(Color.accentColor)
                Text(formatDate(notification.createdAt))
                    .extraSmall()
                    .foregroundStyle(Color.defaultGray)
                    .padding(.top, 2)
            }
        }
    }
    
    func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: date)
    }
    
    func parseMessage(_ message: String) -> some View {
        let tokens = splitMessageByTags(message)
        
        // 각 태그별 처리 방식을 정의
        let tagActions: [String: (String, String) -> Void ] = [
            "MBR": {
                id, name in
                print("MBR - \(id), \(name) 프로필 뷰 열기")
                if let id = Int(id) {
                    path.navigateTo(.profile, id)
                }
            },
            "DRY": {
                id, name in
                print("DRY - \(id), \(name) 다이어리 뷰 열기")
                if let id = Int(id) {
                    path.navigateTo(.diary, id)
                }
            },
            "NTE": {
                id, name in
                print("NTE - \(id), \(name) 노트 뷰 열기")
                if let id = Int(id) {
                    path.navigateTo(.note, id)
                }
            },
            "FLW": {
                id, name in
                print("FLW - \(id), \(name) 팔로우 뷰 열기")
                if let id = Int(id) {
                    path.navigateTo(.profile, id)
                }
            }
        ]
        
        return HStack(spacing: 2) {
            ForEach(tokens, id: \.self) { token in
                if let (tag, id, name) = extractTagAndName(from: token), let action = tagActions[tag] {
                    Text(name)
                        .fontWeight(.semibold)
                        .onTapGesture {
                        action(id, name)
                    }
                } else {
                    Text(token)
                }
            }
        }
    }
    
    //  중괄호({}) 기준으로 토큰 나누기
    func splitMessageByTags(_ message: String) -> [String] {
        let pattern = "(\\{[^}]*\\})|([^{}]+)"
        guard let regex = try? NSRegularExpression(pattern: pattern) else { return [message] }
        let nsString = message as NSString
        let matches = regex.matches(in: message, range: NSRange(message.startIndex..., in: message))
        
        return matches.map { nsString.substring(with: $0.range) }
    }
    
    //  중괄호 {} {MBR|1|INSHIN} 파싱
    func extractTagAndName(from token: String) -> (String, String, String)? {
        guard token.starts(with: "{"), token.hasSuffix("}") else { return nil }
        let components = token.split(separator: "|")
        guard components.count == 3 else { return nil }
        let tag = String(components[0]).trimmingCharacters(in: CharacterSet(charactersIn: "{}"))
        let id = String(components[1]).trimmingCharacters(in: CharacterSet(charactersIn: "{}"))
        let name = urlDecode(String(components[2]).trimmingCharacters(in: CharacterSet(charactersIn: "{}")))

        return (tag, id, name)
    }

    // URL 디코딩
    func urlDecode(_ string: String) -> String {
        return string.removingPercentEncoding ?? string
    }
}

#Preview {
    NoticeView()
}
