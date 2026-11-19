package com.PagoFlex.Compromisos.Controller;

import com.PagoFlex.Compromisos.Model.Compromiso;
import com.PagoFlex.Compromisos.Model.Pago;
import com.PagoFlex.Compromisos.Service.CompromisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compromisos")
@RequiredArgsConstructor
@Tag(name = "Compromisos", description = "Gestión de compromisos de pago y abonos")
public class CompromisoController {

    private final CompromisoService compromisoService;

    @GetMapping
    @Operation(summary = "Listar todos los compromisos")
    public ResponseEntity<List<Compromiso>> listarTodos() {
        return ResponseEntity.ok(compromisoService.listarTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar compromisos de un usuario")
    public ResponseEntity<List<Compromiso>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(compromisoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un compromiso")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compromisoService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear compromiso validando usuario vía Feign")
    public ResponseEntity<?> crear(@Valid @RequestBody Compromiso compromiso) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(compromisoService.crearCompromiso(compromiso));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/pagos")
    @Operation(summary = "Registrar un abono o pago sobre un compromiso")
    public ResponseEntity<?> registrarPago(@PathVariable Long id, @RequestBody Pago pago) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(compromisoService.registrarPago(id, pago));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/pagos")
    @Operation(summary = "Listar pagos asociados a un compromiso")
    public ResponseEntity<List<Pago>> listarPagos(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.listarPagosPorCompromiso(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un compromiso")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            compromisoService.eliminarCompromiso(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}