INSERT IGNORE INTO member (login_type, created_at, deleted_at, email, last_logged_in_at, nickname, nickname_updated_at, profile_image_url, role, statement) VALUES ('SOCIAL', '2024-01-01 20:34:34', null, 'a@a.com', '2024-01-01 20:34:34', 'nickname1', '2024-01-01 20:35:44', null, 'USER', '룰루랄라');
INSERT IGNORE INTO member (login_type, created_at, deleted_at, email, last_logged_in_at, nickname, nickname_updated_at, profile_image_url, role, statement) VALUES ('SOCIAL', '2024-01-01 20:34:34', null, 'b@b.com', '2024-01-01 20:34:34', 'nickname2', '2024-01-01 20:56:04', null, 'USER', 'Hello world!');
INSERT IGNORE INTO member (login_type, created_at, deleted_at, email, last_logged_in_at, nickname, nickname_updated_at, profile_image_url, role, statement) VALUES ('SOCIAL', '2024-01-01 20:34:34', null, 'c@c.com', '2024-01-01 20:34:34', 'nickname3', '2024-01-01 20:35:44', null, 'USER', 'zzz');

INSERT IGNORE INTO auth_social (oauth_id, oauth_type, id, member_token_member_id) VALUES ('123456789', 'KAKAO', 1, null);
INSERT IGNORE INTO auth_social (oauth_id, oauth_type, id, member_token_member_id) VALUES ('1234567890', 'KAKAO', 2, null);
INSERT IGNORE INTO auth_social (oauth_id, oauth_type, id, member_token_member_id) VALUES ('1234567891', 'KAKAO', 3, null);
