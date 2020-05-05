package com.lemon.oauth2.custom.author;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lemon
 * @description 自定义授权/Token请求创建服务
 * {@link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint#authorize} /oauth/authorize 调用该类创建 AuthorizationRequest
 * {@link org.springframework.security.oauth2.provider.endpoint.TokenEndpoint#postAccessToken} /oauth/token 调用该类创建 TokenRequest
 * {@link org.springframework.security.oauth2.provider.token.AbstractTokenGranter#getOAuth2Authentication} 调用该类创建 OAuth2Request，封装成 OAuth2Authentication
 * @date 2020-05-05 21:18
 */
@Slf4j
@Component("customOAuth2RequestFactory")
public class CustomOAuth2RequestFactory extends DefaultOAuth2RequestFactory implements OAuth2RequestFactory, InitializingBean {
    @Value("${lemon.oauth2.checkUserScopes:false}")
    private boolean checkUserScopes;

    @Autowired(required = false)
    private SecurityContextAccessor securityContextAccessor;

    public CustomOAuth2RequestFactory(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
        log.info("初始化自定义认证请求/Token请求封装服务类 {}", CustomOAuth2RequestFactory.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setCheckUserScopes(this.checkUserScopes);

        if (this.securityContextAccessor != null) {
            super.setSecurityContextAccessor(this.securityContextAccessor);
        }
    }

    @Override
    public AuthorizationRequest createAuthorizationRequest(Map<String, String> map) {
        return super.createAuthorizationRequest(map);
    }

    @Override
    public OAuth2Request createOAuth2Request(AuthorizationRequest authorizationRequest) {
        return super.createOAuth2Request(authorizationRequest);
    }

    @Override
    public OAuth2Request createOAuth2Request(ClientDetails clientDetails, TokenRequest tokenRequest) {
        return super.createOAuth2Request(clientDetails, tokenRequest);
    }

    @Override
    public TokenRequest createTokenRequest(Map<String, String> map, ClientDetails clientDetails) {
        return super.createTokenRequest(map, clientDetails);
    }

    @Override
    public TokenRequest createTokenRequest(AuthorizationRequest authorizationRequest, String grantType) {
        return super.createTokenRequest(authorizationRequest, grantType);
    }
}
