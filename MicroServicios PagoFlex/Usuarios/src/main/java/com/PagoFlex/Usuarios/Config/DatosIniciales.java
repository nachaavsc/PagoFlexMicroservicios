package com.PagoFlex.Usuarios.Config;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        crear("Cliente PagoFlex", "cliente@pagoflex.cl", "911111111", "CLIENTE"),
                        crear("Gestor PagoFlex", "gestor@pagoflex.cl", "922222222", "GESTOR")
                ));
            }
        };
    }

    private Usuario crear(String nombre, String correo, String telefono, String rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setRol(rol);
        return usuario;
    }
}
