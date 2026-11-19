package com.PagoFlex.Usuarios.Service;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void listarYBuscar_ExitosoYNoExiste() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setCorreo("ana@pagoflex.cl");

        when(repository.findAll()).thenReturn(List.of(u));
        when(repository.findById(1L)).thenReturn(Optional.of(u));
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(u));

        assertEquals(1, service.listarTodos().size());
        assertEquals(1L, service.obtenerPorId(1L).getId());
        assertEquals("ana@pagoflex.cl", service.obtenerPorCorreo("ana@pagoflex.cl").getCorreo());

        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void crearUsuario_ExitosoYErrores() {
        Usuario u = new Usuario();
        u.setCorreo("nuevo@pagoflex.cl");

        when(repository.existsByCorreo("nuevo@pagoflex.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario creado = service.crearUsuario(u);
        assertEquals("Usuario PagoFlex", creado.getNombre());
        assertEquals("CLIENTE", creado.getRol());

        assertThrows(IllegalArgumentException.class, () -> service.crearUsuario(new Usuario()));

        when(repository.existsByCorreo("duplicado@pagoflex.cl")).thenReturn(true);
        Usuario dup = new Usuario();
        dup.setCorreo("duplicado@pagoflex.cl");
        assertThrows(IllegalArgumentException.class, () -> service.crearUsuario(dup));
    }

    @Test
    void actualizarYEliminar_ExitosoYErrores() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNombre("Ana");

        Usuario cambios = new Usuario();
        cambios.setNombre("Ana María");
        cambios.setTelefono("987654321");
        cambios.setRutaFoto("/img/ana.jpg");
        cambios.setRol("ADMIN");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario actualizado = service.actualizarUsuario(1L, cambios);
        assertEquals("Ana María", actualizado.getNombre());
        assertEquals("987654321", actualizado.getTelefono());

        when(repository.existsById(1L)).thenReturn(true);
        service.eliminarUsuario(1L);
        verify(repository).deleteById(1L);

        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.eliminarUsuario(99L));
    }
}