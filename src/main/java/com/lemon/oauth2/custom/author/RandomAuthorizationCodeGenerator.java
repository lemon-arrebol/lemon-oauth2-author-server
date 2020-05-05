package com.lemon.oauth2.custom.author;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * @author lemon
 * @description 随机数授权码生成器，可以自定义自己的授权码生成规则
 * @date 2020-05-02 21:09
 */
@Slf4j
@Component
public class RandomAuthorizationCodeGenerator implements AuthorizationCodeGenerator, InitializingBean {
    @Value("${lemon.oauth2.author.code.length:32}")
    private int length;

    private RandomValueStringGenerator generator;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.generator = new RandomValueStringGenerator(length);
        log.info("初始化随机数授权码生成器服务类 {}，授权码长度[{}]", RandomAuthorizationCodeGenerator.class, length);
    }

    /**
     * @param authentication
     * @return java.lang.String
     * @description
     * @author lemon
     * @date 2020-05-02 13:57
     */
    @Override
    public String generate(OAuth2Authentication authentication) {
        return this.generator.generate();
    }
}
