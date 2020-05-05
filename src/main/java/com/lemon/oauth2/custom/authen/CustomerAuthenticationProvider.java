package com.lemon.oauth2.custom.authen;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author lemon
 * @description 自定义认证处理
 * AuthenticationManagerBuilder中的AuthenticationProvider是进行认证的核心
 * CustomerAuthenticationProvider 主要是为AuthenticationProvider接口的实现类  为spring security提供密码验证器  是核心配置，
 * 这儿需要注意  系统中已经有相应的实现类，如果不配置，则系统中会默认使用#{@org.springframework.security.authentication.dao.DaoAuthenticationProvider}这个类来进行验证，
 * DaoAuthenticationProvider这个类继承了org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider这个抽象类，
 * 所以我们要自定义provider验证流程可以实现AuthenticationProvider接口或者继承AbstractUserDetailsAuthenticationProvider抽象类均可
 * <p>
 * {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * 拦截/login请求，将username、password参数封装为{@link UsernamePasswordAuthenticationToken}
 * <p>
 * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} 支持处理 {@link UsernamePasswordAuthenticationToken} 类型参数
 * @date 2020-04-30 15:11
 */
@Slf4j
@Component("customerAuthenticationProvider")
public class CustomerAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    @Qualifier("customPasswordEncoder")
    PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    private boolean forcePrincipalAsString = false;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
     * @param
     * @return
     * @description authentication是前台拿过来的用户名、密码bean  主要验证流程代码  注意这儿懒得用加密验证！！！
     * @author lemon
     * @date 2019-08-17 22:50
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("用户输入的用户名是：" + authentication.getName());
        log.info("用户输入的密码是：" + authentication.getCredentials());
        log.info("用户详细信息类型是：" + authentication.getDetails());
        log.info("用户详细信息是：" + JSON.toJSONString(authentication.getDetails()));

        if (StringUtils.isBlank(authentication.getName())) {
            throw new UsernameNotFoundException("Account is not allowed to be empty");
        }

        if (authentication.getCredentials() == null || StringUtils.isBlank(authentication.getCredentials().toString())) {
            throw new UsernameNotFoundException("Password is not allowed to be empty");
        }

        // 根据用户输入的用户名获取该用户名已经在服务器上存在的用户详情，如果没有则返回null
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());

        if (userDetails == null) {
            log.warn("未查询到用户{}", authentication.getName());
            throw new UsernameNotFoundException(String.format("Account %s does not exist", authentication.getName()));
        }

        try {
            log.info("服务器上已经保存的用户名是：" + userDetails.getUsername());
            log.info("服务器上保存的该用户名对应的密码是： " + userDetails.getPassword());
            log.info("服务器上保存的该用户对应的权限是：" + userDetails.getAuthorities());

            String presentedPassword = authentication.getCredentials().toString();

            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }

            // 验证成功  将返回一个UsernamePasswordAuthenticaionToken对象
            log.info("LOGIN SUCCESS !!!!!!!!!!!!!!!!!!!");

            Object principalToReturn = userDetails;
            if (this.forcePrincipalAsString) {
                principalToReturn = userDetails.getUsername();
            }

            return this.createSuccessAuthentication(principalToReturn, authentication, userDetails);
        } catch (Exception e) {
            log.error("author failed, -------------------the error message is:-------- " + e);
            throw e;
        }
    }

    /**
     * @param principal
     * @param authentication
     * @param userDetails
     * @return org.springframework.security.core.Authentication
     * @description
     * @author lemon
     * @date 2020-04-30 21:03
     */
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails userDetails) {
        // 用户实体、输入的密码、用户的权限
        CustomAuthenticationToken result = new CustomAuthenticationToken(principal, authentication.getCredentials(),
                this.authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     *
     **/
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}