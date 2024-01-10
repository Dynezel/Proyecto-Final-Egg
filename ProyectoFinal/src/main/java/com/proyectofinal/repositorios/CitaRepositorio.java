package com.proyectofinal.repositorios;

import com.proyectofinal.entidades.Cita;
import com.proyectofinal.entidades.RangoHorario;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CitaRepositorio extends CrudRepository<Cita, String> {

    @Query("SELECT r FROM RangoHorario r WHERE r.inmueble.cuentaTributaria = :cuentaTributaria")
    List<RangoHorario> findByCuentaTributaria(@Param("cuentaTributaria") String cuentaTributaria);

    @Query("SELECT c FROM Cita c WHERE c.horario = :rangoHorario")
    List<Cita> findByHorario(@Param("rangoHorario") RangoHorario rangoHorario);
}
