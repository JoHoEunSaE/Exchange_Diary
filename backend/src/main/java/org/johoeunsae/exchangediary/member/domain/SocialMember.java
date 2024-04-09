package org.johoeunsae.exchangediary.member.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AUTH_SOCIAL") @Entity
@DiscriminatorValue(SocialMember.SOCIAL_MEMBER)
public class SocialMember extends Member {
    /* 고유 정보 { */
    @Column(name = "OAUTH_ID", nullable = false)
    private String oauthId;
    @Enumerated(EnumType.STRING)
    @Column(name = "OAUTH_TYPE", nullable = false, length = 20)
    private OauthType oauthType;

    @OneToOne(fetch = FetchType.LAZY)
    private MemberToken memberToken;
    /* } 고유 정보 */

    /* 생성자 { */
    protected SocialMember(
        String nickname, String email, MemberRole role, LocalDateTime now,
        String oauthId, OauthType oauthType) {
        super(nickname, email, role, now);
        this.oauthId = oauthId;
        this.oauthType = oauthType;
    }
    public static SocialMember of(
        String nickname, String email, MemberRole role, LocalDateTime now,
        String oauthId, OauthType oauthType
    ) {
        return new SocialMember(nickname, email, role, now, oauthId, oauthType);
    }
    /* } 생성자 */

    public void setMemberToken(MemberToken memberToken) {
        this.memberToken = memberToken;
    }
}
