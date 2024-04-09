package org.johoeunsae.exchangediary.auth.oauth2.service;

import java.time.LocalDateTime;
import org.johoeunsae.exchangediary.auth.oauth2.domain.UserSessionDto;
import org.johoeunsae.exchangediary.auth.oauth2.dto.UnregisterRequestDTO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.LoginResultVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;

public interface Oauth2Service {

	LoginResultVO login(Oauth2LoginRequestVO dto, LocalDateTime now);

	void unregister(UserSessionDto userDto, UnregisterRequestDTO requestDto);
}
