package com.lemon.oauth2.custom.author;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author lemon
 * @description 自定义Token创建、删除、更新服务，可以自定义Token如何生成、加前后缀、加解密等
 * 调用 TokenEnhancer#enhance 方法，转换成 JWT 格式生成新的 OAuth2AccessToken
 * @date 2020-05-05 21:17
 */
@Slf4j
@Component("customAuthorizationServerTokenServices")
public class CustomAuthorizationServerTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices, InitializingBean {
    @Value("${lemon.oauth2.refreshTokenValiditySeconds:2592000}")
    private int refreshTokenValiditySeconds;

    @Value("${lemon.oauth2.accessTokenValiditySeconds:43200}")
    private int accessTokenValiditySeconds;

    @Value("${lemon.oauth2.supportRefreshToken:true}")
    private boolean supportRefreshToken;

    @Value("${lemon.oauth2.reuseRefreshToken:true}")
    private boolean reuseRefreshToken;

    @Autowired
    @Qualifier("customTokenStore")
    private TokenStore tokenStore;

    /**
     * TokenEnhancer 可以对创建的 OAuth2AccessToken 进行自定义处理
     */
    @Autowired
    @Qualifier("customTokenEnhancer")
    private TokenEnhancer accessTokenEnhancer;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("customClientDetailsService")
    private ClientDetailsService clientDetailsService;

    private DefaultTokenServices tokenServices = new DefaultTokenServices();

    /**
     * @return void
     * @description 参考一下两个方法创建
     * {@link org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer#createDefaultTokenServices}
     * {@link org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer#addUserDetailsService}
     * @author lemon
     * @date 2020-05-02 14:35
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.tokenServices.setRefreshTokenValiditySeconds(this.refreshTokenValiditySeconds);
        this.tokenServices.setAccessTokenValiditySeconds(this.accessTokenValiditySeconds);
        this.tokenServices.setSupportRefreshToken(this.supportRefreshToken);
        this.tokenServices.setReuseRefreshToken(this.reuseRefreshToken);
        this.tokenServices.setTokenStore(this.tokenStore);
        this.tokenServices.setClientDetailsService(this.clientDetailsService);
        // 创建 OAuth2AccessToken 之后调用 TokenEnhancer#enhance 方法，转换成 JWT 格式生成新的 OAuth2AccessToken
        this.tokenServices.setTokenEnhancer(this.accessTokenEnhancer);

        if (this.userDetailsService != null) {
            // Principal Credentials 不为NULL即可，只根据Principal查询用户信息
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper(userDetailsService));
            this.tokenServices.setAuthenticationManager(new ProviderManager(Arrays.asList(provider)));
        }

        this.tokenServices.afterPropertiesSet();
        log.info("初始化自定义Token管理服务类 {}", CustomAuthorizationServerTokenServices.class);
    }

    /**
     * @param oAuth2Authentication
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description 创建 OAuth2AccessToken 之后调用 TokenEnhancer#enhance 方法，转换成 JWT 格式生成新的 OAuth2AccessToken
     * @author lemon
     * @date 2020-05-02 19:43
     */
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication oAuth2Authentication) throws AuthenticationException {
        return this.tokenServices.createAccessToken(oAuth2Authentication);
    }

    /**
     * @param refreshTokenValue
     * @param tokenRequest
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description DefaultOAuth2AccessToken DefaultOAuth2RefreshToken
     * @author lemon
     * @date 2020-05-02 08:56
     */
    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
        return this.tokenServices.refreshAccessToken(refreshTokenValue, tokenRequest);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        return this.tokenServices.getAccessToken(oAuth2Authentication);
    }

    @Override
    public boolean revokeToken(String tokenValue) {
        return this.tokenServices.revokeToken(tokenValue);
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
        return this.tokenServices.loadAuthentication(accessTokenValue);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return this.tokenServices.readAccessToken(accessToken);
    }
}
