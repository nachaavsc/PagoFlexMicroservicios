package com.PagoFlex.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "server.port=0", 
    "eureka.client.enabled=false",
    "spring.cloud.gateway.discovery.locator.enabled=false"
})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto del Gateway levante correctamente
    }
}