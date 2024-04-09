package org.johoeunsae.exchangediary.block.domain;

import org.johoeunsae.exchangediary.dto.entity.MemberFromTo;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.OauthInfo;
import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import utils.bean.JPAQueryFactoryConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JPAQueryFactoryConfig.class)
class BlockTest {
	@PersistenceContext
	EntityManager em;
	Member from;
	Member to;

	@BeforeEach
	void setUp() throws Exception {
		from = Member.createSocialMember(MemberFeatures.of("from", "from"), LocalDateTime.now(),
				OauthInfo.of("from", OauthType.NAVER));
		to = Member.createSocialMember(
				MemberFeatures.of("to", "to"), LocalDateTime.now(), OauthInfo.of("to", OauthType.NAVER));
		em.persist(from);
		em.persist(to);
		em.flush();
		em.clear();
	}

	@DisplayName("toString")
	@Test
	void toStringTest() throws Exception {
		Block block = Block.of(MemberFromTo.of(from, to), LocalDateTime.now());
		assertThat(block.toString()).isInstanceOf(String.class);
	}

	@DisplayName("equals")
	@Test
	void equalsTest() throws Exception {
		Block block1 = Block.of(MemberFromTo.of(from, to), LocalDateTime.now());
		Block block2 = Block.of(MemberFromTo.of(from, to), LocalDateTime.now());
		Block block3 = Block.of(MemberFromTo.of(to, from), LocalDateTime.now());
		em.persist(block1);
		em.persist(block3);
		em.flush();
		em.clear();
		assertThat(block1).isEqualTo(block2);
		assertThat(block1).isNotEqualTo(block3);
	}

	@DisplayName("valid")
	@Test
	void validTest() throws Exception {
		Block block = Block.of(MemberFromTo.of(from, to), LocalDateTime.now());
		Block block2 = Block.of(MemberFromTo.of(to, from), null);
		assertThat(block.isValid()).isTrue();
		assertThat(block2.isValid()).isFalse();
	}
}