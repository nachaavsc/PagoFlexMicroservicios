package com.PagoFlex.Incidencias.Service;

import com.PagoFlex.Incidencias.Client.UsuarioFeignClient;
import com.PagoFlex.Incidencias.Model.Incidencia;
import com.PagoFlex.Incidencias.Repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository repository;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<Incidencia> listarTodas() {
        return repository.findAll();
    }

    public List<Incidencia> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    public Incidencia obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada"));
    }

    public Incidencia crearIncidencia(Incidencia incidencia) {
        if (incidencia.getUsuarioId() == null) {
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        }
        try {
            Map<String, Object> usuario = usuarioFeignClient.obtenerUsuarioPorId(incidencia.getUsuarioId());
            if (usuario == null || usuario.isEmpty()) {
                throw new IllegalArgumentException("El usuario no existe");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo validar el usuario en ms-usuarios");
        }

        if (incidencia.getEstado() == null || incidencia.getEstado().isBlank()) {
            incidencia.setEstado("ABIERTA");
        }
        if (incidencia.getFechaCreacion() == null) {
            incidencia.setFechaCreacion(LocalDateTime.now());
        }
        return repository.save(incidencia);
    }

    public Incidencia actualizarEstado(Long id, String nuevoEstado, String respuestaSoporte) {
        Incidencia inc = obtenerPorId(id);
        if (nuevoEstado != null && !nuevoEstado.isBlank()) {
            inc.setEstado(nuevoEstado);
        }
        if (respuestaSoporte != null) {
            inc.setRespuestaSoporte(respuestaSoporte);
        }
        return repository.save(inc);
    }

    public void eliminarIncidencia(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Incidencia no encontrada");
        }
        repository.deleteById(id);
    }
}