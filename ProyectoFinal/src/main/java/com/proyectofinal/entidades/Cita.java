package com.proyectofinal.entidades;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
public class Cita implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @ManyToOne
    private Usuario ente;
    @ManyToOne
    private Usuario cliente;

    @ManyToOne
    private RangoHorario horario;

    private String Nota;

}
