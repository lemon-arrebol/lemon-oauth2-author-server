package com.lemon.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lemon
 * @description 默认 {@link org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint}
 * @date 2020-05-01 14:47
 */
@Slf4j
@Controller
public class OAuth2ApprovalController {
    @RequestMapping("/custom/oauth2/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("跳转到自自定义授权页面");
        }

        Map<String, String> scopeMap = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes") : request.getAttribute("scopes"));
        model.put("scopeMap", scopeMap);

        CsrfToken csrfToken = (CsrfToken) ((model.containsKey("_csrf") ? model.get("_csrf") : request.getAttribute("_csrf")));

        if (csrfToken != null) {
            model.put("csrfParamName", csrfToken.getParameterName());
            model.put("csrfParamValue", csrfToken.getToken());
        }

        return "oauth2/oauth_approval";
    }
}  