package com.PagoFlex.Incidencias.Repository;

import com.PagoFlex.Incidencias.Model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByUsuarioId(Long usuarioId);
    List<Incidencia> findByEstado(String estado);
}