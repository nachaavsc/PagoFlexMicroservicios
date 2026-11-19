package com.PagoFlex.Autenticacion.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-usuarios")
public interface UsuarioFeignClient {
    @PostMapping("/api/usuarios")
    Map<String, Object> crearPerfilUsuario(@RequestBody Map<String, Object> usuarioRequest);
}
