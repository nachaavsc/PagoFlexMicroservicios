package com.PagoFlex.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureWebTestClient
class ApiGatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void rutasConfiguradasHaciaLosMicroservicios() {
        List<Route> rutas = routeLocator.getRoutes().collectList().block();

        assertEquals(4, rutas.size());
        assertTrue(rutas.stream().anyMatch(r -> r.getId().equals("ms-autenticacion") && r.getUri().toString().equals("lb://ms-autenticacion")));
        assertTrue(rutas.stream().anyMatch(r -> r.getId().equals("ms-usuarios") && r.getUri().toString().equals("lb://ms-usuarios")));
        assertTrue(rutas.stream().anyMatch(r -> r.getId().equals("ms-compromisos") && r.getUri().toString().equals("lb://ms-compromisos")));
        assertTrue(rutas.stream().anyMatch(r -> r.getId().equals("ms-incidencias") && r.getUri().toString().equals("lb://ms-incidencias")));
    }

    @Test
    void corsPermiteClientesExternos() {
        webTestClient.options()
                .uri("http://localhost:8080/api/usuarios")
                .header("Origin", "https://app.pagoflex.cl")
                .header("Access-Control-Request-Method", "POST")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "https://app.pagoflex.cl");
    }

    @Test
    void mainLevantaLaAplicacion() {
        ApiGatewayApplication.main(new String[]{"--server.port=0", "--eureka.client.enabled=false", "--spring.main.register-shutdown-hook=false"});
    }
}
