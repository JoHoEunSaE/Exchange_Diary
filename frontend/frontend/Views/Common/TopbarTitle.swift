//
//  TopbarTitle.swift
//  frontend
//
//  Created by 신인호 on 2/13/24.
//

import SwiftUI

struct TopbarTitle: ToolbarContent {
    let title: String
    
    init(_ title: String) {
        self.title = title
    }
    
    var body: some ToolbarContent {
        ToolbarItem(placement: .principal) {
            Text(title)
                .sansSerifBold(17)
        }
    }
}

#Preview {
    NavigationStack {
        Text("hi!")
            .toolbar {
                TopbarTitle("타이틀")
            }
    }
}
