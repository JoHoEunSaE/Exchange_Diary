package org.johoeunsae.exchangediary.blacklist.service;

import org.johoeunsae.exchangediary.blacklist.domain.Blacklist;

public interface BlacklistService {

	Blacklist addBlacklist(Long loginMemberId);

}
