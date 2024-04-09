//
//  NotesListOptionView.swift
//  frontend
//
//  Created by 신인호 on 2023/07/12.
//

import SwiftUI

struct NotesListOptionView: View {
//    @Binding var viewMode: ViewMode
    @Binding var sortOption: SortOption
    @Binding var isMenuPresented: Bool
    private var rotation: Angle {
        isMenuPresented ? .degrees(-180) : .degrees(0)
    }

    var body: some View {
        HStack {
            Menu {
                ForEach(SortOption.allCases, id: \.self) { option in
                    Button {
                        self.sortOption = option
                        isMenuPresented = false
                    } label: {
                        Text(option.title)
                    }
                }
            } label: {
                HStack {
                    Text(sortOption.title)
                        .small()
                    Image(systemName: "chevron.down")
                        .small()
                        .foregroundColor(.accentColor)
                        .rotationEffect(rotation)
                        .animation(.easeInOut(duration: 0.2), value: isMenuPresented)
                }
            }
            .menuStyle(DefaultMenuStyle())
            .buttonStyle(.plain)
            .fontWeight(.semibold)
            .onTapGesture {
                isMenuPresented.toggle()
            }
            Spacer()
//            Button {
//                viewMode = .grid
//            } label: {
//                Image(systemName: "square.grid.2x2")
//                    .large()
//                    .foregroundColor(viewMode == .grid ? .accentColor : .defaultGray)
//            }
//            Button {
//                viewMode = .list
//            } label: {
//                Image(systemName: "list.bullet")
//                    .large()
//                    .foregroundColor(viewMode == .list ? .accentColor : .defaultGray)
//            }
        }
        .padding(.bottom, 12)
    }
}

#Preview {
    NotesListOptionView(
//        viewMode: .constant(.grid),
        sortOption: .constant(.latest),
        isMenuPresented: .constant(false)
    )
}
