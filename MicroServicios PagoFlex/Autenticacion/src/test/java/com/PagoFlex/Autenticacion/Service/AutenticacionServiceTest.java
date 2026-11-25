package com.PagoFlex.Autenticacion.Service;

import com.PagoFlex.Autenticacion.Client.UsuarioFeignClient;
import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Repository.AutenticacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private AutenticacionRepository repository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private AutenticacionService service;

    private Autenticacion credencial;

    @BeforeEach
    void setUp() {
        credencial = new Autenticacion();
        credencial.setId(1L);
        credencial.setUsuarioId(10L);
        credencial.setCorreo("ana@pagoflex.cl");
        credencial.setClaveHash("1234");
    }

    @Test
    @SuppressWarnings("unchecked")
    void registrar_creaPerfilYCredencial() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(usuarioFeignClient.crearPerfilUsuario(any())).thenReturn(Map.of("id", 10L));
        when(repository.save(any(Autenticacion.class))).thenAnswer(i -> i.getArgument(0));
        ArgumentCaptor<Map<String, Object>> perfil = ArgumentCaptor.forClass(Map.class);

        Autenticacion creada = service.registrar("Ana", "ana@pagoflex.cl", "1234", "999999999");

        verify(usuarioFeignClient).crearPerfilUsuario(perfil.capture());
        assertEquals("Ana", perfil.getValue().get("nombre"));
        assertEquals("CLIENTE", perfil.getValue().get("rol"));
        assertEquals(10L, creada.getUsuarioId());
        assertEquals("ana@pagoflex.cl", creada.getCorreo());
        assertEquals("1234", creada.getClaveHash());
    }

    @Test
    void registrar_idComoTexto_seConvierte() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(false);
        when(usuarioFeignClient.crearPerfilUsuario(any())).thenReturn(Map.of("id", "25"));
        when(repository.save(any(Autenticacion.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(25L, service.registrar("Ana", "ana@pagoflex.cl", "1234", "").getUsuarioId());
    }

    @Test
    void registrar_correoDuplicado_lanzaExcepcion() {
        when(repository.existsByCorreo("ana@pagoflex.cl")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrar("Ana", "ana@pagoflex.cl", "1234", "999999999"));
        assertEquals("El correo ya está registrado", ex.getMessage());
        verifyNoInteractions(usuarioFeignClient);
        verify(repository, never()).save(any());
    }

    @Test
    void login_credencialesValidas() {
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(credencial));

        assertEquals(10L, service.login("ana@pagoflex.cl", "1234").getUsuarioId());
    }

    @Test
    void login_claveIncorrecta_lanzaExcepcion() {
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(credencial));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login("ana@pagoflex.cl", "mala"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void login_correoNoRegistrado_lanzaExcepcion() {
        when(repository.findByCorreo("no@pagoflex.cl")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login("no@pagoflex.cl", "1234"));
        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void cambiarClave_actualizaClave() {
        when(repository.findByCorreo("ana@pagoflex.cl")).thenReturn(Optional.of(credencial));
        when(repository.save(any(Autenticacion.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("5678", service.cambiarClave("ana@pagoflex.cl", "5678").getClaveHash());
        verify(repository).save(credencial);
    }

    @Test
    void cambiarClave_noExiste_lanzaExcepcion() {
        when(repository.findByCorreo("no@pagoflex.cl")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cambiarClave("no@pagoflex.cl", "5678"));
        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(repository, never()).save(any());
    }
}
