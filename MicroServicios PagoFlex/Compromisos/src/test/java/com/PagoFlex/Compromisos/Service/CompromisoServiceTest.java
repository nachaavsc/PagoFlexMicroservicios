package com.PagoFlex.Compromisos.Service;

import com.PagoFlex.Compromisos.Client.UsuarioFeignClient;
import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Repository.CompromisoRepository;
import com.PagoFlex.Compromisos.Repository.PagoRepository;
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
public class CompromisoServiceTest {

    @Mock
    private CompromisoRepository compromisoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private CompromisoService service;

    @Test
    void listarYBuscar_Exitoso() {
        Compromiso c = new Compromiso();
        c.setId(1L);
        when(compromisoRepository.findAll()).thenReturn(List.of(c));
        when(compromisoRepository.findByUsuarioId(10L)).thenReturn(List.of(c));
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(c));
        when(pagoRepository.findByCompromisoId(1L)).thenReturn(List.of(new Pago()));

        assertEquals(1, service.listarTodos().size());
        assertEquals(1, service.listarPorUsuario(10L).size());
        assertEquals(1L, service.obtenerPorId(1L).getId());
        assertEquals(1, service.listarPagosPorCompromiso(1L).size());
    }

    @Test
    void crearCompromiso_ExitosoYErrores() {
        Compromiso c = new Compromiso();
        c.setUsuarioId(10L);
        c.setTitulo("Cuota Arriendo");
        c.setMontoTotal(300000.0);

        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        Compromiso creado = service.crearCompromiso(c);
        assertEquals("PENDIENTE", creado.getEstado());
        assertEquals(0.0, creado.getMontoPagado());

        Compromiso sinUsuario = new Compromiso();
        assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(sinUsuario));

        Compromiso usuarioInvalido = new Compromiso();
        usuarioInvalido.setUsuarioId(99L);
        when(usuarioFeignClient.obtenerUsuarioPorId(99L)).thenThrow(new RuntimeException("404"));
        assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(usuarioInvalido));
    }

    @Test
    void registrarPago_ParcialYTotal() {
        Compromiso comp = new Compromiso();
        comp.setId(1L);
        comp.setMontoTotal(1000.0);
        comp.setMontoPagado(0.0);
        comp.setCuotasPagadas(0);

        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(comp));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        Pago abono1 = new Pago();
        abono1.setMonto(400.0);
        service.registrarPago(1L, abono1);
        assertEquals("EN_PROCESO", comp.getEstado());

        Pago abono2 = new Pago();
        abono2.setMonto(600.0);
        service.registrarPago(1L, abono2);
        assertEquals("PAGADO", comp.getEstado());

        Pago invalido = new Pago();
        invalido.setMonto(0.0);
        assertThrows(IllegalArgumentException.class, () -> service.registrarPago(1L, invalido));
    }

    @Test
    void eliminarCompromiso_ExitosoYNoExiste() {
        when(compromisoRepository.existsById(1L)).thenReturn(true);
        service.eliminarCompromiso(1L);
        verify(compromisoRepository).deleteById(1L);

        when(compromisoRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.eliminarCompromiso(99L));
    }
}