package com.PagoFlex.Incidencias.Controller;

import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Service.IncidenciaService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenciaControllerTest {

    @Mock
    private IncidenciaService service;

    @InjectMocks
    private IncidenciaController controller;

    private Incidencia incidencia;

    @BeforeEach
    void setUp() {
        incidencia = new Incidencia();
        incidencia.setId(1L);
        incidencia.setUsuarioId(10L);
        incidencia.setTitulo("Error en pago");
        incidencia.setDescripcion("No se reflejó la transferencia");
    }

    @Test
    void listarTodas_retornaOk() {
        when(service.listarTodas()).thenReturn(List.of(incidencia));

        ResponseEntity<List<Incidencia>> respuesta = controller.listarTodas();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    void listarPorUsuario_retornaOk() {
        when(service.listarPorUsuario(10L)).thenReturn(List.of(incidencia));

        assertEquals(HttpStatus.OK, controller.listarPorUsuario(10L).getStatusCode());
    }

    @Test
    void obtenerPorId_existente_retornaOk() {
        when(service.obtenerPorId(1L)).thenReturn(incidencia);

        ResponseEntity<?> respuesta = controller.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(incidencia, respuesta.getBody());
    }

    @Test
    void obtenerPorId_noExiste_retornaNotFound() {
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("Incidencia no encontrada"));

        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());
    }

    @Test
    void crear_valida_retornaCreated() {
        when(service.crearIncidencia(any(Incidencia.class))).thenReturn(incidencia);

        ResponseEntity<?> respuesta = controller.crear(incidencia);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(incidencia, respuesta.getBody());
    }

    @Test
    void crear_usuarioInvalido_retornaBadRequestConError() {
        when(service.crearIncidencia(any(Incidencia.class))).thenThrow(new IllegalArgumentException("El usuario no existe"));

        ResponseEntity<?> respuesta = controller.crear(incidencia);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El usuario no existe"), respuesta.getBody());
    }

    @Test
    void actualizarEstado_existente_retornaOk() {
        when(service.actualizarEstado(1L, "RESUELTA", "Listo")).thenReturn(incidencia);

        ResponseEntity<?> respuesta = controller.actualizarEstado(1L, Map.of("estado", "RESUELTA", "respuestaSoporte", "Listo"));

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(incidencia, respuesta.getBody());
    }

    @Test
    void actualizarEstado_noExiste_retornaNotFound() {
        when(service.actualizarEstado(99L, "RESUELTA", null)).thenThrow(new RuntimeException("Incidencia no encontrada"));

        assertEquals(HttpStatus.NOT_FOUND, controller.actualizarEstado(99L, Map.of("estado", "RESUELTA")).getStatusCode());
    }

    @Test
    void eliminar_existente_retornaNoContent() {
        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        verify(service).eliminarIncidencia(1L);
    }

    @Test
    void eliminar_noExiste_retornaNotFound() {
        doThrow(new RuntimeException("Incidencia no encontrada")).when(service).eliminarIncidencia(99L);

        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}
