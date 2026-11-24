package com.PagoFlex.Compromisos.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del compromiso es obligatorio")
    @Column(nullable = false)
    private Long compromisoId;

    @NotNull(message = "El monto del pago es obligatorio")
    @Positive(message = "El monto del pago debe ser mayor a cero")
    @Column(nullable = false)
    private Double monto;

    private String metodoPago = "TRANSFERENCIA";

    private LocalDateTime fechaPago = LocalDateTime.now();

    private String comprobante;
}