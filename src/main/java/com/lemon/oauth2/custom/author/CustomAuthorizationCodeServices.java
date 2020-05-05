package com.lemon.oauth2.custom.author;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * @author lemon
 * @description 自定义授权码管理服务
 * @date 2020-05-01 12:42
 */
@Slf4j
@Component("customAuthorizationCodeServices")
public class CustomAuthorizationCodeServices extends JdbcAuthorizationCodeServices implements AuthorizationCodeServices, InitializingBean {
    @Autowired
    private AuthorizationCodeGenerator authorizationCodeGenerator;

    public CustomAuthorizationCodeServices(DataSource dataSource) {
        super(dataSource);
        log.info("初始化自定义授权码服务类 {}", CustomAuthorizationCodeServices.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authorizationCodeGenerator, "authorizationCodeGenerator must be set");
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = this.authorizationCodeGenerator.generate(authentication);

        Assert.hasText(code, "Username parameter must not be empty or null");

        if (log.isDebugEnabled()) {
            log.debug("Principal [{}] 生成授权码 {}", authentication.getPrincipal(), code);
        }

        super.store(code, authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication oauth2Authentication = super.consumeAuthorizationCode(code);

        if (log.isDebugEnabled()) {
            log.debug("Principal [{}] 消费授权码 {}", oauth2Authentication.getPrincipal(), code);
        }

        return oauth2Authentication;
    }
}