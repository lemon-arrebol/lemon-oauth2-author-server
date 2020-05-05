package com.lemon.oauth2.custom.author;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @author lemon
 * @description 授权码生成器
 * @date 2020-05-02 21:08
 */
public interface AuthorizationCodeGenerator {
    /**
     * @param authentication
     * @return java.lang.String
     * @description
     * @author lemon
     * @date 2020-05-02 13:57
     */
    String generate(OAuth2Authentication authentication);
}
