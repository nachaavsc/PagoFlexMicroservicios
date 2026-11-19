package com.PagoFlex.Compromisos.Repository;

import com.PagoFlex.Compromisos.Model.Compromiso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {
    List<Compromiso> findByUsuarioId(Long usuarioId);
    List<Compromiso> findByEstado(String estado);
}