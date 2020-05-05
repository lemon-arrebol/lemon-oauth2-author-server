package com.lemon.oauth2.custom.authen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Component;

/**
 * @author lemon
 * @description 根据 OAuth2Authentication 访问或存储 OAuth2AccessToken 时生成 Token ID
 * JdbcTokenStore、RedisTokenStore、InMemoryTokenStore 使用 AuthenticationKeyGenerator 生成 authentication_id、auth_to_access:生成的Key、AccessTokenId
 * @date 2020-05-05 21:19
 */
@Slf4j
@Component("customAuthenticationKeyGenerator")
public class CustomAuthenticationKeyGenerator implements AuthenticationKeyGenerator, InitializingBean {
    private DefaultAuthenticationKeyGenerator authenticationKeyGenerator;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

        log.info("初始化自定义AuthenticationKey生成器类 {}", CustomAuthenticationKeyGenerator.class);
    }

    /**
     * @param oAuth2Authentication
     * @return java.lang.String
     * @description 根据 OAuth2Authentication 访问或存储 OAuth2AccessToken 时生成 Token ID
     * @author lemon
     * @date 2020-05-02 09:50
     */
    @Override
    public String extractKey(OAuth2Authentication oAuth2Authentication) {
        String key = this.authenticationKeyGenerator.extractKey(oAuth2Authentication);

        if (log.isDebugEnabled()) {
            log.debug("根据 OAuth2Authentication 访问或存储 OAuth2AccessToken 时生成 Token ID [{}]", key);
        }

        return key;
    }
}
