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

    @Test
    void crearCompromiso_exitoso() {
        Compromiso c = new Compromiso();
        c.setUsuarioId(10L);
        c.setMontoTotal(1000.0);

        when(usuarioFeignClient.obtenerUsuarioPorId(10L)).thenReturn(Map.of("id", 10L));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        Compromiso creado = service.crearCompromiso(c);
        assertEquals("PENDIENTE", creado.getEstado());
        assertEquals(0.0, creado.getMontoPagado());
    }

    @Test
    void crearCompromiso_usuarioInvalido_lanzaExcepcion() {
        Compromiso c = new Compromiso();
        c.setUsuarioId(99L);
        
        // Simulamos que Feign no encuentra al usuario
        when(usuarioFeignClient.obtenerUsuarioPorId(99L)).thenThrow(new RuntimeException("404"));

        assertThrows(IllegalArgumentException.class, () -> service.crearCompromiso(c));
    }

    @Test
    void registrarPago_parcialYTotal_cambiaEstado() {
        Compromiso comp = new Compromiso();
        comp.setId(1L);
        comp.setMontoTotal(1000.0);
        comp.setMontoPagado(0.0);
        comp.setCuotasPagadas(0);

        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(comp));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(i -> i.getArgument(0));

        // Pago parcial (400)
        Pago pago1 = new Pago();
        pago1.setMonto(400.0);
        service.registrarPago(1L, pago1);
        assertEquals("EN_PROCESO", comp.getEstado());

        // Pago final (600)
        Pago pago2 = new Pago();
        pago2.setMonto(600.0);
        service.registrarPago(1L, pago2);
        assertEquals("PAGADO", comp.getEstado());
    }

    @Test
    void registrarPago_montoMenorACero_lanzaExcepcion() {
        Compromiso comp = new Compromiso();
        comp.setId(1L);
        when(compromisoRepository.findById(1L)).thenReturn(Optional.of(comp));

        Pago pagoInvalido = new Pago();
        pagoInvalido.setMonto(-50.0);

        assertThrows(IllegalArgumentException.class, () -> service.registrarPago(1L, pagoInvalido));
    }
}