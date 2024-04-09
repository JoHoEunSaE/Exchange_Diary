package org.johoeunsae.exchangediary.member.repository;

import org.johoeunsae.exchangediary.member.domain.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
}
