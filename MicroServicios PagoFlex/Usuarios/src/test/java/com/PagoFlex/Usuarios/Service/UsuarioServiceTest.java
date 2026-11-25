package com.PagoFlex.Usuarios.Service;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@pagoflex.cl");
        usuario.setTelefono("912345678");
        usuario.setRol("CLIENTE");
    }

    @Test
    void listarTodos_retornaUsuarios() {
        when(repository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("ana@pagoflex.cl", resultado.get(0).getCorreo());
    }

    @Test
    void obtenerPorId_existente() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        assertEquals("Ana", service.obtenerPorId(1L).getNombre());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void obtenerPorCorreo_existente() {
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(usuario));

        assertEquals(1L, service.obtenerPorCorreo("ana@pagoflex.cl").getId());
    }

    @Test
    void obtenerPorCorreo_noExiste_lanzaExcepcion() {
        when(repository.findByCorreo("no@pagoflex.cl")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorCorreo("no@pagoflex.cl"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void crearUsuario_conservaDatosEnviados() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario creado = service.crearUsuario(usuario);

        assertEquals("Ana", creado.getNombre());
        assertEquals("CLIENTE", creado.getRol());
        verify(repository).save(usuario);
    }

    @Test
    void crearUsuario_asignaValoresPorDefecto() {
        usuario.setNombre(" ");
        usuario.setRol(null);
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario creado = service.crearUsuario(usuario);

        assertEquals("Usuario PagoFlex", creado.getNombre());
        assertEquals("CLIENTE", creado.getRol());
    }

    @Test
    void crearUsuario_nombreNulo_asignaNombrePorDefecto() {
        usuario.setNombre(null);
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Usuario PagoFlex", service.crearUsuario(usuario).getNombre());
    }

    @Test
    void crearUsuario_rolVacio_asignaCliente() {
        usuario.setRol("");
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("CLIENTE", service.crearUsuario(usuario).getRol());
    }

    @Test
    void crearUsuario_sinCorreo_lanzaExcepcion() {
        usuario.setCorreo(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearUsuario(usuario));
        assertEquals("El correo es obligatorio", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void crearUsuario_correoEnBlanco_lanzaExcepcion() {
        usuario.setCorreo("  ");

        assertThrows(IllegalArgumentException.class, () -> service.crearUsuario(usuario));
        verify(repository, never()).save(any());
    }

    @Test
    void crearUsuario_correoDuplicado_lanzaExcepcion() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearUsuario(usuario));
        assertEquals("El correo ya está registrado", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarUsuario_modificaCamposEnviados() {
        Usuario cambios = new Usuario();
        cambios.setNombre("Ana María");
        cambios.setTelefono("987654321");
        cambios.setRutaFoto("/img/ana.jpg");
        cambios.setRol("ADMIN");
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario actualizado = service.actualizarUsuario(1L, cambios);

        assertEquals("Ana María", actualizado.getNombre());
        assertEquals("987654321", actualizado.getTelefono());
        assertEquals("/img/ana.jpg", actualizado.getRutaFoto());
        assertEquals("ADMIN", actualizado.getRol());
    }

    @Test
    void actualizarUsuario_ignoraCamposVaciosONulos() {
        Usuario cambios = new Usuario();
        cambios.setNombre(" ");
        cambios.setRol(" ");
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario actualizado = service.actualizarUsuario(1L, cambios);

        assertEquals("Ana", actualizado.getNombre());
        assertEquals("912345678", actualizado.getTelefono());
        assertNull(actualizado.getRutaFoto());
        assertEquals("CLIENTE", actualizado.getRol());
    }

    @Test
    void actualizarUsuario_rolNulo_conservaRol() {
        Usuario cambios = new Usuario();
        cambios.setRol(null);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("CLIENTE", service.actualizarUsuario(1L, cambios).getRol());
    }

    @Test
    void actualizarUsuario_noExiste_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.actualizarUsuario(99L, new Usuario()));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarUsuario_existente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminarUsuario(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void eliminarUsuario_noExiste_lanzaExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.eliminarUsuario(99L));
        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(repository, never()).deleteById(any());
    }
}
