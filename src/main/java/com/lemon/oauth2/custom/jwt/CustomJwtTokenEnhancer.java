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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.util.Map;

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
