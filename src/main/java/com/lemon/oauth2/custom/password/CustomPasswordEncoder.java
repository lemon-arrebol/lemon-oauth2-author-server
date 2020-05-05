package com.lemon.oauth2.custom.password;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author lemon
 * @description 密码加密、验证服务
 * @date 2020-05-02 21:32
 */
@Slf4j
@Component("customPasswordEncoder")
public class CustomPasswordEncoder implements PasswordEncoder, InitializingBean {
    private PasswordEncoder passwordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        log.info("初始化密码加密、验证服务类 {}", CustomPasswordEncoder.class);
    }

    @Override
    public String encode(CharSequence charSequence) {
        return this.passwordEncoder.encode(charSequence);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
