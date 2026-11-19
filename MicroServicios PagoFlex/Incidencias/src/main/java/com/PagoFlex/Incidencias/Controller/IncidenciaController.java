package com.PagoFlex.Incidencias.Controller;

import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Service.IncidenciaService;
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
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
@Tag(name = "Incidencias", description = "Reporte y seguimiento de incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    @Operation(summary = "Listar todas las incidencias")
    public ResponseEntity<List<Incidencia>> listarTodas() {
        return ResponseEntity.ok(incidenciaService.listarTodas());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar incidencias de un usuario")
    public ResponseEntity<List<Incidencia>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(incidenciaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una incidencia")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(incidenciaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear incidencia validando usuario vía Feign")
    public ResponseEntity<?> crear(@Valid @RequestBody Incidencia incidencia) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaService.crearIncidencia(incidencia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado y respuesta de soporte de una incidencia")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(incidenciaService.actualizarEstado(
                    id,
                    body.get("estado"),
                    body.get("respuestaSoporte")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar incidencia")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            incidenciaService.eliminarIncidencia(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}