package com.lemon.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author lemon
 * @description
 * @date 2020-05-06 17:06
 */
@Slf4j
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServletInitializer.class);
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        WebApplicationContext webApplicationContext = super.run(application);
        Environment env = webApplicationContext.getEnvironment();

        log.info("\n----------------------------------------------------------\n\tOAuth2授权服务 '{}' 启动完成! \n\t端口号(s): \t{}\n\t环境(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getProperty("server.port"), env.getActiveProfiles());

        return webApplicationContext;
    }
}
