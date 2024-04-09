package org.johoeunsae.exchangediary.member.domain;


import javax.persistence.DiscriminatorValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter @ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AUTH_PASSWORD") @Entity
@DiscriminatorValue(PasswordMember.PASSWORD_MEMBER)
public class PasswordMember extends Member {
    /* 고유 정보 { */
    @Column(name = "USERNAME", nullable = false, unique = true, length = 31)
    private String username;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    /* } 고유 정보 */

    /* 생성자 { */
    protected PasswordMember(
        String nickname, String email, MemberRole role, LocalDateTime now,
        String username, String password) {
        super(nickname, email, role, now);
        this.username = username;
        this.password = password;
    }

    public static PasswordMember of(
            MemberFeatures identity, MemberRole role, LocalDateTime now, PasswordInfo passwordInfo) {
        return new PasswordMember(identity.getNickname(), identity.getEmail(), role, now,
                passwordInfo.getUsername(), passwordInfo.getPassword());
    }
    /* } 생성자 */

    /* update { */
    public void updatePassword(String password) {
        this.password = password;
    }
    /* } update */

    public boolean isPasswordMatch(String password) {
        return this.password.equals(password);
    }
}
