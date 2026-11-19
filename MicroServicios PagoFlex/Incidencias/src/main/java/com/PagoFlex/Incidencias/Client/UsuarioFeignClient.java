package com.PagoFlex.Incidencias.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "ms-usuarios")
public interface UsuarioFeignClient {
    @GetMapping("/api/usuarios/{id}")
    Map<String, Object> obtenerUsuarioPorId(@PathVariable("id") Long id);
}