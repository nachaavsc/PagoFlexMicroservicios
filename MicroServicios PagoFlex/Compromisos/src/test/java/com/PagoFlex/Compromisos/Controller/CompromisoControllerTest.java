package com.PagoFlex.Compromisos.Controller;

import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Service.CompromisoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompromisoControllerTest {

    @Mock
    private CompromisoService service;

    @InjectMocks
    private CompromisoController controller;

    @Test
    void endpointsLecturaYCreacion() {
        when(service.listarTodos()).thenReturn(List.of(new Compromiso()));
        when(service.listarPorUsuario(1L)).thenReturn(List.of(new Compromiso()));
        when(service.obtenerPorId(1L)).thenReturn(new Compromiso());
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("No existe"));
        when(service.crearCompromiso(any())).thenReturn(new Compromiso());

        assertEquals(HttpStatus.OK, controller.listarTodos().getStatusCode());
        assertEquals(HttpStatus.OK, controller.listarPorUsuario(1L).getStatusCode());
        assertEquals(HttpStatus.OK, controller.obtenerPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());
        assertEquals(HttpStatus.CREATED, controller.crear(new Compromiso()).getStatusCode());

        when(service.crearCompromiso(any())).thenThrow(new IllegalArgumentException("Error"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.crear(new Compromiso()).getStatusCode());
    }

    @Test
    void endpointsPagosYEliminar() {
        when(service.registrarPago(eq(1L), any())).thenReturn(new Pago());
        when(service.listarPagosPorCompromiso(1L)).thenReturn(List.of(new Pago()));

        assertEquals(HttpStatus.CREATED, controller.registrarPago(1L, new Pago()).getStatusCode());
        assertEquals(HttpStatus.OK, controller.listarPagos(1L).getStatusCode());

        when(service.registrarPago(eq(2L), any())).thenThrow(new IllegalArgumentException("Monto invalido"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.registrarPago(2L, new Pago()).getStatusCode());

        when(service.registrarPago(eq(99L), any())).thenThrow(new RuntimeException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, controller.registrarPago(99L, new Pago()).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        doThrow(new RuntimeException("No existe")).when(service).eliminarCompromiso(99L);
        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}