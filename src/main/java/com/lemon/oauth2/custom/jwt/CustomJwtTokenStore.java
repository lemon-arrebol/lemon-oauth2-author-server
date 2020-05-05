package com.lemon.oauth2.custom.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author lemon
 * @description 自定义Token管理服务: 保存、查询、删除、更新
 * 调用 JwtAccessTokenConverter 实现的 AccessTokenConverter 接口方法进行解码 JWT 转换
 * @date 2020-05-05 21:19
 */
@Slf4j
@Component("customTokenStore")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "jwt")
public class CustomJwtTokenStore implements TokenStore, InitializingBean {
    @Qualifier("customApprovalStore")
    private ApprovalStore approvalStore;

    private JwtTokenStore tokenStore;

    @Autowired
    @Qualifier("customTokenEnhancer")
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.tokenStore = new JwtTokenStore(this.jwtAccessTokenConverter);
        this.tokenStore.setApprovalStore(this.approvalStore);
        // 调用 JwtAccessTokenConverter 实现的 AccessTokenConverter 接口方法进行解码 JWT 转换
        this.tokenStore.setTokenEnhancer(this.jwtAccessTokenConverter);

        log.info("初始化自定义Token存储服务类 {}", CustomJwtTokenStore.class);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.readAuthentication(oAuth2AccessToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
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
