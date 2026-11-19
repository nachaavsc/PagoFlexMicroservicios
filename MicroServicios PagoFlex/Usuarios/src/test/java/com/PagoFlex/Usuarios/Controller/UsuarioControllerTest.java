package com.PagoFlex.Usuarios.Controller;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @InjectMocks
    private UsuarioController controller;

    @Test
    void endpointsCrudCompletos() {
        when(service.listarTodos()).thenReturn(List.of(new Usuario()));
        when(service.obtenerPorId(1L)).thenReturn(new Usuario());
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("No existe"));
        when(service.obtenerPorCorreo("ana@pagoflex.cl")).thenReturn(new Usuario());
        when(service.obtenerPorCorreo("no@pagoflex.cl")).thenThrow(new RuntimeException("No existe"));

        assertEquals(HttpStatus.OK, controller.listarTodos().getStatusCode());
        assertEquals(HttpStatus.OK, controller.obtenerPorId(1L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorId(99L).getStatusCode());
        assertEquals(HttpStatus.OK, controller.obtenerPorCorreo("ana@pagoflex.cl").getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerPorCorreo("no@pagoflex.cl").getStatusCode());

        when(service.crearUsuario(any())).thenReturn(new Usuario());
        assertEquals(HttpStatus.CREATED, controller.crear(new Usuario()).getStatusCode());
        when(service.crearUsuario(any())).thenThrow(new IllegalArgumentException("Duplicado"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.crear(new Usuario()).getStatusCode());

        when(service.actualizarUsuario(eq(1L), any())).thenReturn(new Usuario());
        assertEquals(HttpStatus.OK, controller.actualizar(1L, new Usuario()).getStatusCode());
        when(service.actualizarUsuario(eq(99L), any())).thenThrow(new RuntimeException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, controller.actualizar(99L, new Usuario()).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        doThrow(new RuntimeException("No existe")).when(service).eliminarUsuario(99L);
        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(99L).getStatusCode());
    }
}