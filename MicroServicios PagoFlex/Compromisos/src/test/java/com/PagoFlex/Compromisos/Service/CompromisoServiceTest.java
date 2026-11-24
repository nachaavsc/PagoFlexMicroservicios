package com.PagoFlex.Compromisos.Service;

import com.PagoFlex.Compromisos.Client.UsuarioFeignClient;
import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Repository.CompromisoRepository;
import com.PagoFlex.Compromisos.Repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CompromisoServiceTest {

    @Mock
    private CompromisoRepository compromisoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private CompromisoService service;

    private Compromiso compromiso;

    private Pago pago;

    @BeforeEach
    void setUp() {
        compromiso = new Compromiso();
        compromiso.setId(1L);
        compromiso.setUsuarioId(10L);
        compromiso.setTitulo("Arriendo");
        compromiso.setMontoTotal(1000.0);

        pago = new Pago();
        pago.setMonto(400.0);
    }

    @Test
    void listarTodos_retornaCompromisos() {
        when(compromisoRepository.findAll()).thenReturn(List.of(compromiso));

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void listarPorUsuario_retornaCompromisosDelUsuario() {
        when(compromisoRepository.findByUsuarioId(10L)).thenReturn(List.of(compromiso));

        assertEquals("Arriendo", service.listarPorUsuario(10L).get(0).getTitulo());
    }

    @Test
    void obtenerPorId_existente() {
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(compromiso));

        assertEquals(1L, service.obtenerPorId(1L).getId());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(compromisoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
        assertEquals("Compromiso no encontrado con id 99", ex.getMessage());
    }

    @Test
    void crearCompromiso_exitoso() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        Compromiso creado = service.crearCompromiso(compromiso);

        assertEquals("PENDIENTE", creado.getEstado());
        assertEquals(0.0, creado.getMontoPagado());
        verify(compromisoRepository).save(compromiso);
    }

    @Test
    void crearCompromiso_sinUsuarioId_lanzaExcepcion() {
        compromiso.setUsuarioId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(compromiso));
        assertEquals("El usuarioId es obligatorio", ex.getMessage());
        verifyNoInteractions(usuarioFeignClient);
    }

    @Test
    void crearCompromiso_usuarioVacio_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(compromiso));
        assertEquals("El usuario no existe", ex.getMessage());
        verify(compromisoRepository, never()).save(any());
    }

    @Test
    void crearCompromiso_usuarioNulo_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(compromiso));
        verify(compromisoRepository, never()).save(any());
    }

    @Test
    void crearCompromiso_falloFeign_lanzaExcepcion() {
        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenThrow(new RuntimeException("404"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(compromiso));
        assertEquals("No se pudo validar el usuario en ms-usuarios", ex.getMessage());
    }

    @Test
    void registrarPago_parcialYTotal_cambiaEstado() {
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(compromiso));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        Pago guardado = service.registrarPago(1L, pago);
        assertEquals(1L, guardado.getCompromisoId());
        assertEquals("EN_PROCESO", compromiso.getEstado());
        assertEquals(400.0, compromiso.getMontoPagado());
        assertEquals(1, compromiso.getCuotasPagadas());

        Pago pagoFinal = new Pago();
        pagoFinal.setMonto(600.0);
        service.registrarPago(1L, pagoFinal);
        assertEquals("PAGADO", compromiso.getEstado());
        assertEquals(2, compromiso.getCuotasPagadas());
    }

    @Test
    void registrarPago_sinMontosPrevios_partenEnCero() {
        compromiso.setMontoPagado(null);
        compromiso.setCuotasPagadas(null);
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(compromiso));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        service.registrarPago(1L, pago);

        assertEquals(400.0, compromiso.getMontoPagado());
        assertEquals(1, compromiso.getCuotasPagadas());
    }

    @Test
    void registrarPago_montoNegativo_lanzaExcepcion() {
        pago.setMonto(-50.0);
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(compromiso));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.registrarPago(1L, pago));
        assertEquals("El monto del pago debe ser mayor a cero", ex.getMessage());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void registrarPago_montoNulo_lanzaExcepcion() {
        pago.setMonto(null);
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(compromiso));

        assertThrows(IllegalArgumentException.class, () -> service.registrarPago(1L, pago));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void registrarPago_compromisoNoExiste_lanzaExcepcion() {
        when(compromisoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.registrarPago(99L, pago));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void listarPagosPorCompromiso_retornaPagos() {
        when(pagoRepository.findByCompromisoId(1L)).thenReturn(List.of(pago));

        assertEquals(1, service.listarPagosPorCompromiso(1L).size());
    }

    @Test
    void eliminarCompromiso_existente() {
        when(compromisoRepository.existsById(1L)).thenReturn(true);

        service.eliminarCompromiso(1L);

        verify(compromisoRepository).deleteById(1L);
    }

    @Test
    void eliminarCompromiso_noExiste_lanzaExcepcion() {
        when(compromisoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.eliminarCompromiso(99L));
        verify(compromisoRepository, never()).deleteById(any());
    }
}
