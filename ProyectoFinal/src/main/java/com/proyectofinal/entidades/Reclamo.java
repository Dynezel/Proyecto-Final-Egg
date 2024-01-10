package com.proyectofinal.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
public class Reclamo implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String idReclamo;
    private String reclamoDescrip;
    private Date fechaReclamo;
    private String respuesta;
    private String estado;

    @ManyToOne
    //@JoinColumn(name = "usuario_cliente_id")
    private Usuario usuario;

    @ManyToOne
    private Inmueble inmueble;

}
