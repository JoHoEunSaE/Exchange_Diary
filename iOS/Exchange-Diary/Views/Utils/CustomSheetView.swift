//
//  CustomSheetView.swift
//  Exchange-Diary
//
//  Created by 신인호 on 2023/07/18.
//

import SwiftUI

struct CustomSheetView<Content: View>: View {
    @Binding var isPresented: Bool
    let content: Content
    
    init(isPresented: Binding<Bool>, @ViewBuilder content: () -> Content) {
        self._isPresented = isPresented
        self.content = content()
    }
    
    var body: some View {
        ZStack(alignment: .top) {
            content
                .padding(.top, 20)
        }
        .presentationDetents([.medium, .large])
        .edgesIgnoringSafeArea(.all)
        .presentationDragIndicator(.visible)
        .onTapGesture {
            isPresented = false
        }
        .background(Color.sheet)
    }
}
