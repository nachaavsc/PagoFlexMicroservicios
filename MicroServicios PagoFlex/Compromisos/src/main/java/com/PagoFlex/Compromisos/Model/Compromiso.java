package com.PagoFlex.Compromisos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "compromisos")
public class Compromiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(nullable = false)
    private Long usuarioId;

    @NotBlank(message = "El título del compromiso es obligatorio")
    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Column(nullable = false)
    private Double montoTotal;

    private Double montoPagado = 0.0;

    private Integer cuotasTotales = 1;

    private Integer cuotasPagadas = 0;

    private LocalDate fechaVencimiento;

    private String estado = "PENDIENTE";
}