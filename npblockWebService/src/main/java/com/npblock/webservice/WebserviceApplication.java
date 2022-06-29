package com.npblock.webservice;

import com.npblock.webservice.manager.SocketServerManager;
import com.npblock.webservice.manager.ThreadPoolManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
        // 启动socket服务
        ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().startGameSocketServer());
    }
}
