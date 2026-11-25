package com.PagoFlex.Autenticacion.Config;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Repository.AutenticacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarCredenciales(AutenticacionRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        crear(1L, "cliente@pagoflex.cl", "CLIENTE"),
                        crear(2L, "gestor@pagoflex.cl", "GESTOR")
                ));
            }
        };
    }

    private Autenticacion crear(Long usuarioId, String correo, String rol) {
        Autenticacion cred = new Autenticacion();
        cred.setUsuarioId(usuarioId);
        cred.setCorreo(correo);
        cred.setClaveHash("123456");
        cred.setRol(rol);
        return cred;
    }
}
