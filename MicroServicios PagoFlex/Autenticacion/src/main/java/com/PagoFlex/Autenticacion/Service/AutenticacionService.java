package com.PagoFlex.Autenticacion.Service;

import com.PagoFlex.Autenticacion.Client.UsuarioFeignClient;
import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Repository.AutenticacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final AutenticacionRepository repository;
    private final UsuarioFeignClient usuarioFeignClient;

    public Autenticacion registrar(String nombre, String correo, String claveHash, String telefono) {
        if (repository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Map<String, Object> perfilReq = new HashMap<>();
        perfilReq.put("nombre", nombre);
        perfilReq.put("correo", correo);
        perfilReq.put("telefono", telefono);
        perfilReq.put("rol", "CLIENTE");

        Map<String, Object> perfilCreado = usuarioFeignClient.crearPerfilUsuario(perfilReq);
        Long usuarioId = Long.valueOf(perfilCreado.get("id").toString());

        Autenticacion cred = new Autenticacion();
        cred.setUsuarioId(usuarioId);
        cred.setCorreo(correo);
        cred.setClaveHash(claveHash);
        return repository.save(cred);
    }

    public Autenticacion login(String correo, String claveHash) {
        Autenticacion cred = repository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!cred.getClaveHash().equals(claveHash)) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return cred;
    }

    public Autenticacion cambiarClave(String correo, String nuevaClaveHash) {
        Autenticacion cred = repository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        cred.setClaveHash(nuevaClaveHash);
        return repository.save(cred);
    }
}
