package com.PagoFlex.Autenticacion.Repository;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AutenticacionRepository extends JpaRepository<Autenticacion, Long> {
    Optional<Autenticacion> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}
