package com.lemon.oauth2.custom.exception;

import com.alibaba.fastjson.JSON;
import com.lemon.oauth2.enums.ResponseEnum;
import com.lemon.oauth2.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lemon
 * @description 自定义未授权 token无效 权限不足返回信息处理类
 * @date 2020-05-02 21:31
 */
@Slf4j
@Component
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        Throwable cause = authException.getCause();
        this.setResponseHeader(response);

        if (cause instanceof InvalidTokenException) {
            log.error("InvalidTokenException : {}", cause.getMessage());
            // Token无效
            response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID)));
        } else {
            log.error("AuthenticationException : NoAuthentication");
            // 资源未授权
            response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.UNAUTHORIZED)));
        }

        authException.printStackTrace();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.setResponseHeader(response);
        // 访问资源的用户权限不足
        log.error("AccessDeniedException : {}", accessDeniedException.getMessage());
        response.getWriter().write(JSON.toJSONString(ResponseVO.error(ResponseEnum.INSUFFICIENT_PERMISSIONS)));

        accessDeniedException.printStackTrace();
    }

    /**
     * @param response
     * @return void
     * @description
     * @author lemon
     * @date 2020-04-30 10:24
     */
    private void setResponseHeader(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.addHeader("Access-Control-Max-Age", "1800");
    }
}
