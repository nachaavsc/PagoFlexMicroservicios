package com.PagoFlex.eureka_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"server.port=0"})
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
        EurekaServerApplication.main(new String[]{"--server.port=0", "--spring.main.register-shutdown-hook=false"});
    }
}