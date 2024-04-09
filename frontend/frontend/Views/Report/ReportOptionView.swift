//
//  ReportOptionView.swift
//  frontend
//
//  Created by Katherine JANG on 1/16/24.
//

import SwiftUI

struct ReportOptionView: View {
    @Binding var isReportScreenPresented: Bool
    var noteId: Int? = nil
    var memberId: Int? = nil
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 26) {
                    ForEach(ReportOption.allCases, id: \.self) { option in
                        NavigationLink {
                            ReportView(isReportScreenPresented: $isReportScreenPresented, noteId: noteId, memberId: memberId, reportType: option)
                        } label: {
                            HStack {
                                Text(option.description)
                                    .smallBold()
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .foregroundStyle(Color.defaultGray)
                            }
                        }
                    }
                    Spacer()
                }
                .padding(.horizontal, 20)
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button {
                            self.isReportScreenPresented = false
                        } label: {
                            Image(systemName: "xmark")
                        }
                    }
                    TopbarTitle(Titles.report)
                }
            }
        }
    }
}

#Preview {
    ReportOptionView(isReportScreenPresented: .constant(false))
}
