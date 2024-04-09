package org.johoeunsae.exchangediary.block.service;

public interface BlockService {
	void blockUser(Long loginMemberId, Long targetMemberId);
	void unblockUser(Long loginMemberId, Long targetMemberId);
}
