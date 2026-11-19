package com.PagoFlex.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        ApiGatewayApplication.main(new String[]{"--server.port=0", "--eureka.client.enabled=false", "--spring.main.register-shutdown-hook=false"});
    }
}