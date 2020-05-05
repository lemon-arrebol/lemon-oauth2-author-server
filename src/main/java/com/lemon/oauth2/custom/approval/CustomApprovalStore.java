package com.lemon.oauth2.custom.approval;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * @author lemon
 * @description 自定义用户授权资源 DAO 服务，授权码的创建、消费，授权码是一次性的
 * @date 2020-05-05 21:17
 */
@Slf4j
@Component("customApprovalStore")
public class CustomApprovalStore implements ApprovalStore, InitializingBean {
    @Autowired
    private DataSource dataSource;

    private JdbcApprovalStore approvalStore;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.approvalStore = new JdbcApprovalStore(this.dataSource);
        log.info("初始化自定义用户授权资源管理服务类 {}", CustomApprovalStore.class);
    }

    @Override
    public boolean addApprovals(Collection<Approval> collection) {
        return this.approvalStore.addApprovals(collection);
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> collection) {
        return this.approvalStore.revokeApprovals(collection);
    }

    @Override
    public Collection<Approval> getApprovals(String userName, String clientId) {
        return this.approvalStore.getApprovals(userName, clientId);
    }
}
