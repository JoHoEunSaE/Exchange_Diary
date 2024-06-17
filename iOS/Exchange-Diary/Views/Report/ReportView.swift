//
//  ReportView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 1/16/24.
//

import SwiftUI

struct ReportView: View {
    @Environment(\.dismiss) var dismiss
    @Binding var isReportScreenPresented: Bool
    @State var reportContent: String = ""
    var noteId: Int? = nil
    var memberId: Int? = nil
    var reportType: ReportOption
    var isValid: Bool { !reportContent.isEmpty }
    
    // TODO: 상세내용 길이 제한(추가)
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(reportType.description)
                .smallBold()
            Divider()
                .overlay(Color.line)
            TextField(Titles.reportContentPlaceholder, text: $reportContent, axis: .vertical)
            Spacer()
        }
        .padding(.horizontal, 20)
        .navigationBarBackButtonHidden()
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button {
                    dismiss()
                } label: {
                    Text(Titles.cancel)
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    Task {
                        do {
                            let report = ReportModel(reportType: reportType.rawValue, reason: reportContent)
                            if let noteId = noteId {
                                try await ReportManager.shared.reportNote(noteId: noteId , report)
                            } else if let memberId = memberId{
                                print("memberIdreport", memberId)
                                try await ReportManager.shared.reportMember(memberId: memberId, report)
                            }
                            self.isReportScreenPresented = false
                        } catch {
                            AlertManager.shared.setError(error as? ResponseErrorData)
                        }
                    }
                } label : {
                    Text(Titles.submit)
                        .tint(.defaultBlue)
                }
                .disabled(!isValid)
            }
        }
    }
}

#Preview {
    ReportView(isReportScreenPresented: .constant(false), reportType: .illegal)
}
