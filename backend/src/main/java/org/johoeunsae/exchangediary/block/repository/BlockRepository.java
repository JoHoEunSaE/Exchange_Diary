package org.johoeunsae.exchangediary.block.repository;

import java.util.List;
import org.johoeunsae.exchangediary.block.domain.Block;
import org.johoeunsae.exchangediary.keys.MemberCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlockRepository extends JpaRepository<Block, MemberCompositeKey> {

	@Query("select b from Block b where b.id.memberId = :memberId")
	Page<Block> findAllByMemberId(Long memberId, Pageable pageable);

	@Query("select b from Block b where b.id.memberId = :memberId")
	List<Block> findAllByMemberId(Long memberId);

	@Query("select case when count(b) > 0 " +
			"then true else false end from Block b " +
			"where b.id.memberId = :memberId and b.id.targetMemberId = :targetMemberId")
	boolean existsByMemberIdAndTargetMemberId(Long memberId, Long targetMemberId);
}
