package com.proyectofinal.entidades;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class RangoHorario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private String diaSemana; // Día de la semana (por ejemplo, "Lunes", "Martes", etc.)

    @Column(name = "hora_inicio")
    private LocalTime horaInicio; // Hora de inicio del rango horario

    @Column(name = "hora_fin")
    private LocalTime horaFin; // Hora de fin del rango horario

    @ManyToOne
    @JoinColumn(name = "cuenta_tributaria", referencedColumnName = "cuentaTributaria")
    private Inmueble inmueble;

    // Otras propiedades y getters/setters
}
