package com.PagoFlex.Autenticacion.Controller;

import com.PagoFlex.Autenticacion.Model.Autenticacion;
import com.PagoFlex.Autenticacion.Service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y cambio de clave")
public class AuthController {

    private final AutenticacionService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario y sincronizar perfil vía Feign")
    public ResponseEntity<Autenticacion> registrar(@RequestBody Map<String, String> body) {
        Autenticacion creada = authService.registrar(
                body.get("nombre"),
                body.get("correo"),
                body.get("clave"),
                body.getOrDefault("telefono", "")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PostMapping("/login")
    @Operation(summary = "Validar credenciales de acceso")
    public ResponseEntity<Autenticacion> login(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.login(body.get("correo"), body.get("clave")));
    }

    @PutMapping("/cambiar-clave")
    @Operation(summary = "Actualizar contraseña")
    public ResponseEntity<Autenticacion> cambiarClave(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.cambiarClave(body.get("correo"), body.get("nuevaClave")));
    }
}   