package com.lemon.oauth2.custom.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author lemon
 * @description 自定义Token增强处理服务，对 Token 提供编码 、解码方法
 * 自定义对原始数据编码生成新的 OAuth2AccessToken，编码后的数据作为 OAuth2AccessToken.value，
 * 编码时调用 AccessTokenConverter.convertAccessToken 方法得到 Map数据
 * @date 2020-05-02 21:32
 */
@Slf4j
@Component("customTokenEnhancer")
@ConditionalOnProperty(name = "lemon.oauth2.token.format", havingValue = "default", matchIfMissing = true)
public class CustomTokenEnhancer implements TokenEnhancer, InitializingBean {
    @Autowired
    @Qualifier("customAccessTokenConverter")
    private AccessTokenConverter accessTokenConverter;

    @Override
    public void afterPropertiesSet() {
        Assert.state(this.accessTokenConverter != null, "AccessTokenConverter must be provided");

        log.info("初始化自定义Token增强处理服务类 {}", CustomTokenEnhancer.class);
    }

    /**
     * @param oAuth2AccessToken
     * @param oAuth2Authentication
     * @return org.springframework.security.oauth2.common.OAuth2AccessToken
     * @description 调用 AccessTokenConverter.convertAccessToken 方法得到 Map数据，进一步处理创建新的 OAuth2AccessToken
     * @author lemon
     * @date 2020-05-02 19:45
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        if (log.isDebugEnabled()) {
            log.debug("CustomTokenEnhancer不对Token[{}]进行处理", oAuth2AccessToken.getValue());
        }

        Map<String, Object> converterMap = (Map<String, Object>) this.accessTokenConverter.convertAccessToken(oAuth2AccessToken, oAuth2Authentication);

        log.info("convertAccessToken is {}", converterMap);

        // TODO 添加额外信息返回调用方
        converterMap.put("CustomTokenEnhancer", "enhance");

        // TODO 可以进行加密处理
//        String accessToken = converterMap.toString();
//        oAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);

        return oAuth2AccessToken;
    }
}
