//
//  SkeleltonNoteView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2/5/24.
//

import SwiftUI

struct SkeleltonNoteView: View {
    var body: some View {
        ScrollView {
            VStack {
                VStack(spacing: 8) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.skeletonColor)
                        .frame(width: 220, height: 24)
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.skeletonColor)
                        .frame(width: 60, height: 18)
                    HStack(spacing: 4) {
                        Circle()
                            .fill(Color.skeletonColor)
                            .frame(width: 18, height: 18)
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.skeletonColor)
                            .frame(width: 50, height: 18)
                    }
                    .padding(.vertical, 16)
                }
                .padding(8)
                VStack {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.skeletonColorDark)
                        .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.width)
                    VStack(alignment: .leading, spacing: 12) {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.skeletonColor)
                            .frame(maxWidth: .infinity, idealHeight: 18)
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.skeletonColor)
                            .frame(maxWidth: .infinity, idealHeight: 18)
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.skeletonColor)
                            .frame(width: 260, height: 18)
                    }
                    .padding(20)
                }
            }
            .padding(.vertical, 20)
            .shimmering()
            .background(Color.defaultBackground)
        }
    }
}

#Preview {
    SkeleltonNoteView()
}
