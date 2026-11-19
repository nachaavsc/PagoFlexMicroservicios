package com.PagoFlex.Incidencias.Controller;

import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Service.IncidenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidenciaControllerTest {

    @Mock
    private IncidenciaService service;

    @InjectMocks
    private IncidenciaController controller;

    @Test
    void endpointsCrudCompletos() {
        when(service.listarTodas()).thenReturn(List.of(new Incidencia()));
        when(service.listarPorUsuario(10L)).thenReturn(List.of(new Incidencia()));
        when(service.obtenerPorId(1L)).thenReturn(new Incidencia());
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("No existe"));

        assertEquals(HttpStatus.OK, controller.listarTodas().getStatusCode());
        assertEquals(HttpStatus.OK, controller.listarPorUsuario(10L).getStatusCode());
        assertEquals(HttpStatus.OK, controller.obtenerPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());

        when(service.crearIncidencia(any())).thenReturn(new Incidencia());
        assertEquals(HttpStatus.CREATED, controller.crear(new Incidencia()).getStatusCode());
        when(service.crearIncidencia(any())).thenThrow(new IllegalArgumentException("Usuario invalido"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.crear(new Incidencia()).getStatusCode());

        when(service.actualizarEstado(eq(1L), any(), any())).thenReturn(new Incidencia());
        assertEquals(HttpStatus.OK, controller.actualizarEstado(1L, Map.of("estado", "RESUELTA", "respuestaSoporte", "Listo")).getStatusCode());
        when(service.actualizarEstado(eq(99L), any(), any())).thenThrow(new RuntimeException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, controller.actualizarEstado(99L, Map.of("estado", "RESUELTA")).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        doThrow(new RuntimeException("No existe")).when(service).eliminarIncidencia(99L);
        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}