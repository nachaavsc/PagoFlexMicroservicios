package com.PagoFlex.Incidencias.Service;

import com.PagoFlex.Incidencias.Client.UsuarioFeignClient;
import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Repository.IncidenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository repository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private IncidenciaService service;

    @Test
    void listarYBuscar_ExitosoYNoExiste() {
        Incidencia inc = new Incidencia();
        inc.setId(1L);

        when(repository.findAll()).thenReturn(List.of(inc));
        when(repository.findByUsuarioId(10L)).thenReturn(List.of(inc));
        when(repository.findById(1L)).thenReturn(Optional.of(inc));

        assertEquals(1, service.listarTodas().size());
        assertEquals(1, service.listarPorUsuario(10L).size());
        assertEquals(1L, service.obtenerPorId(1L).getId());

        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void crearIncidencia_ExitosoYErrores() {
        Incidencia inc = new Incidencia();
        inc.setUsuarioId(10L);
        inc.setTitulo("Error en pago");
        inc.setDescripcion("No se reflejó la transferencia");

        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia creada = service.crearIncidencia(inc);
        assertEquals("ABIERTA", creada.getEstado());
        assertNotNull(creada.getFechaCreacion());

        assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(new Incidencia()));

        Incidencia usuarioInvalido = new Incidencia();
        usuarioInvalido.setUsuarioId(99L);
        when(usuarioFeignClient.obtenerUsuarioPorId(99L)).thenThrow(new RuntimeException("404"));
        assertThrows(IllegalArgumentException.class, () -> service.crearIncidencia(usuarioInvalido));
    }

    @Test
    void actualizarEstadoYEliminar_ExitosoYErrores() {
        Incidencia inc = new Incidencia();
        inc.setId(1L);
        inc.setEstado("ABIERTA");

        when(repository.findById(1L)).thenReturn(Optional.of(inc));
        when(repository.save(any(Incidencia.class))).thenAnswer(i -> i.getArgument(0));

        Incidencia actualizada = service.actualizarEstado(1L, "RESUELTA", "Pago conciliado");
        assertEquals("RESUELTA", actualizada.getEstado());
        assertEquals("Pago conciliado", actualizada.getRespuestaSoporte());

        when(repository.existsById(1L)).thenReturn(true);
        service.eliminarIncidencia(1L);
        verify(repository).deleteById(1L);

        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.eliminarIncidencia(99L));
    }
}