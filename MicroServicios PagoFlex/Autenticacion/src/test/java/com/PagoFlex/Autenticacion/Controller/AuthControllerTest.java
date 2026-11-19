package com.PagoFlex.Autenticacion.Controller;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Service.AutenticacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AutenticacionService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    void registrar_Retorna201() {
        Autenticacion mockAuth = new Autenticacion();
        mockAuth.setCorreo("ana@pagoflex.cl");
        when(authService.registrar(anyString(), anyString(), anyString(), anyString())).thenReturn(mockAuth);

        ResponseEntity<?> resp = controller.registrar(Map.of(
                "nombre", "Ana", "correo", "ana@pagoflex.cl", "clave", "1234", "telefono", "999"
        ));
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    @Test
    void registrar_Error_Retorna400() {
        when(authService.registrar(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Duplicado"));

        ResponseEntity<?> resp = controller.registrar(Map.of(
                "nombre", "Ana", "correo", "ana@pagoflex.cl", "clave", "1234"
        ));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void login_Retorna200Y401() {
        when(authService.login("ana@pagoflex.cl", "1234")).thenReturn(new Autenticacion());
        assertEquals(HttpStatus.OK, controller.login(Map.of("correo", "ana@pagoflex.cl", "clave", "1234")).getStatusCode());

        when(authService.login("ana@pagoflex.cl", "bad")).thenThrow(new RuntimeException("Inválido"));
        assertEquals(HttpStatus.UNAUTHORIZED, controller.login(Map.of("correo", "ana@pagoflex.cl", "clave", "bad")).getStatusCode());
    }

    @Test
    void cambiarClave_Retorna200Y404() {
        when(authService.cambiarClave("ana@pagoflex.cl", "5678")).thenReturn(new Autenticacion());
        assertEquals(HttpStatus.OK, controller.cambiarClave(Map.of("correo", "ana@pagoflex.cl", "nuevaClave", "5678")).getStatusCode());

        when(authService.cambiarClave("no@pagoflex.cl", "5678")).thenThrow(new RuntimeException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, controller.cambiarClave(Map.of("correo", "no@pagoflex.cl", "nuevaClave", "5678")).getStatusCode());
    }
}