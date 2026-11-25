package com.PagoFlex.Compromisos.Controller;

import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Service.CompromisoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompromisoControllerTest {

    @Mock
    private CompromisoService service;

    @InjectMocks
    private CompromisoController controller;

    private Compromiso compromiso;

    private Pago pago;

    @BeforeEach
    void setUp() {
        compromiso = new Compromiso();
        compromiso.setId(1L);
        compromiso.setUsuarioId(10L);
        compromiso.setTitulo("Arriendo");
        compromiso.setMontoTotal(1000.0);

        pago = new Pago();
        pago.setMonto(400.0);
    }

    @Test
    void listarTodos_retornaOk() {
        when(service.listarTodos()).thenReturn(List.of(compromiso));

        ResponseEntity<List<Compromiso>> respuesta = controller.listarTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    void listarPorUsuario_retornaOk() {
        when(service.listarPorUsuario(10L)).thenReturn(List.of(compromiso));

        assertEquals(HttpStatus.OK, controller.listarPorUsuario(10L).getStatusCode());
    }

    @Test
    void obtenerPorId_existente_retornaOk() {
        when(service.obtenerPorId(1L)).thenReturn(compromiso);

        ResponseEntity<?> respuesta = controller.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(compromiso, respuesta.getBody());
    }

    @Test
    void obtenerPorId_noExiste_retornaNotFound() {
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("Compromiso no encontrado con id 99"));

        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());
    }

    @Test
    void crear_valido_retornaCreated() {
        when(service.crearCompromiso(any(Compromiso.class))).thenReturn(compromiso);

        ResponseEntity<?> respuesta = controller.crear(compromiso);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(compromiso, respuesta.getBody());
    }

    @Test
    void crear_usuarioInvalido_retornaBadRequestConError() {
        when(service.crearCompromiso(any(Compromiso.class))).thenThrow(new IllegalArgumentException("El usuario no existe"));

        ResponseEntity<?> respuesta = controller.crear(compromiso);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El usuario no existe"), respuesta.getBody());
    }

    @Test
    void registrarPago_valido_retornaCreated() {
        when(service.registrarPago(eq(1L), any(Pago.class))).thenReturn(pago);

        ResponseEntity<?> respuesta = controller.registrarPago(1L, pago);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(pago, respuesta.getBody());
    }

    @Test
    void registrarPago_montoInvalido_retornaBadRequestConError() {
        when(service.registrarPago(eq(1L), any(Pago.class))).thenThrow(new IllegalArgumentException("El monto del pago debe ser mayor a cero"));

        ResponseEntity<?> respuesta = controller.registrarPago(1L, pago);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El monto del pago debe ser mayor a cero"), respuesta.getBody());
    }

    @Test
    void registrarPago_compromisoNoExiste_retornaNotFound() {
        when(service.registrarPago(eq(99L), any(Pago.class))).thenThrow(new RuntimeException("Compromiso no encontrado con id 99"));

        assertEquals(HttpStatus.NOT_FOUND, controller.registrarPago(99L, pago).getStatusCode());
    }

    @Test
    void listarPagos_retornaOk() {
        when(service.listarPagosPorCompromiso(1L)).thenReturn(List.of(pago));

        ResponseEntity<List<Pago>> respuesta = controller.listarPagos(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    void eliminar_existente_retornaNoContent() {
        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        verify(service).eliminarCompromiso(1L);
    }

    @Test
    void eliminar_noExiste_retornaNotFound() {
        doThrow(new RuntimeException("Compromiso no encontrado con id 99")).when(service).eliminarCompromiso(99L);

        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}
