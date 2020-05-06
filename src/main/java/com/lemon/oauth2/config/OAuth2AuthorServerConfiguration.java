package com.lemon.oauth2.config;

import com.lemon.oauth2.interceptor.WebAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author lemon
 * @description 认证授权服务器
 * @date 2020-04-30 09:01
 */
@Slf4j
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired()
    @Qualifier("customUserApprovalHandler")
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("customAuthorizationCodeServices")
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    @Qualifier("customTokenEnhancer")
    private TokenEnhancer tokenEnhancer;

    @Autowired
    @Qualifier("customAuthorizationServerTokenServices")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    @Qualifier("customOAuth2RequestFactory")
    private OAuth2RequestFactory oauth2RequestFactory;

    @Autowired
    @Qualifier("customTokenStore")
    private TokenStore tokenStore;

    @Autowired
    @Qualifier("customAccessTokenConverter")
    private AccessTokenConverter accessTokenConverter;

    @Autowired
    @Qualifier("customClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Autowired
    private WebAccessInterceptor webAccessInterceptor;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(this.clientDetailsService);
    }

    /**
     * @param endpoints
     * @description token及用户信息存储到redis，当然你也可以存储在当前的服务内存，不推荐
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // token信息存到redis
        endpoints
                .addInterceptor(this.webAccessInterceptor)
                /*
                 * false，每次通过refresh_token获得access_token时，也会刷新refresh_token；也就是说，会返回全新的access_token与refresh_token。
                 * true，只返回新的access_token，refresh_token不变。
                 */
                .reuseRefreshTokens(true)
                .requestFactory(this.oauth2RequestFactory)
                .tokenEnhancer(this.tokenEnhancer)
                .accessTokenConverter(this.accessTokenConverter)
                /*
                 * {@link org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer#getDefaultTokenGranters}
                 * AuthorizationCodeTokenGranter 授权码模式获取授权码
                 * ImplicitTokenGranter 隐式授权模式
                 * ClientCredentialsTokenGranter 客户端凭证模式
                 * ResourceOwnerPasswordTokenGranter 密码模式
                 * RefreshTokenGranter 刷新token
                 * CompositeTokenGranter 封装以上5个TokenGranter，遍历执行根据grantType获取匹配的TokenGranter
                 *
                 * 调用{@link org.springframework.security.oauth2.provider.TokenGranter#grant}返回OAuth2AccessToken
                 */
//                .tokenGranter()
                // tokenStore 创建TokenApprovalStore、UserApprovalHandler、DefaultTokenServices时使用到
                .tokenStore(this.tokenStore)
                .userDetailsService(this.userDetailsService)
                .authorizationCodeServices(this.authorizationCodeServices)
                .authenticationManager(this.authenticationManager)
                .userApprovalHandler(userApprovalHandler)
                .pathMapping("/oauth/confirm_access", "/custom/oauth2/confirm_access");

//        // 配置TokenService参数
//        DefaultTokenServices tokenService = new DefaultTokenServices();
//        tokenService.setTokenStore(endpoints.getTokenStore());
//        tokenService.setClientDetailsService(endpoints.getClientDetailsService());
//        tokenService.setTokenEnhancer(endpoints.getTokenEnhancer());
//        // 1小时
//        tokenService.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
//        // 1小时
//        tokenService.setRefreshTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
//        tokenService.setReuseRefreshToken(true);
//        tokenService.setSupportRefreshToken(true);
        endpoints.tokenServices(this.authorizationServerTokenServices);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        // 配置oauth2服务跨域
        CorsConfigurationSource corsConfigurationSource = request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.addAllowedOrigin(request.getHeader(HttpHeaders.ORIGIN));
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setMaxAge(3600L);
            return corsConfiguration;
        };

        oauthServer
                // 允许客户表单认证
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()")
                // 对于CheckEndpoint控制器[框架自带的校验]的/oauth/check端点允许所有客户端发送器请求而不会被Spring-security拦截
                .checkTokenAccess("permitAll()")
                .addTokenEndpointAuthenticationFilter(new CorsFilter(corsConfigurationSource));
    }
}
