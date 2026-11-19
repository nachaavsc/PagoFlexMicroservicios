package com.PagoFlex.Incidencias.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(nullable = false)
    private Long usuarioId;

    private Long compromisoId;

    @NotBlank(message = "El título de la incidencia es obligatorio")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    private String estado = "ABIERTA";

    private String respuestaSoporte;

    private LocalDateTime fechaCreacion = LocalDateTime.now();
}