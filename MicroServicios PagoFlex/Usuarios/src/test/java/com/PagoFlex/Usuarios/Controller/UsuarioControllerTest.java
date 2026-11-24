package com.PagoFlex.Usuarios.Controller;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@pagoflex.cl");
    }

    @Test
    void listarTodos_retornaOk() {
        when(service.listarTodos()).thenReturn(List.of(usuario));

        ResponseEntity<List<Usuario>> respuesta = controller.listarTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    void obtenerPorId_existente_retornaOk() {
        when(service.obtenerPorId(1L)).thenReturn(usuario);

        ResponseEntity<?> respuesta = controller.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuario, respuesta.getBody());
    }

    @Test
    void obtenerPorId_noExiste_retornaNotFound() {
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("Usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());
    }

    @Test
    void obtenerPorCorreo_existente_retornaOk() {
        when(service.obtenerPorCorreo("ana@pagoflex.cl")).thenReturn(usuario);

        ResponseEntity<?> respuesta = controller.obtenerPorCorreo("ana@pagoflex.cl");

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuario, respuesta.getBody());
    }

    @Test
    void obtenerPorCorreo_noExiste_retornaNotFound() {
        when(service.obtenerPorCorreo("no@pagoflex.cl")).thenThrow(new RuntimeException("Usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorCorreo("no@pagoflex.cl").getStatusCode());
    }

    @Test
    void crear_valido_retornaCreated() {
        when(service.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> respuesta = controller.crear(usuario);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(usuario, respuesta.getBody());
    }

    @Test
    void crear_correoDuplicado_retornaBadRequestConError() {
        when(service.crearUsuario(any(Usuario.class))).thenThrow(new IllegalArgumentException("El correo ya está registrado"));

        ResponseEntity<?> respuesta = controller.crear(usuario);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(Map.of("error", "El correo ya está registrado"), respuesta.getBody());
    }

    @Test
    void actualizar_existente_retornaOk() {
        when(service.actualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> respuesta = controller.actualizar(1L, usuario);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(usuario, respuesta.getBody());
    }

    @Test
    void actualizar_noExiste_retornaNotFound() {
        when(service.actualizarUsuario(eq(99L), any(Usuario.class))).thenThrow(new RuntimeException("Usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, controller.actualizar(99L, usuario).getStatusCode());
    }

    @Test
    void eliminar_existente_retornaNoContent() {
        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        verify(service).eliminarUsuario(1L);
    }

    @Test
    void eliminar_noExiste_retornaNotFound() {
        doThrow(new RuntimeException("Usuario no encontrado")).when(service).eliminarUsuario(99L);

        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}
