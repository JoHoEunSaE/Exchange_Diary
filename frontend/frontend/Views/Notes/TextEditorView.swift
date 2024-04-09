//
//  TextEditorView.swift
//
//
//  Created by 신인호 on 3/13/24.
//
import SwiftUI
import RichTextKit

struct TextEditorView: View {
    @Binding var text: String
    @StateObject var context = RichTextContext()

    private var attributedText: Binding<NSAttributedString> {
        Binding<NSAttributedString>(
            get: { NSAttributedString(string: self.text) },
            set: { self.text = $0.string }
        )
    }


    var body: some View {
        VStack {
            RichTextEditor(text: attributedText, context: context)
        }
        .focusedValue(\.richTextContext, context)
    }
}

#Preview {
    TextEditorView(text: .constant("Type!Type!!Type!Type!\nType!Type!Type!Type!Type!"))
}
