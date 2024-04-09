DROP TABLE IF EXISTS auth_password;
DROP TABLE IF EXISTS auth_social;
DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS device_registry;
DROP TABLE IF EXISTS cover_color;
DROP TABLE IF EXISTS cover_image;
DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS member_token;
DROP TABLE IF EXISTS bookmark;
DROP TABLE IF EXISTS `like`;
DROP TABLE IF EXISTS note_image;
DROP TABLE IF EXISTS note_read;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS blacklist;
DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS diary;

-- Member 테이블
DROP TABLE IF EXISTS member;
CREATE TABLE member (
                        login_type varchar(31) not null,
                        id bigint auto_increment primary key,
                        created_at datetime not null,
                        deleted_at datetime null,
                        email varchar(255) not null,
                        last_logged_in_at datetime not null,
                        nickname varchar(15) collate utf8mb3_bin not null, -- 대소문자 구분을 위함
                        nickname_updated_at datetime not null,
                        profile_image_url varchar(255) null,
                        role varchar(20) not null,
                        statement varchar(31) null,
                        CONSTRAINT UK_member_nickname UNIQUE (nickname),
                        CONSTRAINT UK_member_email UNIQUE (email)
);

-- Auth Password 테이블
CREATE TABLE auth_password (
                               password varchar(255) not null,
                               username varchar(31) not null,
                               id bigint not null primary key,
                               CONSTRAINT UK_auth_password_username UNIQUE (username),
                               CONSTRAINT FK_auth_password_member_id FOREIGN KEY (id) REFERENCES member (id)
);

