package com.PagoFlex.Usuarios.Config;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Repository.UsuarioRepository;
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
    private UsuarioRepository repository;

    private CommandLineRunner runner;

    @BeforeEach
    void setUp() {
        runner = new DatosIniciales().cargarUsuarios(repository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void baseVacia_creaClienteYGestor() throws Exception {
        when(repository.count()).thenReturn(0L);
        ArgumentCaptor<List<Usuario>> captor = ArgumentCaptor.forClass(List.class);

        runner.run();

        verify(repository).saveAll(captor.capture());
        List<Usuario> creados = captor.getValue();
        assertEquals(2, creados.size());
        assertEquals("cliente@pagoflex.cl", creados.get(0).getCorreo());
        assertEquals("CLIENTE", creados.get(0).getRol());
        assertEquals("gestor@pagoflex.cl", creados.get(1).getCorreo());
        assertEquals("GESTOR", creados.get(1).getRol());
    }

    @Test
    void baseConDatos_noCreaNada() throws Exception {
        when(repository.count()).thenReturn(3L);

        runner.run();

        verify(repository, never()).saveAll(anyList());
    }
}
