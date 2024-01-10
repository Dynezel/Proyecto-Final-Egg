package com.proyectofinal.entidades;

import lombok.Data;

import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "rangosHorarios")
public class Inmueble implements Serializable {

    @Id
    private String cuentaTributaria;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String transaccion; // input compra, venta, alquiler anual, alquiler temporario
    private String tituloAnuncio;
    private String descripcionAnuncio;
    private String moneda;
    private Integer precio;
    // Caracteristicas
    private int cantidadHabitaciones;
    private int banios;
    private int cantidadAmbientes;
    private int  superficieTotal;  //Ambos en metros cuadrados
    private int superficieCubierta; //
    private Boolean alta;
    private String tipoInmueble;
    private String estado;

    // Nueva relación para la imagen principal
    @OneToOne
    @JoinColumn(name = "imagen_principal_id")
    private Imagen imagenPrincipal;

    // Relación para la galería de imágenes secundarias
    @OneToMany(mappedBy = "inmueble")
    private List<Imagen> imagenesSecundarias;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_administrador")
    private Usuario usuarioAdministrador;
    @OneToMany(mappedBy = "inmueble")
    private List<RangoHorario> rangosHorarios;

    // In your Inmueble entity
    @Override
    public String toString() {
        return "Inmueble{"
                + "cuentaTributaria='" + cuentaTributaria + '\''
                + ", tituloAnuncio='" + tituloAnuncio + '\''
                + // Other fields
                // Do not include 'imagenesSecundarias' or 'imagenPrincipal' if they have back-reference to Inmueble
                '}';
    }
}
