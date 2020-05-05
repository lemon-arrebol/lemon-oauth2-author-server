package com.lemon.oauth2.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class WebAccessInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("before request url is [{}] starting", httpServletRequest.getRequestURL());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, ModelAndView modelAndView) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("pathInfo is {}, viewName is {}, {}", httpServletRequest.getPathInfo(), modelAndView, object);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception e) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("after request url is [{}] completion", httpServletRequest.getRequestURL());
        }
    }
}
