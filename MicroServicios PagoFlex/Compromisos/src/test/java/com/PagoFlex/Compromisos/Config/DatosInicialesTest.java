package com.PagoFlex.Compromisos.Config;

import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Repository.CompromisoRepository;
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
    private CompromisoRepository repository;

    private CommandLineRunner runner;

    @BeforeEach
    void setUp() {
        runner = new DatosIniciales().cargarCompromisos(repository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void baseVacia_creaDosCompromisos() throws Exception {
        when(repository.count()).thenReturn(0L);
        ArgumentCaptor<List<Compromiso>> captor = ArgumentCaptor.forClass(List.class);

        runner.run();

        verify(repository).saveAll(captor.capture());
        List<Compromiso> creados = captor.getValue();
        assertEquals(2, creados.size());
        assertEquals("Arriendo departamento", creados.get(0).getTitulo());
        assertEquals(1L, creados.get(0).getUsuarioId());
        assertEquals("PENDIENTE", creados.get(0).getEstado());
        assertEquals("Crédito de consumo", creados.get(1).getTitulo());
        assertEquals(24, creados.get(1).getCuotasTotales());
    }

    @Test
    void baseConDatos_noCreaNada() throws Exception {
        when(repository.count()).thenReturn(2L);

        runner.run();

        verify(repository, never()).saveAll(anyList());
    }
}
