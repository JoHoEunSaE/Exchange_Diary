//
//  Titles.swift
//  frontend
//
//  Created by 신인호 on 10/22/23.
//

import Foundation

struct Titles {
    // MARK: 공통
    static let appTitle = "공유일기"
    static let followerTitle = "팔로워"
    static let followingTitle = "팔로잉"
    static let followTitle = "팔로우"
    static let settings = "설정"
    static let complete = "완료"
    static let confirm = "확인"
    static let edit = "편집"
    static let submit = "제출"
    static let error = "오류"
    static let notice = "알림"
    static let cancel = "취소"
    static let post = "게시"
    static let share = "공유하기"
    static let delete = "삭제"
    static let user = "사용자"
    static let kick = "내보내기"

    // MARK: 로그인 페이지
    static let greetings = [
        "함께 쓰는 일기, 함께 나누는 이야기.",
        "우리의 추억을 한 권의 일기장에 담아요.",
        "공유 일기는 우리 관계의 아름다운 기록입니다.",
        "매일의 소소한 이야기를 함께 공유하며 소통하세요.",
        "친구와 함께 쓰는 일기, 우정을 더욱 깊게 합니다.",
        "서로의 일상을 공유하는 것이야말로 진정한 소통입니다.",
        "함께 쓰는 매일이 특별한 추억이 됩니다.",
        "서로의 마음을 더 가까이 느껴보세요.",
        "둘만의 비밀, 함께 쓰는 일기장에서 시작됩니다.",
        "서로의 삶을 공유하는 것은 놀라운 경험입니다.",
        "함께 쓰는 일기, 서로의 성장을 함께 기록하세요.",
        "공유 일기는 우리가 함께한 시간의 소중한 증거입니다.",
        "마음을 나누는 가장 아름다운 방법, 함께 쓰는 일기입니다.",
        "서로의 이야기를 통해 더 깊은 이해를 나누세요.",
        "공유 일기는 우리 사이의 소중한 다리가 됩니다.",
        "함께 쓰는 일기를 통해 서로를 더 가깝게 느낄 수 있어요.",
        "서로의 하루를 공유하며 더 특별한 관계를 만들어가세요.",
        "우리만의 이야기를 공유 일기로 풍부하게 만들어보세요.",
        "함께 쓰는 일기는 우리의 우정을 더욱 단단하게 합니다.",
        "공유 일기는 함께한 순간들을 영원히 기억하는 방법입니다.",
    ]

    // MARK: 온보딩
    static let addFirstDiary = "첫 일기장을\n만들어볼까요"
    static let clickOnDiaryCover = "일기장을 클릭해주세요."
    static let doItLater = "나중에 하기"
    static let goToHome = "홈으로 이동"
    static let welcomeTitle = "새로운 시작을\n함께해요"
    static let welcomeSubtitle = "여러분의 이야기가 지금부터 시작됩니다.\n서로의 일상을 나누며, 즐거운 추억을 함께 만들어보세요.\n간편하게 시작해 보세요!"
    
    // MARK: 설정
    static let AccountManger = "계정 관리"
    static let blockList = "차단 목록"
    static let userGuide = "이용 가이드"
    static let sendFeedback = "문의하기"
    static let information = "정보"
    static let terms = "이용약관"
    static let privacy = "개인정보처리방침"
    static let openSourceLicense = "오픈소스 라이센스"
    static let help = "도움말"
    static let logout = "로그아웃"
    static let logoutConfirm = "정말로 로그아웃 하시겠습니까?"
    static let accountDeletion = "탈퇴하기"
    static let copyrightText = "🅒 2023. Good42. all rights reserved."
    static let deleteCache = "캐시 삭제"

    // MARK: 탈퇴
    enum ReasonsForLeaving: String, CaseIterable {
        case `default` = "선택해주세요."
        case noNeed = "서비스가 필요하지 않게 되었어요."
        case difficult = "이용이 어려웠어요."
        case findBetterOne = "좀 더 나은 서비스를 찾았어요."
        case privacy = "개인 정보에 대한 걱정이 있었어요."
        case highCost = "비용이 부담되었어요."
        case etc = "기타 (자유롭게 작성해 주세요.)"
    }
    
    static let goodbyeTitle = "안녕히 가세요, \(MyProfileManager.shared.myProfile.nickname)님."
    static let goodbyeContent = "우리의 길이 여기까지인가 봅니다. \n가시는 길에 몇 가지만 귀띔해 드릴게요."
    
