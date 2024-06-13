//
//  DiaryView.swift
//  Exchange-Diary
//
//  Created by Katherine JANG on 6/3/23.
//

import SwiftUI
import _PhotosUI_SwiftUI

// FIXME: user local 시간으로 변경 또는 바로 Date로 가져오기
func getCreateDate(_ fullDate: String) -> Substring{
    print("getCreateDate", fullDate)
    let date = fullDate.split(separator: "T").first ?? ""
    return date
}

// FIXME 겹치는 text font -> viewModifier로 빼기
struct DiaryView: View {
    @StateObject var diaryManager: DiaryViewModel
    @State var isSettingSheetPresented = false
    @State var showMembers: Bool = false
    @State var detentHeight: CGFloat = 0
    @State var diarySheetOptions: diarySettingOption = .setting
    @State var navigateToDiaryEdit: Bool = false
    @State var isReportScreenPresented = false
    @State var selectedMemberId: Int = 0
    @EnvironmentObject var path: PathViewModel
    var diaryId: Int
    
    @State private var selectedDetent: PresentationDetent = .medium
    
    init(diaryId: Int) {
        self.diaryId = diaryId
        _diaryManager = StateObject(wrappedValue: DiaryViewModel(diaryId: diaryId))
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .center) {
                if diaryManager.diaryInfo.coverType == .image {
                    LoadImageView(diaryManager.diaryInfo.coverData)
                        .cornerRadius(10)
                        .frame(width: 220, height: 220)
                        .shadow(color: .black.opacity(0.08), radius: 20, x: 10, y: 10)
                        .padding(.vertical, 16)
                } else {
                    DiaryColorCover
                }
                DiaryInfoText
                DiaryMembers
                Divider()
                    .overlay(Color.line)
                NotesListView(noteLists: diaryManager.noteList, upLoadLists: diaryManager.getNoteList) { noteId in
                    let idPair = DiaryNoteIdentifier(noteId: noteId, diaryId: diaryManager.diaryId)
                    path.navigateTo(.note, idPair)
                }
                .padding(.top, 10)
            }
            .padding(.horizontal, 20)
        }
        .refreshable {
            diaryManager.setDiary()
        }
        .fullScreenCover(isPresented: $isReportScreenPresented) {
            ReportOptionView(isReportScreenPresented: $isReportScreenPresented, memberId: selectedMemberId)
        }
        .sheet(isPresented: $isSettingSheetPresented) {
            DiarySettingSheetView(
                diaryManager: diaryManager,
                isSettingSheetPresented: $isSettingSheetPresented,
                selectedDetent: $selectedDetent
            )
            .presentationDetents([.medium, .large], selection: $selectedDetent)
        }
        .toolbar {
            TopbarTitle(diaryManager.diaryInfo.title)
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    self.isSettingSheetPresented = true
                } label: {
                    Image(systemName: "ellipsis")
                        .frame(width: 32, height: 32)
                }
            }
        }
        .overlay {
            diaryManager.isLoading ? SkeleltonDiaryView() : nil
        }
    }

    var DiaryImageCover: some View {
        LoadImageView(diaryManager.diaryInfo.coverData)
            .cornerRadius(10)
            .frame(width: 220, height: 220)
            .shadow(color: .black.opacity(0.08), radius: 20, x: 10, y: 10)
            .padding(.vertical, 16)
    }

    var DiaryColorCover: some View {
        ZStack(alignment: .center) {
            RoundedRectangle(cornerRadius: 10)
                .frame(width: 220, height: 220)
                .foregroundColor(Color(hexWithAlpha: diaryManager.diaryInfo.coverData))
            Image("TransparentLogo")
                .resizable()
                .frame(width: 220, height: 220)
        }
        .shadow(color: .black.opacity(0.08), radius: 20, x: 10, y: 10)
        .padding(.vertical, 16)
    }

    var DiaryInfoText: some View {
        VStack {
            Text(diaryManager.diaryInfo.groupName ?? "")
                .serif(18)
                .padding(.bottom, 2)
            Text(getCreateDate(diaryManager.diaryInfo.createdAt ?? ""))
                .serif(14)
                .foregroundColor(.defaultGray)
        }
    }

    var DiaryMembers: some View {
        Button {
            showMembers = true
        } label: {
            HStack(spacing: 4) {
                ProfileImageCircle
                Text("멤버 \(diaryManager.membersInfo.count)명")
                    .smallBold()
            }
        }
        .sheet(isPresented: $showMembers) {
            DiaryMemberSheetView(diaryManager: diaryManager, showMemberSheet: $showMembers,
                                 isReportScreenPresented: $isReportScreenPresented, selectedMemberId: $selectedMemberId)
            .presentationDetents([.medium, .large])
            .edgesIgnoringSafeArea(.all)
            .presentationDragIndicator(.visible)
        }
        .padding(8)
    }

    var ProfileImageCircle: some View {
        HStack(spacing: -10.0) {
            ForEach(diaryManager.membersInfo.indices, id: \.self) { index in
                if index < 3 { // 최대 3개의 이미지만 표시
                    LoadProfileImage(diaryManager.membersInfo[index].profileImageUrl, size: 24)
                }
            }
        }
    }
}
