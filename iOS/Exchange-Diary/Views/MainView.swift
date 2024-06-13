//
//  MainView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 6/27/23.
//

import SwiftUI

enum Tag {
    case home
    case explore
    case write
    case notification
    case profile
}

struct MainView: View {
    var myProfileManager = MyProfileManager.shared
    @ObservedObject var noteCreateManager = CreateNoteViewModel.shared

    @State var selectedTab = Tag.home
    @State private var oldSelectedItem = Tag.home
    @StateObject var path = PathViewModel()
    
    var selectedTabHandler: Binding<Tag> { Binding(
        get: { self.selectedTab }, set: {
            if $0 == Tag.home {
                path.targetDestination.removeAll()
            } else {
                NavigationUtils.popToRootView()
            }
            HapticManager.shared.impact(style: .light)
            self.selectedTab = $0
        })}

    var body: some View {
        TabView(selection: selectedTabHandler) {
            HomeView()
                .tabItem({
                    Text("")
                    Image(oldSelectedItem == Tag.home ? "homeIconFill" : "homeIcon")
                })
                .tag(Tag.home)
            Text("")
                .tabItem({
                    Text("")
                    Image("penIcon")
                })
                .tag(Tag.write)
            MyProfileView()
                .tabItem({
                    Text("")
                    Image(oldSelectedItem == Tag.profile ? "userIconFill" : "userIcon")
                })
                .tag(Tag.profile)
        }
        .onChange(of: selectedTab) { newValue in
            if newValue == Tag.write {
                noteCreateManager.isCreateNotePresented = true
                self.selectedTab = self.oldSelectedItem
            } else {
                self.oldSelectedItem = newValue
            }
        }
        .fullScreenCover(isPresented: $noteCreateManager.isCreateNotePresented) {
            CreateNoteView() {
                noteCreateManager.isCreateNotePresented = false
            }
        }
        .onAppear {
            myProfileManager.getMyProfile()
        }
        .onAppear {
            let tabBarAppearance = UITabBarAppearance()
            tabBarAppearance.configureWithDefaultBackground()
            UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
            
            print("token", getAccessToken() ?? "")
        }
        .environmentObject(path)
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
