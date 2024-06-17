//
//  AddDiaryButtonView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/06/10.
//

import SwiftUI

struct AddDiaryButtonView: View {
    var body: some View {
        ZStack(){
            Rectangle()
                .foregroundColor(.reverseAccentColor)
                .clipShape(RoundedCorner(corner: [.topRight, .bottomRight], radius: 10))
                .shadow(color: .black.opacity(0.1), radius: 21, x: 10, y: 10)
            VStack(alignment: .leading) {
                Text(Titles.addDiaryButtonTitle)
                    .serif(16)
                    .lineSpacing(6)
                    .foregroundColor(Color.accentColor)
                Spacer()
                HStack {
                    Spacer()
                    Image("PlusCircle")
                        .resizable()
                        .frame(width: 35, height: 35)
                }
            }
            .padding(12)
            .padding(.top, 8)
        }        
    }
}

struct AddDiaryButtonView_Previews: PreviewProvider {
    static var previews: some View {
        AddDiaryButtonView()
    }
}
