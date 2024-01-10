package com.proyectofinal.entidades;

import java.io.Serializable;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Imagen implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;

    private String mime;

    private String rutaImagen;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public byte[] contenido;

    @ManyToOne // Agrega esta anotación para definir la relación con Inmueble
    @JoinColumn(name = "inmueble_id") // Nombre de la columna en la tabla Imagen
    private Inmueble inmueble; // Nombre de la propiedad en Imagen que se refiere a Inmueble

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((id == null) ? 0 : id.hashCode());

        // Do NOT include the inmueble field in the hashCode calculation
        // result = prime * result + ((inmueble == null) ? 0 : inmueble.hashCode());
        return result;
    }

    // Make sure that equals() is also safely implemented
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Imagen other = (Imagen) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }

        // Similarly, do not compare the inmueble field here
    }

    // In your Imagen entity
    @Override
    public String toString() {
        return "Imagen{"
                + "id='" + id + '\''
                + ", mime='" + mime + '\''
                + ", nombre='" + nombre + '\''
                + // Do not include 'inmueble' in toString as it will cause recursion
                '}';
    }

}
