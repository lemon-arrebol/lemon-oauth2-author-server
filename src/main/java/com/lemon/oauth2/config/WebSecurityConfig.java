package com.lemon.oauth2.config;

import com.lemon.oauth2.custom.authen.CustomAuthenticationFailureHandler;
import com.lemon.oauth2.custom.authen.CustomAuthenticationProcessingFilter;
import com.lemon.oauth2.custom.authen.CustomAuthenticationSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * @author lemon
 * @description Security核心配置
 * @date 2020-04-30 14:45
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("customPasswordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("customerAuthenticationProvider")
    private AuthenticationProvider customerAuthenticationProvider;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    /**
     * @param
     * @return org.springframework.security.authentication.AuthenticationManager
     * @description 认证管理器
     * @author lemon
     * @date 2020-04-30 14:31
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager authenticationManager = super.authenticationManagerBean();
        log.info("初始化AuthenticationManager {}", authenticationManager.getClass());
        return authenticationManager;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // 目的是为了获取前端数据时获取到整个form-data的数据,提供验证器
                .authenticationProvider(this.customerAuthenticationProvider)
                // 配置登录user验证处理器 以及密码加密器  好让认证中心进行验证
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(this.passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Assert.notNull(this.successHandler, "successHandler should not be null.");
        Assert.notNull(this.failureHandler, "failureHandler should not be null.");

        super.configure(http);

        CustomAuthenticationProcessingFilter customAuthenticationProcessingFilter = new CustomAuthenticationProcessingFilter();
        customAuthenticationProcessingFilter.setAuthenticationManager(this.authenticationManager());
        customAuthenticationProcessingFilter.setAuthenticationSuccessHandler(this.successHandler);
        customAuthenticationProcessingFilter.setAuthenticationFailureHandler(this.failureHandler);
        ((CustomAuthenticationSuccessHandler) this.successHandler).setDefaultTargetUrl("/");
        ((CustomAuthenticationFailureHandler) this.failureHandler).setDefaultFailureUrl("/login?error");

        http
                // 将自定义拦截器加到 UsernamePasswordAuthenticationFilter 之前
                .addFilterBefore(customAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
