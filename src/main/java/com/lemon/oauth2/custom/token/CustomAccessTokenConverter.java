package com.lemon.oauth2.custom.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lemon
 * @description 自定义AccessToken转换器，可以自定义返回信息
 * {@link org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint#checkToken} 使用 AccessTokenConverter 转换 Token 信息
 * @date 2020-05-02 21:33
 */
@Slf4j
@Component("customAccessTokenConverter")
public class CustomAccessTokenConverter implements AccessTokenConverter, InitializingBean {
    @Value("${lemon.oauth2.accessTokenConverter.includeGrantType:false}")
    private boolean includeGrantType;

    @Value("${lemon.oauth2.accessTokenConverter.scopeAttribute:scope}")
    private String scopeAttribute;

    @Value("${lemon.oauth2.accessTokenConverter.clientIdAttribute:client_id}")
    private String clientIdAttribute;

    @Autowired
    @Qualifier("customUserAuthenticationConverter")
    private UserAuthenticationConverter userTokenConverter;

    private DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.accessTokenConverter = new DefaultAccessTokenConverter();
        this.accessTokenConverter.setIncludeGrantType(this.includeGrantType);
        this.accessTokenConverter.setScopeAttribute(this.scopeAttribute);
        this.accessTokenConverter.setClientIdAttribute(this.clientIdAttribute);
        this.accessTokenConverter.setUserTokenConverter(this.userTokenConverter);

        log.info("初始化自定义AccessToken转换器类 {}", CustomAccessTokenConverter.class);
    }

    /**
     * @param oAuth2AccessToken
     * @param oAuth2Authentication
     * @return java.util.Map<java.lang.String, ?>
     * @description
     * @author lemon
     * @date 2020-05-02 19:54
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> response = (Map<String, Object>) this.accessTokenConverter.convertAccessToken(oAuth2AccessToken, oAuth2Authentication);
        return response;
    }

    /**
     * @param tokenValue
     * @param map        调用 TokenEnhancer 提供的解码方法
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description
     * @author lemon
     * @date 2020-05-02 19:36
     */
    @Override
    public OAuth2AccessToken extractAccessToken(String tokenValue, Map<String, ?> map) {
        // TODO 由CustomTokenStore调用CustomTokenEnhancer解密方法解密tokenValue
        // TODO 明文转Map
        // TODO 调用本方法得到OAuth2AccessToken
        return this.accessTokenConverter.extractAccessToken(tokenValue, map);
    }

    /**
     * @param map 调用 TokenEnhancer 提供的解码方法
     * @return org.springframework.security.oauth2.provider.OAuth2Authentication
     * @description
     * @author lemon
     * @date 2020-05-02 19:37
     */
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        return this.accessTokenConverter.extractAuthentication(map);
    }
}
