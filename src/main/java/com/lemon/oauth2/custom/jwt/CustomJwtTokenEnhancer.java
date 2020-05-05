package com.lemon.oauth2.custom.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Map;

/**
 * @author lemon
 * @description 自定义 JWT Token 增强处理服务，可以对 JWT Token 增加额外处理
 * @date 2020-05-05 21:19
 */
@Slf4j
@Component("customTokenEnhancer")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "jwt")
public class CustomJwtTokenEnhancer extends JwtAccessTokenConverter implements TokenEnhancer, AccessTokenConverter, InitializingBean {
    @Value("${lemon.oauth2.jwt.signingKey:}")
    private String signingKey;

    @Value("${lemon.oauth2.jwt.verifierKey:}")
    private String verifierKey;

    private Signer signer;

    private SignatureVerifier verifier;

    private KeyPair keyPair;

    private JwtClaimsSetVerifier jwtClaimsSetVerifier;

    @Autowired
    @Qualifier("customAccessTokenConverter")
    private AccessTokenConverter accessTokenConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.jwtAccessTokenConverter.setAccessTokenConverter(this.accessTokenConverter);
//        // PRIVATE KEY
//        this.jwtAccessTokenConverter.setSigningKey(this.signingKey);
//        // PUBLIC KEY
//        this.jwtAccessTokenConverter.setVerifierKey(this.verifierKey);
//        this.jwtAccessTokenConverter.setSigner(this.signer);
//        this.jwtAccessTokenConverter.setVerifier(this.verifier);
//        this.jwtAccessTokenConverter.setKeyPair(this.keyPair);
//        this.jwtAccessTokenConverter.setJwtClaimsSetVerifier(this.jwtClaimsSetVerifier);

        super.afterPropertiesSet();

        log.info("初始化JWT Token增强、转换服务类 {}", CustomJwtTokenEnhancer.class);
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        OAuth2AccessToken newOAuth2AccessToken = super.enhance(oAuth2AccessToken, oAuth2Authentication);

        if (log.isDebugEnabled()) {
            log.debug("原始Token[{}] 转换为JWT[{}]", oAuth2AccessToken.getValue(), newOAuth2AccessToken.getValue());
        }

        return newOAuth2AccessToken;
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        return super.convertAccessToken(oAuth2AccessToken, oAuth2Authentication);
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        return super.extractAccessToken(value, map);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        return super.extractAuthentication(map);
    }
}
