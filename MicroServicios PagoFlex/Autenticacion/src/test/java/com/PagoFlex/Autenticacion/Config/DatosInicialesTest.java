package com.PagoFlex.Autenticacion.Config;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Repository.AutenticacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatosInicialesTest {

    @Mock
    private AutenticacionRepository repository;

    private CommandLineRunner runner;

    @BeforeEach
    void setUp() {
        runner = new DatosIniciales().cargarCredenciales(repository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void baseVacia_creaClienteYGestor() throws Exception {
        when(repository.count()).thenReturn(0L);
        ArgumentCaptor<List<Autenticacion>> captor = ArgumentCaptor.forClass(List.class);

        runner.run();

        verify(repository).saveAll(captor.capture());
        List<Autenticacion> creadas = captor.getValue();
        assertEquals(2, creadas.size());
        assertEquals("CLIENTE", creadas.get(0).getRol());
        assertEquals(1L, creadas.get(0).getUsuarioId());
        assertEquals("cliente@pagoflex.cl", creadas.get(0).getCorreo());
        assertEquals("GESTOR", creadas.get(1).getRol());
        assertEquals(2L, creadas.get(1).getUsuarioId());
        assertEquals("gestor@pagoflex.cl", creadas.get(1).getCorreo());
    }

    @Test
    void baseConDatos_noCreaNada() throws Exception {
        when(repository.count()).thenReturn(5L);

        runner.run();

        verify(repository, never()).saveAll(anyList());
    }
}