-- Blacklist 테이블
CREATE TABLE blacklist (
                           id bigint auto_increment primary key,
                           ended_at datetime not null,
                           started_at datetime not null,
                           member_id bigint not null,
                           CONSTRAINT FK_blacklist_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Block 테이블
CREATE TABLE block (
                       member_id bigint not null,
                       target_member_id bigint not null,
                       blocked_at datetime not null,
                       PRIMARY KEY (member_id, target_member_id),
                       CONSTRAINT FK_block_target_member_id FOREIGN KEY (target_member_id) REFERENCES member (id),
                       CONSTRAINT FK_block_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);
-- Device Registry 테이블
CREATE TABLE device_registry (
                                 id bigint auto_increment primary key,
                                 created_at datetime not null,
                                 token varchar(255) not null,
                                 member_id bigint not null,
                                 CONSTRAINT FK_device_registry_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Diary 테이블
CREATE TABLE diary (
                       id bigint auto_increment primary key,
                       cover_type varchar(255) not null,
                       created_at datetime not null,
                       group_name varchar(15) null,
                       title varchar(31) not null,
                       updated_at datetime not null,
                       master_member_id bigint not null,
                       CONSTRAINT FK_diary_master_member_id FOREIGN KEY (master_member_id) REFERENCES member (id)
);

-- Cover Color 테이블
CREATE TABLE cover_color (
                             id bigint auto_increment primary key,
                             color_code varchar(255) not null,
                             diary_id bigint not null,
                             CONSTRAINT FK_cover_color_diary_id FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE
);

-- Cover Image 테이블
CREATE TABLE cover_image (
                             id bigint auto_increment primary key,
                             image_url varchar(255) not null,
                             diary_id bigint not null,
                             CONSTRAINT FK_cover_image_diary_id FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE
);

-- Follow 테이블
CREATE TABLE follow (
                        member_id bigint not null,
                        target_member_id bigint not null,
                        created_at datetime not null,
                        PRIMARY KEY (member_id, target_member_id),
                        CONSTRAINT FK_follow_member_id FOREIGN KEY (member_id) REFERENCES member (id),
                        CONSTRAINT FK_follow_target_member_id FOREIGN KEY (target_member_id) REFERENCES member (id)
);

-- Member Token 테이블
CREATE TABLE member_token (
                              member_id bigint not null primary key,
                              created_at datetime not null,
                              token varchar(255) null,
                              CONSTRAINT FK_member_token_member_id FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

-- Auth Social 테이블
CREATE TABLE auth_social (
                             oauth_id varchar(255) not null,
                             oauth_type varchar(20) not null,
                             id bigint not null primary key,
                             member_token_member_id bigint null,
                             CONSTRAINT FK_auth_social_member_id FOREIGN KEY (id) REFERENCES member (id),
                             CONSTRAINT FK_auth_social_member_token_id FOREIGN KEY (member_token_member_id) REFERENCES member_token (member_id)
);

-- Note 테이블
CREATE TABLE note (
                      id bigint auto_increment primary key,
                      content varchar(4095) not null,
                      created_at datetime not null,
                      diary_id bigint default 0 null,
                      title varchar(31) not null,
                      updated_at datetime not null,
                      visible_scope varchar(20) not null,
                      member_id bigint not null,
                      deleted_at datetime null,
                      CONSTRAINT FK_note_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Bookmark 테이블
CREATE TABLE bookmark (
                          member_id bigint not null,
                          note_id bigint not null,
                          created_at datetime not null,
                          PRIMARY KEY (member_id, note_id),
                          CONSTRAINT FK_bookmark_note_id FOREIGN KEY (note_id) REFERENCES note (id),
                          CONSTRAINT FK_bookmark_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Like 테이블
CREATE TABLE `like` (
                        member_id bigint not null,
                        note_id bigint not null,
                        liked_at datetime not null,
                        PRIMARY KEY (member_id, note_id),
                        CONSTRAINT FK_like_note_id FOREIGN KEY (note_id) REFERENCES note (id),
                        CONSTRAINT FK_like_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Note Image 테이블
CREATE TABLE note_image (
                            id bigint auto_increment primary key,
                            image_url varchar(255) not null,
                            `index` int not null,
                            note_id bigint not null,
                            CONSTRAINT FK_note_image_note_id FOREIGN KEY (note_id) REFERENCES note (id)
);

-- Note Read 테이블
CREATE TABLE note_read (
                           member_id bigint not null,
                           note_id bigint not null,
                           counts int not null,
                           read_at datetime not null,
                           PRIMARY KEY (member_id, note_id),
                           CONSTRAINT FK_note_read_note_id FOREIGN KEY (note_id) REFERENCES note (id),
                           CONSTRAINT FK_note_read_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Notice 테이블
CREATE TABLE notice (
                        id bigint auto_increment primary key,
                        content varchar(255) not null,
                        create_at datetime not null,
                        from_id bigint null,
                        member_id bigint not null,
                        notice_type varchar(20) not null,
                        title varchar(63) null,
                        to_id bigint null,
                        created_at datetime not null,
                        read_at datetime null,
                        receiver_id bigint not null,
                        CONSTRAINT FK_notice_receiver_id FOREIGN KEY (receiver_id) REFERENCES member (id),
                        CONSTRAINT FK_notice_member_id FOREIGN KEY (member_id) REFERENCES member (id)
);

-- Registration 테이블
CREATE TABLE registration (
                              diary_id bigint not null,
                              member_id bigint not null,
                              registered_at datetime not null,
                              PRIMARY KEY (diary_id, member_id),
                              CONSTRAINT FK_registration_member_id FOREIGN KEY (member_id) REFERENCES member (id),
                              CONSTRAINT FK_registration_diary_id FOREIGN KEY (diary_id) REFERENCES diary (id)
);

-- Report 테이블
CREATE TABLE report (
                        id bigint auto_increment primary key,
                        create_at datetime not null,
                        reason varchar(20) not null,
                        blacklist_id bigint null,
                        report_member_id bigint not null,
                        note_id bigint null,
                        reported_member_id bigint not null,
                        report_type varchar(20) not null,
                        CONSTRAINT FK_report_blacklist_id FOREIGN KEY (blacklist_id) REFERENCES blacklist (id),
                        CONSTRAINT FK_report_note_id FOREIGN KEY (note_id) REFERENCES note (id),
                        CONSTRAINT FK_report_reported_member_id FOREIGN KEY (reported_member_id) REFERENCES member (id),
                        CONSTRAINT FK_report_report_member_id FOREIGN KEY (report_member_id) REFERENCES member (id)
);
