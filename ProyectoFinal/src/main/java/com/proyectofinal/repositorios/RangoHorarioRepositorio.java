package com.proyectofinal.repositorios;

import com.proyectofinal.entidades.RangoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RangoHorarioRepositorio extends JpaRepository<RangoHorario, Long> {

    List<RangoHorario> findById(String idHorario);

    @Query("SELECT r FROM RangoHorario r WHERE r.inmueble.cuentaTributaria = :cuentaTributaria")
    List<RangoHorario> findByCuentaTributaria(@Param("cuentaTributaria") String cuentaTributaria);

    List<RangoHorario> findByFecha(@Param("fecha")LocalDate fecha);
}
