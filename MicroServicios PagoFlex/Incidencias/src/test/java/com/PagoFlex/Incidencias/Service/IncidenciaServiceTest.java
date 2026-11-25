package com.PagoFlex.Incidencias.Service;

import com.PagoFlex.Incidencias.Client.UsuarioFeignClient;
import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Repository.IncidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository repository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private IncidenciaService service;

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
    void listarTodas_retornaIncidencias() {
        when(repository.findAll()).thenReturn(List.of(incidencia));

        assertEquals(1, service.listarTodas().size());
    }

    @Test
    void listarPorUsuario_retornaIncidenciasDelUsuario() {
        when(repository.findByUsuarioId(10L)).thenReturn(List.of(incidencia));

        assertEquals("Error en pago", service.listarPorUsuario(10L).get(0).getTitulo());
    }

    @Test
    void obtenerPorId_existente() {
        when(repository.findById(1L)).thenReturn(Optional.of(incidencia));

        assertEquals(1L, service.obtenerPorId(1L).getId());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
        assertEquals("Incidencia no encontrada", ex.getMessage());
    }

    @Test
    void crearIncidencia_asignaEstadoYFechaPorDefecto() {
        incidencia.setEstado(" ");
        incidencia.setFechaCreacion(null);
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia creada = service.crearIncidencia(incidencia);

        assertEquals("ABIERTA", creada.getEstado());
        assertNotNull(creada.getFechaCreacion());
    }

    @Test
    void crearIncidencia_estadoNulo_asignaAbierta() {
        incidencia.setEstado(null);
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("ABIERTA", service.crearIncidencia(incidencia).getEstado());
    }

    @Test
    void crearIncidencia_conservaEstadoYFechaEnviados() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 10, 30);
        incidencia.setEstado("EN_REVISION");
        incidencia.setFechaCreacion(fecha);
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia creada = service.crearIncidencia(incidencia);

        assertEquals("EN_REVISION", creada.getEstado());
        assertEquals(fecha, creada.getFechaCreacion());
    }

    @Test
    void crearIncidencia_sinUsuarioId_lanzaExcepcion() {
        incidencia.setUsuarioId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(incidencia));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
        verifyNoInteractions(usuarioFeignClient);
    }

    @Test
    void crearIncidencia_usuarioVacio_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(incidencia));
        assertEquals("El usuario no existe", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void crearIncidencia_usuarioNulo_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(incidencia));
        verify(repository, never()).save(any());
    }

    @Test
    void crearIncidencia_falloFeign_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenThrow(new RuntimeException("404"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(incidencia));
        assertEquals("No se pudo validar el usuario en ms-usuarios", ex.getMessage());
    }

    @Test
    void actualizarEstado_modificaEstadoYRespuesta() {
        when(repository.findById(1L)).thenReturn(Optional.of(incidencia));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia actualizada = service.actualizarEstado(1L, "RESUELTA", "Pago conciliado");

        assertEquals("RESUELTA", actualizada.getEstado());
        assertEquals("Pago conciliado", actualizada.getRespuestaSoporte());
    }

    @Test
    void actualizarEstado_valoresVaciosONulos_conservaDatos() {
        when(repository.findById(1L)).thenReturn(Optional.of(incidencia));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia actualizada = service.actualizarEstado(1L, " ", null);

        assertEquals("ABIERTA", actualizada.getEstado());
        assertNull(actualizada.getRespuestaSoporte());
    }

    @Test
    void actualizarEstado_estadoNulo_conservaEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(incidencia));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("ABIERTA", service.actualizarEstado(1L, null, "Revisando").getEstado());
    }

    @Test
    void actualizarEstado_noExiste_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.actualizarEstado(99L, "RESUELTA", "Listo"));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarIncidencia_existente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminarIncidencia(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void eliminarIncidencia_noExiste_lanzaExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminarIncidencia(99L));
        assertEquals("Incidencia no encontrada", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }
}
