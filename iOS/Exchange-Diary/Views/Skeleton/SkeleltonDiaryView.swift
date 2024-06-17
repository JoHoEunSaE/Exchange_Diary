//
//  SkeleltonDiaryView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2/4/24.
//

import SwiftUI

struct SkeleltonDiaryView: View {
    var body: some View {
        ScrollView {
            VStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.skeletonColorDark)
                    .frame(width: 220, height: 220)
                    .padding(.vertical, 16)
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.skeletonColor)
                    .frame(width: 80, height: 18)
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
                .padding(8)
                Divider()
                    .overlay(Color.line)
                    .padding(EdgeInsets(top: 0, leading: 20, bottom: 30, trailing: 20))
                VStack(spacing: 20) {
                    SkeletonNoteListView()
                    SkeletonNoteListView()
                    SkeletonNoteListView()
                    SkeletonNoteListView()
                }
                .padding(20)
            }
        }
        .shimmering()
        .background(Color.defaultBackground)
    }
}


struct SkeletonNoteListView: View {
    var body: some View {
        HStack {
            RoundedRectangle(cornerRadius: 4)
                .fill(Color.skeletonColor)
                .frame(maxWidth: .infinity, idealHeight: 60)
            RoundedRectangle(cornerRadius: 10)
                .fill(Color.skeletonColor)
                .frame(width: 60, height: 60)
        }
    }
}


#Preview {
    SkeleltonDiaryView()
}
