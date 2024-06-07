//
//  NoteInformationTextView.swift
//  frontend
//
//  Created by 김나연 on 3/28/24.
//

import SwiftUI

struct InformationText: View {
    let information: String

    var body: some View {
        Text(information)
            .foregroundColor(.defaultGray)
            .lineLimit(1)
            .fontWeight(.light)
            .extraSmall()
    }
}

struct InformationDivider: View {
    var body: some View {
        Circle()
            .fill(Color.defaultGray)
            .frame(width: 2.0)
    }
}

struct InformationHeart: View {
    var body: some View {
        Image(systemName: "heart")
            .foregroundColor(.defaultGray)
            .lineLimit(1)
            .fontWeight(.light)
            .font(.system(size: 11))
    }
}
