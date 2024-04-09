package org.johoeunsae.exchangediary.auth.oauth2.login;

import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginInfoVO;

public interface Oauth2Login {

	boolean isValid();

	Oauth2LoginInfoVO provideLoginInfo();
	void revokeToken();
}
