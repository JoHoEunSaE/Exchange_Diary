package org.johoeunsae.exchangediary.like.service;

public interface LikeService {
	void createLike(Long noteId, Long memberId);
	void deleteLike(Long noteId, Long memberId);
}
