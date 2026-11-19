package com.PagoFlex.Autenticacion.Service;

import com.PagoFlex.Autenticacion.Client.UsuarioFeignClient;
import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Repository.AutenticacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutenticacionServiceTest {

    @Mock
    private AutenticacionRepository repository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private AutenticacionService service;

    @Test
    void registrar_Exitoso() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(usuarioFeignClient.crearPerfilUsuario(any())).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Autenticacion.class))).thenAnswer(i -> i.getArgument(0));

        Autenticacion res = service.registrar("Ana", "ana@pagoflex.cl", "1234", "999999999");

        assertEquals(10L, res.getUsuarioId());
        assertEquals("ana@pagoflex.cl", res.getCorreo());
        verify(repository).save(any(Autenticacion.class));
    }

    @Test
    void registrar_CorreoDuplicado_LanzaExcepcion() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar("Ana", "ana@pagoflex.cl", "1234", "999999999"));
        verify(repository, never()).save(any());
    }

    @Test
    void login_Exitoso() {
        Autenticacion cred = new Autenticacion();
        cred.setCorreo("ana@pagoflex.cl");
        cred.setClaveHash("1234");
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(cred));

        Autenticacion res = service.login("ana@pagoflex.cl", "1234");
        assertEquals("ana@pagoflex.cl", res.getCorreo());
    }

    @Test
    void login_ClaveIncorrecta_LanzaExcepcion() {
        Autenticacion cred = new Autenticacion();
        cred.setCorreo("ana@pagoflex.cl");
        cred.setClaveHash("1234");
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(cred));

        assertThrows(RuntimeException.class, () -> service.login("ana@pagoflex.cl", "wrong"));
    }

    @Test
    void cambiarClave_Exitoso() {
        Autenticacion cred = new Autenticacion();
        cred.setCorreo("ana@pagoflex.cl");
        cred.setClaveHash("1234");
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(cred));
        when(repository.save(any(Autenticacion.class))).thenAnswer(i -> i.getArgument(0));

        Autenticacion res = service.cambiarClave("ana@pagoflex.cl", "5678");
        assertEquals("5678", res.getClaveHash());
    }

    @Test
    void cambiarClave_NoExiste_LanzaExcepcion() {
        when(repository.findByCorreo("no@pagoflex.cl")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.cambiarClave("no@pagoflex.cl", "5678"));
    }
}