package org.johoeunsae.exchangediary.auth.oauth2.login.naver;

import org.johoeunsae.exchangediary.auth.oauth2.login.Oauth2LoginClient;
import org.johoeunsae.exchangediary.auth.oauth2.vo.AccessTokenValidationVO;
import org.johoeunsae.exchangediary.auth.oauth2.vo.NaverLoginInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naver-client", url = "https://openapi.naver.com/")
public interface NaverFeignClient extends Oauth2LoginClient {

    @GetMapping("/v1/nid/verify")
    AccessTokenValidationVO verifyAccessToken(
            @RequestHeader("Authorization") String accessToken);

    @GetMapping("/v1/nid/me")
    NaverLoginInfoVO getLoginUserInfo(@RequestHeader("Authorization") String accessToken);
}
