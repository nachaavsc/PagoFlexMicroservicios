package com.PagoFlex.Usuarios.Exception;

import com.PagoFlex.Usuarios.Controller.UsuarioController;
import com.PagoFlex.Usuarios.Model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private BeanPropertyBindingResult bindingResult;

    private MethodParameter parametro;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        handler = new GlobalExceptionHandler();
        bindingResult = new BeanPropertyBindingResult(new Usuario(), "usuario");
        parametro = new MethodParameter(UsuarioController.class.getMethod("crear", Usuario.class), 0);
    }

    @Test
    void validacion_retornaBadRequestConMensajeDelCampo() {
        bindingResult.addError(new FieldError("usuario", "correo", "El correo es obligatorio"));

        ResponseEntity<Map<String, String>> respuesta = handler.manejarValidacion(new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El correo es obligatorio"), respuesta.getBody());
    }

    @Test
    void validacion_sinErroresDeCampo_retornaMensajeGenerico() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarValidacion(new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Datos inválidos"), respuesta.getBody());
    }

    @Test
    void argumentoInvalido_retornaBadRequest() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarSolicitudInvalida(new IllegalArgumentException("El correo ya está registrado"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El correo ya está registrado"), respuesta.getBody());
    }

    @Test
    void noEncontrado_retornaNotFound() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarNoEncontrado(new RuntimeException("Usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Usuario no encontrado"), respuesta.getBody());
    }

    @Test
    void errorGeneral_retornaInternalServerError() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarErrorGeneral(new Exception("Fallo inesperado"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Error interno del servidor"), respuesta.getBody());
    }
}
