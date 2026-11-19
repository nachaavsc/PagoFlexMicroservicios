package com.PagoFlex.Usuarios.Service;

import com.PagoFlex.Usuarios.Model.Usuario;
import com.PagoFlex.Usuarios.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario obtenerPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (repository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            usuario.setNombre("Usuario PagoFlex");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        }
        return repository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datos) {
        Usuario existente = obtenerPorId(id);
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getTelefono() != null) {
            existente.setTelefono(datos.getTelefono());
        }
        if (datos.getRutaFoto() != null) {
            existente.setRutaFoto(datos.getRutaFoto());
        }
        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            existente.setRol(datos.getRol());
        }
        return repository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }
}