package com.PagoFlex.Compromisos.Exception;

import com.PagoFlex.Compromisos.Controller.CompromisoController;
import com.PagoFlex.Compromisos.Model.Compromiso;
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
        bindingResult = new BeanPropertyBindingResult(new Compromiso(), "compromiso");
        parametro = new MethodParameter(CompromisoController.class.getMethod("crear", Compromiso.class), 0);
    }

    @Test
    void validacion_retornaBadRequestConMensajeDelCampo() {
        bindingResult.addError(new FieldError("compromiso", "titulo", "El título del compromiso es obligatorio"));

        ResponseEntity<Map<String, String>> respuesta = handler.manejarValidacion(new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El título del compromiso es obligatorio"), respuesta.getBody());
    }

    @Test
    void validacion_sinErroresDeCampo_retornaMensajeGenerico() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarValidacion(new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Datos inválidos"), respuesta.getBody());
    }

    @Test
    void argumentoInvalido_retornaBadRequest() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarSolicitudInvalida(new IllegalArgumentException("El usuarioId es obligatorio"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El usuarioId es obligatorio"), respuesta.getBody());
    }

    @Test
    void noEncontrado_retornaNotFound() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarNoEncontrado(new RuntimeException("Compromiso no encontrado con id 99"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Compromiso no encontrado con id 99"), respuesta.getBody());
    }

    @Test
    void errorGeneral_retornaInternalServerError() {
        ResponseEntity<Map<String, String>> respuesta = handler.manejarErrorGeneral(new Exception("Fallo inesperado"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(Map.of("error", "Error interno del servidor"), respuesta.getBody());
    }
}
