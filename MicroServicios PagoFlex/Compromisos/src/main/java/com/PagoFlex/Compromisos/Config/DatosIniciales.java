package com.PagoFlex.Compromisos.Config;

import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Repository.CompromisoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarCompromisos(CompromisoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        crear("Arriendo departamento", "Pago mensual del arriendo", 450000.0, 12, LocalDate.now().plusMonths(1)),
                        crear("Crédito de consumo", "Cuotas del crédito bancario", 1200000.0, 24, LocalDate.now().plusMonths(2))
                ));
            }
        };
    }

    private Compromiso crear(String titulo, String descripcion, Double montoTotal, Integer cuotasTotales, LocalDate fechaVencimiento) {
        Compromiso compromiso = new Compromiso();
        compromiso.setUsuarioId(1L);
        compromiso.setTitulo(titulo);
        compromiso.setDescripcion(descripcion);
        compromiso.setMontoTotal(montoTotal);
        compromiso.setCuotasTotales(cuotasTotales);
        compromiso.setFechaVencimiento(fechaVencimiento);
        return compromiso;
    }
}
