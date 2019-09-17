package com.example.demo.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Description 请输入类描述信息
 * @ClassName WebConfig
 * @Autor DZT
 * @Date 2019/9/9 10:39
 * @Version 1.0
 */
@Configuration
public class WebConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
