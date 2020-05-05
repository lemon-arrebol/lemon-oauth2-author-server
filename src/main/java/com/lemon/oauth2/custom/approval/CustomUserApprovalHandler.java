package com.lemon.oauth2.custom.approval;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * @author lemon
 * @description 自定义用户资源授权管理服务
 * 参考: {@link org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler}
 * {@link org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint#authorize} 调用该类方法验证、获取用户对资源的授权信息
 * @date 2020-05-01 12:42
 */
@Slf4j
@Component("customUserApprovalHandler")
public class CustomUserApprovalHandler implements UserApprovalHandler, InitializingBean {
    @Value("${lemon.oauth2.approvalHandler.scopePrefix:scope.}")
    private String scopePrefix;

    @Value("${lemon.oauth2.approvalHandler.approvalExpirySeconds:-1}")
    private int approvalExpirySeconds;

    @Autowired
    @Qualifier("customApprovalStore")
    private ApprovalStore approvalStore;

    @Autowired
    @Qualifier("customClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Autowired
    @Qualifier("customOAuth2RequestFactory")
    private OAuth2RequestFactory requestFactory;

    private ApprovalStoreUserApprovalHandler approvalStoreUserApprovalHandler;

    @Override
    public void afterPropertiesSet() {
        Assert.state(this.approvalStore != null, "ApprovalStore must be provided");
        Assert.state(this.requestFactory != null, "OAuth2RequestFactory must be provided");
        Assert.state(this.clientDetailsService != null, "ClientDetailsService must be provided");

        this.approvalStoreUserApprovalHandler = new ApprovalStoreUserApprovalHandler();
        this.approvalStoreUserApprovalHandler.setScopePrefix(this.scopePrefix);
        this.approvalStoreUserApprovalHandler.setApprovalExpiryInSeconds(this.approvalExpirySeconds);
        this.approvalStoreUserApprovalHandler.setApprovalStore(this.approvalStore);
        this.approvalStoreUserApprovalHandler.setRequestFactory(this.requestFactory);
        this.approvalStoreUserApprovalHandler.setClientDetailsService(this.clientDetailsService);

        log.info("初始化自定义用户授权信息服务类 {}", CustomUserApprovalHandler.class);
    }

    /**
     * @param authorizationRequest
     * @param authentication
     * @return boolean
     * @description
     * @author lemon
     * @date 2020-05-01 13:11
     */
    @Override
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication authentication) {
        boolean isApproved = this.approvalStoreUserApprovalHandler.isApproved(authorizationRequest, authentication);

        Collection<String> requestedScopes = authorizationRequest.getScope();

        if (log.isDebugEnabled()) {
            log.debug("Principal [{}] 是否准许授权{} 请求scope {}", authentication.getPrincipal(), isApproved, StringUtils.join(requestedScopes, ", "));
        }

        return isApproved;
    }

    /**
     * @param authorizationRequest
     * @param authentication
     * @return org.springframework.security.oauth2.provider.AuthorizationRequest
     * @description
     * @author lemon
     * @date 2020-05-01 13:11
     */
    @Override
    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication authentication) {
        return this.approvalStoreUserApprovalHandler.checkForPreApproval(authorizationRequest, authentication);
    }

    /**
     * @param authorizationRequest
     * @param authentication
     * @return org.springframework.security.oauth2.provider.AuthorizationRequest
     * @description
     * @author lemon
     * @date 2020-05-01 13:11
     */
    @Override
    public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication authentication) {
        return this.approvalStoreUserApprovalHandler.updateAfterApproval(authorizationRequest, authentication);
    }

    /**
     * @param authorizationRequest
     * @param authentication
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description {@link org.springframework.security.oauth2.provider.approval.UserApprovalHandler#getUserApprovalRequest} 查询授权信息可以自定义修改
     * @author lemon
     * @date 2020-05-01 13:11
     */
    @Override
    public Map<String, Object> getUserApprovalRequest(AuthorizationRequest authorizationRequest, Authentication authentication) {
        return this.approvalStoreUserApprovalHandler.getUserApprovalRequest(authorizationRequest, authentication);
    }
}