package org.johoeunsae.exchangediary.blacklist.repository;

import org.johoeunsae.exchangediary.blacklist.domain.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

}
