package com.PagoFlex.Autenticacion.Controller;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Service.AutenticacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AutenticacionService authService;

    @InjectMocks
    private AuthController controller;

    private final JsonMapper mapper = JsonMapper.builder().build();

    private Autenticacion credencial;

    @BeforeEach
    void setUp() {
        credencial = new Autenticacion();
        credencial.setId(1L);
        credencial.setUsuarioId(10L);
        credencial.setCorreo("ana@pagoflex.cl");
        credencial.setClaveHash("1234");
    }

    @Test
    void registrar_retornaCreated() {
        when(authService.registrar("Ana", "ana@pagoflex.cl", "1234", "999")).thenReturn(credencial);

        ResponseEntity<?> respuesta = controller.registrar(Map.of(
                "nombre", "Ana", "correo", "ana@pagoflex.cl", "clave", "1234", "telefono", "999"
        ));

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(credencial, respuesta.getBody());
    }

    @Test
    void registrar_sinTelefono_usaVacio() {
        when(authService.registrar("Ana", "ana@pagoflex.cl", "1234", "")).thenReturn(credencial);

        ResponseEntity<?> respuesta = controller.registrar(Map.of(
                "nombre", "Ana", "correo", "ana@pagoflex.cl", "clave", "1234"
        ));

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
    }

    @Test
    void registrar_correoDuplicado_retornaBadRequest() {
        when(authService.registrar("Ana", "ana@pagoflex.cl", "1234", ""))
                .thenThrow(new IllegalArgumentException("El correo ya está registrado"));

        ResponseEntity<?> respuesta = controller.registrar(Map.of(
                "nombre", "Ana", "correo", "ana@pagoflex.cl", "clave", "1234"
        ));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El correo ya está registrado"), respuesta.getBody());
    }

    @Test
    void login_valido_retornaOk() {
        when(authService.login("ana@pagoflex.cl", "1234")).thenReturn(credencial);

        ResponseEntity<?> respuesta = controller.login(Map.of("correo", "ana@pagoflex.cl", "clave", "1234"));

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(credencial, respuesta.getBody());
    }

    @Test
    void login_invalido_retornaUnauthorized() {
        when(authService.login("ana@pagoflex.cl", "mala")).thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> respuesta = controller.login(Map.of("correo", "ana@pagoflex.cl", "clave", "mala"));

        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Credenciales inválidas"), respuesta.getBody());
    }

    @Test
    void cambiarClave_existente_retornaOk() {
        when(authService.cambiarClave("ana@pagoflex.cl", "5678")).thenReturn(credencial);

        ResponseEntity<?> respuesta = controller.cambiarClave(Map.of("correo", "ana@pagoflex.cl", "nuevaClave", "5678"));

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    }

    @Test
    void cambiarClave_noExiste_retornaNotFound() {
        when(authService.cambiarClave("no@pagoflex.cl", "5678")).thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> respuesta = controller.cambiarClave(Map.of("correo", "no@pagoflex.cl", "nuevaClave", "5678"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Usuario no encontrado"), respuesta.getBody());
    }

    @Test
    void respuestaJson_noExponeClaveHash() {
        when(authService.login("ana@pagoflex.cl", "1234")).thenReturn(credencial);

        String json = mapper.writeValueAsString(controller.login(Map.of("correo", "ana@pagoflex.cl", "clave", "1234")).getBody());

        assertFalse(json.contains("claveHash"));
        assertFalse(json.contains("1234"));
        assertTrue(json.contains("\"correo\":\"ana@pagoflex.cl\""));
        assertTrue(json.contains("\"usuarioId\":10"));
    }

    @Test
    void json_deEntrada_aceptaClaveHash() {
        Autenticacion leida = mapper.readValue("{\"correo\":\"ana@pagoflex.cl\",\"claveHash\":\"1234\"}", Autenticacion.class);

        assertEquals("1234", leida.getClaveHash());
    }
}
