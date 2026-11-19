package com.PagoFlex.Usuarios.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(unique = true, nullable = false)
    private String correo;

    private String telefono;

    private String rutaFoto;

    private String rol = "CLIENTE";
}