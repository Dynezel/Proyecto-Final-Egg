package com.proyectofinal.entidades;

import com.proyectofinal.enumeraciones.Rol;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@Entity
public class Usuario implements Serializable {

    @Id
    private String idCodigoTributario;
    private String nombre;
    private String apellido;
    private String DNI;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String sexo;
    private String email;
    private String celular;
    private String tipoPersona;
    private String contrasenia;
    private String resetPwToken;
    private Boolean alta;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "imagen_id")
    private Imagen fotoPerfil;

    @Enumerated
    private Rol rol;

    @OneToMany
    private List<Inmueble> propiedades;

}
