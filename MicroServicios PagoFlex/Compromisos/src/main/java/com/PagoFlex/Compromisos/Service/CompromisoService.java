package com.PagoFlex.Compromisos.Service;

import com.PagoFlex.Compromisos.Client.UsuarioFeignClient;
import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Repository.CompromisoRepository;
import com.PagoFlex.Compromisos.Repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompromisoService {

    private final CompromisoRepository compromisoRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<Compromiso> listarTodos() {
        return compromisoRepository.findAll();
    }

    public List<Compromiso> listarPorUsuario(Long usuarioId) {
        return compromisoRepository.findByUsuarioId(usuarioId);
    }

    public Compromiso obtenerPorId(Long id) {
        return compromisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compromiso no encontrado"));
    }

    public Compromiso crearCompromiso(Compromiso compromiso) {
        if (compromiso.getUsuarioId() == null) {
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        }
        try {
            Map<String, Object> usuario = usuarioFeignClient.obtenerUsuarioPorId(compromiso.getUsuarioId());
            if (usuario == null || usuario.isEmpty()) {
                throw new IllegalArgumentException("El usuario no existe");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo validar el usuario en ms-usuarios");
        }

        if (compromiso.getMontoPagado() == null) compromiso.setMontoPagado(0.0);
        if (compromiso.getCuotasTotales() == null || compromiso.getCuotasTotales() < 1) compromiso.setCuotasTotales(1);
        if (compromiso.getCuotasPagadas() == null) compromiso.setCuotasPagadas(0);
        if (compromiso.getEstado() == null || compromiso.getEstado().isBlank()) compromiso.setEstado("PENDIENTE");

        return compromisoRepository.save(compromiso);
    }

    public Pago registrarPago(Long compromisoId, Pago pago) {
        Compromiso comp = obtenerPorId(compromisoId);
        if (pago.getMonto() == null || pago.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }

        pago.setCompromisoId(compromisoId);
        if (pago.getFechaPago() == null) pago.setFechaPago(LocalDateTime.now());
        if (pago.getMetodoPago() == null || pago.getMetodoPago().isBlank()) pago.setMetodoPago("TRANSFERENCIA");
        Pago pagoGuardado = pagoRepository.save(pago);

        double nuevoPagado = (comp.getMontoPagado() != null ? comp.getMontoPagado() : 0.0) + pago.getMonto();
        int nuevasCuotas = (comp.getCuotasPagadas() != null ? comp.getCuotasPagadas() : 0) + 1;
        comp.setMontoPagado(nuevoPagado);
        comp.setCuotasPagadas(nuevasCuotas);
        comp.setEstado(nuevoPagado >= comp.getMontoTotal() ? "PAGADO" : "EN_PROCESO");
        compromisoRepository.save(comp);

        return pagoGuardado;
    }

    public List<Pago> listarPagosPorCompromiso(Long compromisoId) {
        return pagoRepository.findByCompromisoId(compromisoId);
    }

    public void eliminarCompromiso(Long id) {
        if (!compromisoRepository.existsById(id)) {
            throw new RuntimeException("Compromiso no encontrado");
        }
        compromisoRepository.deleteById(id);
    }
}