package com.lemon.oauth2.custom.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author lemon
 * @description 自定义 JWT Token 增强处理服务，可以对 JWT Token 增加额外处理
 * @date 2020-05-05 21:19
 */
@Slf4j
@Component("customTokenEnhancer")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "jwt")
public class CustomJwtTokenEnhancer extends JwtAccessTokenConverter implements TokenEnhancer, AccessTokenConverter, InitializingBean, ResourceLoaderAware {
    @Value("${lemon.oauth2.jwt.keystorePath:classpath:jwt/keystore.jks}")
    private String keystorePath;

    @Value("${lemon.oauth2.jwt.keystorePassword:}")
    private String keystorePassword;

    @Value("${lemon.oauth2.jwt.keystoreAlias:}")
    private String keystoreAlias;

    @Value("${lemon.oauth2.jwt.publicKeyPath:classpath:jwt/publicKey.txt}")
    private String publicKeyPath;

    @Value("${lemon.oauth2.jwt.privateKeyPath:classpath:jwt/privateKey.txt}")
    private String privateKeyPath;

    @Autowired
    @Qualifier("customAccessTokenConverter")
    private AccessTokenConverter accessTokenConverter;

    private ResourceLoader resourceLoader;

    private JsonParser objectMapper = JsonParserFactory.create();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean exist = false;
        Resource resource;

        if (StringUtils.isNotBlank(this.keystorePath) && StringUtils.isNotBlank(this.keystorePassword) && StringUtils.isNotBlank(this.keystoreAlias)) {
            log.info("读取keystore {}", this.keystorePath);

            exist = true;
            resource = this.resourceLoader.getResource(this.keystorePath);
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, this.keystorePassword.toCharArray());
            super.setKeyPair(keyStoreKeyFactory.getKeyPair(this.keystoreAlias));
        } else if (StringUtils.isNotBlank(this.publicKeyPath) && StringUtils.isNotBlank(this.privateKeyPath)) {
            log.info("读取publicKey {}, privateKey {}", this.publicKeyPath, this.privateKeyPath);
            exist = true;
            resource = this.resourceLoader.getResource(this.publicKeyPath);
            String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
            super.setVerifierKey(publicKey);

            resource = this.resourceLoader.getResource(this.privateKeyPath);
            String privateKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
            super.setSigningKey(privateKey);
        }

        Assert.isTrue(exist, "Must specify keystore or publickey、privateKey");
        super.setAccessTokenConverter(this.accessTokenConverter);
        super.afterPropertiesSet();

        log.info("初始化JWT Token增强、转换服务类 {}", CustomJwtTokenEnhancer.class);
    }

    /**
     * @param oAuth2AccessToken
     * @param oAuth2Authentication
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description 根据tokenType生成公私钥
     * @author houjuntao
     * @date 2020-05-08 13:20
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        if (log.isDebugEnabled()) {
            log.debug("grant_type[{}] Principal[{}]", oAuth2Authentication.getOAuth2Request().getRequestParameters().get("grant_type"), oAuth2Authentication.getPrincipal());
        }

        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(oAuth2AccessToken);
        // 根据 grant_type 为Client或User分配一个随机字符串，当密码修改或者已办法token失效时生成一个新的随机字符串
        accessToken.getAdditionalInformation().put("tagId", UUID.randomUUID().toString());
        OAuth2AccessToken newOAuth2AccessToken = super.enhance(accessToken, oAuth2Authentication);

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

    /**
     * @param token
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description 自定义JWT解码
     * @author houjuntao
     * @date 2020-05-08 13:16
     */
//    @Override
//    protected Map<String, Object> decode(String token) {
//        try {
//            Jwt jwt = JwtHelper.decodeAndVerify(token, this.verifier);
//            String claimsStr = jwt.getClaims();
//            Map<String, Object> claims = this.objectMapper.parseMap(claimsStr);
//            if (claims.containsKey("exp") && claims.get("exp") instanceof Integer) {
//                Integer intValue = (Integer)claims.get("exp");
//                claims.put("exp", new Long((long)intValue));
//            }
//
//            this.getJwtClaimsSetVerifier().verify(claims);
//            return claims;
//        } catch (Exception var6) {
//            throw new InvalidTokenException("Cannot convert access token to JSON", var6);
//        }
//    }

    /**
     * @param accessToken
     * @param authentication
     * @return java.lang.String
     * @description 自定义JWT编码
     * @author houjuntao
     * @date 2020-05-08 13:16
     */
//    @Override
//    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//        String content;
//        try {
//            content = this.objectMapper.formatMap(this.accessTokenConverter.convertAccessToken(accessToken, authentication));
//        } catch (Exception var5) {
//            throw new IllegalStateException("Cannot convert access token to JSON", var5);
//        }
//
//        String token = JwtHelper.encode(content, this.signer).getEncoded();
//        return token;
//    }
}