    static let eternalFarewellTitle = "영원한 이별"
    static let eternalFarewellContent = "탈퇴하시면 모든 정보가 사라져요. 마음 바뀌시면 언제든지 돌아오세요, 다만 기억들은 처음부터 새로 쌓아야 한다는 점 잊지 마시고요"
    
    static let keepInMindTitle = "유념해주세요"
    static let keepInMindContent = "서비스 이용 기록은 복구할 수 없어요. 그 모든 추억들이 사라지니, 한번 더 생각해 주세요."
    
    static let breakTitle = "다시 만날 날을 위해 잠시 멈춤 버튼을 누르다"
    static let breakContent = "우리 사이에 잠시 공간이 필요한 건지도 몰라요. 탈퇴하시면 동일한 이메일이나 사용자명으로 바로 다시 만날 수 없게 됩니다. 재회를 꿈꾸신다면, 조금 시간을 두고 다시 시작하는 걸 추천드려요. 서로에게 좋은 변화의 시간을 가질 수 있으니까요."
    
    static let feedbackTitle = "우리의 부족한 점, 꼭 알려주세요."
    static let feedbackContent = "다음에 만날 때 더 나아진 모습을 보여드리고 싶어요.\n무엇이 여러분을 떠나게 했는지, 진심을 담아 말씀해 주시면 감사하겠습니다."
    
    static let farewellMomentTitle = "이별의 순간"
    static let farewellMomentContent = "그래도 정말 떠나실 건가요?\n아쉬움이 남지만, 여러분의 결정을 존중하며 배웅하겠습니다. 언제든지 돌아오실 수 있어요, 우리는 항상 여기 있을 거니까요."
    
    static let thinkMore = "잠시 더 생각해볼게요"
    static let leaveForever = "탈퇴하기"
    
    static let writeReasonPlaceholder = "이유를 작성해 주세요"
    
    // MARK: 프로필 설정
    static let profileEdit = "프로필 수정"
    static let userNamePlaceholder = "닉네임"
    static let userNameMake = "\(userNamePlaceholder)을\n 만들어주세요"
    static let userNameUnique = "\(userNamePlaceholder)은 고유한 이름이어야 합니다."
    static let userNameLength = "\(Rules.nicknameMinLength)글자 이상, \(Rules.nicknameMaxLength)글자 이하여야 합니다."
    static let userNameValidChars = "한글, 영어, 숫자, -, _만 포함할 수 있습니다."
    static let statementPlaceholder = "한 줄 소개"
    static let selectFromLibrary = "라이브러리에서 선택"
    static let deleteCurrentPhoto = "현재 사진 삭제"
    
    // MARK: 프로필
    static let myNote = "내 글"
    static let scrappedNote = "스크랩한 글"
    static let otherProfileStatement = "안녕하세요 새로운 사용자입니다\n언제든 연락 주세요!"

    // MARK: 일기장 생성
    static let diaryTitle = "제목"
    static let groupName = "그룹명(선택)"
    static let selectCover = "커버 선택"
    static let createNew = "새로 만들기"
    static let addDiary = "일기장 추가"
    static let addDiaryConfirm = "일기장을 추가하시겠습니까?"

    // MARK: 일기장
    static let deleteDiaryTitle = "정말로 일기장을 삭제하시겠습니까?"
    static let removeRecordsTitle = "함께 했던 기록들과의 작별"
    static let removeRecordsContent = "일기장을 삭제하시면, 공유된 모든 기록과 사진들은 더 이상 함께 볼 수 없게 됩니다. 공동으로 쌓아올린 추억들을 놓아주시기 전에, 잠시 생각해보시는 건 어떨까요?"
    static let privateNoteTitle = "나만의 이야기를 간직하며"
    static let privateNoteContent = "일기장과의 이별을 고민하시기 전에, 함께 나누었던 공유 일기장 속에서 자신만의 이야기를 한 번 더 되새겨보세요. 여러분이 직접 쓴 글들은, 그 누구의 것도 아닌 여러분만의 귀중한 이야기들입니다. 일기장을 삭제하더라도, '내 글 보기' 기능을 통해 여러분이 남긴 글들을 언제든지 다시 읽을 수 있습니다."
    static let diaryLimit = "다이어리 최대 생성 갯수는 5개입니다"
    
