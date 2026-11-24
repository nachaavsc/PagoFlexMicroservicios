package com.PagoFlex.Autenticacion.Controller;

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
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(
                    body.get("nombre"),
                    body.get("correo"),
                    body.get("clave"),
                    body.getOrDefault("telefono", "")
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Validar credenciales de acceso")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(authService.login(body.get("correo"), body.get("clave")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cambiar-clave")
    @Operation(summary = "Actualizar contraseña")
    public ResponseEntity<?> cambiarClave(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(authService.cambiarClave(body.get("correo"), body.get("nuevaClave")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
