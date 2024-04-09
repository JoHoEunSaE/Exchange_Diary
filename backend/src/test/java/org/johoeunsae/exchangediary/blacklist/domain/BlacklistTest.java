package org.johoeunsae.exchangediary.blacklist.domain;

import org.johoeunsae.exchangediary.exception.DomainException;
import org.johoeunsae.exchangediary.member.domain.Member;
import org.johoeunsae.exchangediary.member.domain.MemberFeatures;
import org.johoeunsae.exchangediary.member.domain.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import utils.bean.JPAQueryFactoryConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@Import(JPAQueryFactoryConfig.class)
class BlacklistTest {
	@PersistenceContext
	EntityManager em;
	Member member1;
	Member member2;

	@BeforeEach
	void setup() {
		member1 = Member.of(MemberFeatures.of("test", "test"), MemberRole.USER, LocalDateTime.now());
		member2 = Member.of(MemberFeatures.of("test2", "test2"), MemberRole.USER, LocalDateTime.now());
		em.persist(member1);
		em.persist(member2);
		em.flush();
		em.clear();
	}

	@DisplayName("Blacklist toString 테스트")
	@Test
	void toStringTest() throws Exception {
		Blacklist blacklist = Blacklist.of(member1, LocalDateTime.now(), 1);
		assertThat(blacklist.toString()).isInstanceOf(String.class);
	}

	@Test
	void member는_null일수없음() throws Exception {
		assertThatThrownBy(() -> {
			Blacklist blacklist = Blacklist.of(null, LocalDateTime.now(), 1);
			em.persist(blacklist);
		});
	}

	@Test
	void member는_바뀔수없음() throws Exception {
		Blacklist blacklist = Blacklist.of(member1, LocalDateTime.now(), 1);
		em.persist(blacklist);
		em.flush();
		em.merge(blacklist);
		Field memberField = blacklist.getClass().getDeclaredField("member");
		// 강제로 private변수를 바꿔서 updateable을 확인한다.
		memberField.setAccessible(true);
		memberField.set(blacklist, member2);
		memberField.setAccessible(false);
		// 실제로 바뀌었는지 확인
		assertThat(blacklist.getMember().getNickname().equals(member2.getNickname())).isTrue();
		em.flush();
		// cache지우기
		em.clear();
		// DB는 바뀌지 않았는지 확인
		assertThat(em.find(Blacklist.class, blacklist.getId()).getMember().getNickname()).isEqualTo(member1.getNickname());
	}

	@Test
	void validTest() throws Exception {
		Blacklist ok = Blacklist.of(member1, LocalDateTime.now(), 1);

		Blacklist endMin = Blacklist.of(mock(Member.class), LocalDateTime.now(), LocalDateTime.MIN);

		assertThat(ok.isValid()).isTrue();
		assertThatThrownBy(() -> Blacklist.of(null, LocalDateTime.now(), 1))
				.isInstanceOf(DomainException.class);
		assertThat(endMin.isValid()).isFalse();
	}

	@Test
	void 생성자_테스트() throws Exception {
		Blacklist blacklist = Blacklist.of(member1, LocalDateTime.now(), 3);
		assertThat(blacklist.getStartedAt()).isEqualTo(blacklist.getEndedAt().minusDays(3));
	}
}