    // MARK: 일기장 추가
    static let addDiaryButtonTitle = "일기장\n만들기"
    static let wrongInvitationCode = "일치하는 일기장이 없습니다 \n초대코드를 다시 한 번 확인해주세요."
    static let joinWithCode = "초대코드로 추가하기"
    static let copyCode = "클릭해서 복사"
    static let copyCodeComplete = "초대코드가 복사되었습니다!"
    static let codePlaceholder = "초대코드 입력"

    // MARK: 일기장 멤버
    static let memberList = "멤버 목록"
    static let delegateMasterAlert = "대표를 변경할 멤버가 부족합니다. \n 함께 기록할 멤버를 초대해보세요!"
    static let memberKick = "멤버 내보내기"
    static let memberKickMessage = "정말로 %@님을 내보내시겠습니까?"

    // MARK: 일기장 선택
    static let selectedDiaryPlaceholder = "일기장 선택"
    static let emptyDiaryListContent = "일기장이 없어요!\n일기장을 먼저 만들어 주세요"
    static let createDiaryRecommendButton = "일기장 만들러 가기"

    // MARK: 일기
    static let noteTitlePlaceholder = "제목"
    static let noteContentPlaceholder = "오늘의 글"
    static let createNoteTitle = "글쓰기"
    static let makeFirstNote = "첫 글을 작성해주세요"
    static let noteList = "글 목록"

    // MARK: 나가기 alert
    static let discardAlertTitle = "정말로 나가시겠습니까?"
    static let discard = "나가기"
    static let discardAlertDescription = "지금 나가시면 작성 중인 글이 삭제됩니다."

    // MARK: TextField alert
    static let blankTitleAlertTitle = "제목을 확인해주세요!"
    static let blankTitleAlertDescription = "제목을 입력하시면 계속 진행할 수 있어요."
    static let longAlertTitle = "글자가 너무 많아요!"
    static let longTitleAlertDescription = "제목은 \(Rules.noteTitleMaxLength)자까지 입력이 가능합니다."
    static let longContentAlertDescription = "내용은 \(Rules.noteContentMaxLength)자까지 입력이 가능합니다."

    // MARK: 사진
    static let addPhoto = "사진 추가"
    static let deletePhoto = "사진 삭제"
    
    // MARK: 알림
    static let noticeEmpty = "알림이 없어요!"
    static let noticeDeleteRule = "알림은 30일이 지나면 영구적으로 삭제 됩니다."
    
    // MARK: 차단
    static let noBlockedListMessage = "차단한 유저가 없습니다."
    static let blockedListTitle = "차단 목록"
    static let blockConfirmMessage = "님을 차단 해제하시겠습니까?"
    static let blockMessage = "차단"
    static let unblockMessage = "차단 해제"
    static let unblockAlertButton = "해제"
    static let blockedContent = "차단한 멤버의 글입니다."

    // MARK: 신고
    static let report = "신고"
    static let reportContentPlaceholder = "상세 내용을 작성해주세요"

    // MARK: 문의
    static let exchangeDiaryMail = "exchangediary42@gmail.com"
    static let mailSubject = "\(Titles.appTitle)에 \(Titles.sendFeedback)"
    static let messageBody = "문의 사항을 자세히 입력해주세요.☺️"
    static let privacyPolicyAgreementText = "문의하기에 포함된 개인정보는 문의 접수 및 고객 불만 해결을 위해 수집, 이용될 수 있습니다. 위와 같이 수집하는 개인정보에 동의하지 않거나, 개인정보 기재 거부가 가능합니다. 다만, 개인 정보가 확인되지 않는 경우 문제 처리 및 회신이 불가할 수 있습니다."
    static let keepInformationRequest = "문의 시 [INFORMATION] 내용을 그대로 두어 주실 것을 부탁드립니다. 빠른 해결을 위해 필요한 정보입니다. 감사합니다.😊"
    static let inquiryContent = "문의 내용"
    static let inquiryImageOrVideo = "문의 관련 이미지 또는 영상"
    static let topicSelection = "주제 선택"
    static let agreeToCollectPersonalInfo = "개인정보 수집에 동의합니다. (필수)"
    static let featureRequest = "기능 요청"
    static let bugReport = "버그 신고"
    static let paymentIssue = "결제 문제"
    static let serviceInquiry = "서비스 이용 문의"
    static let others = "기타"
}
