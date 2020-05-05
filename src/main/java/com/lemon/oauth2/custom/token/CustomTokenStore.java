package com.lemon.oauth2.custom.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * @author lemon
 * @description 自定义Token管理服务: 保存、查询、删除、更新
 * @date 2020-05-02 21:32
 */
@Slf4j
@Component("customTokenStore")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "default", matchIfMissing = true)
public class CustomTokenStore implements TokenStore, InitializingBean {
    @Value("${lemon.oauth2.redis.oauth.key.prefix:lemon:}")
    private String redisKeyPrefix;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    @Autowired
    private AuthenticationKeyGenerator authenticationKeyGenerator;

    private RedisTokenStore tokenStore;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(this.connectionFactory != null, "RedisConnectionFactory must be provided");
        Assert.state(this.authenticationKeyGenerator != null, "AuthenticationKeyGenerator must be provided");

        this.tokenStore = new RedisTokenStore(this.connectionFactory);
        this.tokenStore.setAuthenticationKeyGenerator(this.authenticationKeyGenerator);
        this.tokenStore.setPrefix(this.redisKeyPrefix);
        this.tokenStore.setSerializationStrategy(this.serializationStrategy);

        log.info("初始化自定义Token存储服务类 {}", CustomTokenStore.class);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.readAuthentication(oAuth2AccessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        // TODO 调用CustomTokenEnhancer解密方法
        OAuth2Authentication oauth2Authentication = this.tokenStore.readAuthentication(token);

        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 获取认证信息 {}", token, oauth2Authentication.getName());
        }

        return oauth2Authentication;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        this.tokenStore.storeAccessToken(oAuth2AccessToken, oAuth2Authentication);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 获取 OAuth2AccessToken", tokenValue);
        }

        OAuth2AccessToken oauth2AccessToken = this.tokenStore.readAccessToken(tokenValue);
        return oauth2AccessToken;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        OAuth2AccessToken oauth2AccessToken = this.tokenStore.getAccessToken(oAuth2Authentication);

        if (log.isDebugEnabled()) {
            log.debug("根据 OAuth2Authentication[{}] 获取 OAuth2AccessToken[{}]", oAuth2Authentication.getName(), (oauth2AccessToken == null ? "" : oauth2AccessToken.getValue()));
        }

        return oauth2AccessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 删除相关的信息", oAuth2AccessToken.getValue());
        }

        this.tokenStore.removeAccessToken(oAuth2AccessToken);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication oAuth2Authentication) {

        if (log.isDebugEnabled()) {
            log.debug("存储 RefreshToken[{}]", oAuth2RefreshToken.getValue());
        }

        this.tokenStore.storeRefreshToken(oAuth2RefreshToken, oAuth2Authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {

        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 获取 OAuth2RefreshToken", tokenValue);
        }

        return this.tokenStore.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 获取 OAuth2Authentication", oAuth2RefreshToken.getValue());
        }

        return this.tokenStore.readAuthenticationForRefreshToken(oAuth2RefreshToken);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        if (log.isDebugEnabled()) {
            log.debug("根据 Token[{}] 删除 RefreshToken", oAuth2RefreshToken.getValue());
        }

        this.tokenStore.removeRefreshToken(oAuth2RefreshToken);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        if (log.isDebugEnabled()) {
            log.debug("根据 OAuth2RefreshToken[{}] 删除 OAuth2AccessToken", oAuth2RefreshToken.getValue());
        }

        this.tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        if (log.isDebugEnabled()) {
            log.debug("根据 clientId-userName[{}-{}] 获取 OAuth2AccessToken", clientId, userName);
        }

        return this.tokenStore.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        if (log.isDebugEnabled()) {
            log.debug("根据 clientId[{}] 获取 OAuth2AccessToken", clientId);
        }

        return this.tokenStore.findTokensByClientId(clientId);
    }
}
