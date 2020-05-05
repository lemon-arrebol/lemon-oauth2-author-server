package com.lemon.oauth2.custom.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author lemon
 * @description ClientDetail 查询服务，新增、查询第三方注册的信息，获取授权类型、访问资源范围
 * @date 2020-05-02 21:31
 */
@Slf4j
@Component("customClientDetailsService")
public class CustomClientDetailsService implements ClientDetailsService, ClientRegistrationService, InitializingBean {
    @Autowired
    private DataSource dataSource;

    private JdbcClientDetailsService clientDetailsService;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientDetailsService = new JdbcClientDetailsService(this.dataSource);

        log.info("初始化ClientDetail查询服务类 {}", CustomClientDetailsService.class);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return this.clientDetailsService.loadClientByClientId(clientId);
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        this.clientDetailsService.addClientDetails(clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        this.clientDetailsService.updateClientDetails(clientDetails);
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        this.clientDetailsService.updateClientSecret(clientId, secret);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        this.clientDetailsService.removeClientDetails(clientId);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return this.clientDetailsService.listClientDetails();
    }
}
