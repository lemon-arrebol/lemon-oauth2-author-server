package com.lemon.oauth2.custom.authen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lemon
 * @description 自定义 Authentication 转换器，认证信息编码、解码
 * {@link org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter#convertAccessToken} 转换 AccessToke 时放入用户信息
 * @date 2020-05-02 21:03
 */
@Slf4j
@Component("customUserAuthenticationConverter")
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter, InitializingBean {
    @Value("${lemon.oauth2.authentConverter.defaultAuthorities:}")
    private String[] defaultAuthorities;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    private DefaultUserAuthenticationConverter userAuthenticationConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.userAuthenticationConverter = new DefaultUserAuthenticationConverter();
        this.userAuthenticationConverter.setDefaultAuthorities(this.defaultAuthorities);
        this.userAuthenticationConverter.setUserDetailsService(this.userDetailsService);
        log.info("初始化自定义Authentication转换器类 {}", CustomUserAuthenticationConverter.class);
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> converterMap = (Map<String, Object>) this.userAuthenticationConverter.convertUserAuthentication(authentication);

        // TODO 添加额外信息返回调用方
        converterMap.put("CustomUserAuthenticationConverter", "convertUserAuthentication");

        return converterMap;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        return this.userAuthenticationConverter.extractAuthentication(map);
    }
}
