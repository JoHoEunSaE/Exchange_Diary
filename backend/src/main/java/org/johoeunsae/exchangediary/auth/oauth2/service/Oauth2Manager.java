package org.johoeunsae.exchangediary.auth.oauth2.service;

import java.util.Optional;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;

public interface Oauth2Manager {

	Optional<Oauth2LoginInfoVO> requestOauthLoginInfo(Oauth2LoginRequestVO dto);

	void revokeToken(Oauth2LoginRequestVO dto);
}
