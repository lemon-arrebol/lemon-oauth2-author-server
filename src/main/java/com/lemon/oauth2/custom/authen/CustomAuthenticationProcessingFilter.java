package com.lemon.oauth2.custom.authen;

import com.lemon.oauth2.custom.author.CustomAuthorizationCodeServices;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lemon
 * @description 自定义登录请求过滤器
 * {@link com.lemon.oauth2.config.WebSecurityConfig#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)} 配置
 * http.addFilterBefore(customAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
 * @date 2020-05-05 21:18
 */
@Data
@Slf4j
public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private boolean postOnly = true;
    private String usernameParameter = "username";
    private String passwordParameter = "password";

    public CustomAuthenticationProcessingFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
        log.info("初始化自定义登录请求处理/login服务");
    }

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @return org.springframework.security.core.Authentication
     * @description 可以获取账号、密码之外的信息，比如动态验证码、短信验证码等，对密文进行解密
     * 调用 {@link CustomAuthorizationCodeServices} 进行身份认证
     * @author lemon
     * @date 2020-04-30 16:02
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && !httpServletRequest.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + httpServletRequest.getMethod());
        } else {
            String username = this.obtainUsername(httpServletRequest);
            String password = this.obtainPassword(httpServletRequest);

            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();
            password = password.trim();

            log.info("用户输入的用户名是：" + username);
            log.info("用户输入的密码是：" + password);

            CustomAuthenticationToken authRequest = new CustomAuthenticationToken(username, password);
            this.setDetails(httpServletRequest, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    /**
     * @param request
     * @return java.lang.String
     * @description
     * @author lemon
     * @date 2020-04-30 16:02
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }

    /**
     * @param request
     * @return java.lang.String
     * @description
     * @author lemon
     * @date 2020-04-30 16:02
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    /**
     * @param request
     * @param authRequest
     * @return void
     * @description
     * @author lemon
     * @date 2020-04-30 16:02
     */
    protected void setDetails(HttpServletRequest request, CustomAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }
}
