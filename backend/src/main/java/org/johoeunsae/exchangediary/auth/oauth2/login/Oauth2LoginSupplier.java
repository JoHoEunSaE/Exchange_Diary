package org.johoeunsae.exchangediary.auth.oauth2.login;

import org.johoeunsae.exchangediary.auth.oauth2.vo.Oauth2LoginRequestVO;

public interface Oauth2LoginSupplier {
	boolean supports(Oauth2LoginRequestVO dto);

	Oauth2Login supply(Oauth2LoginRequestVO dto);
}
