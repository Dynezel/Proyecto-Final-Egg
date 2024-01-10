package com.proyectofinal.repositorios;

import com.proyectofinal.entidades.Reclamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamoRepositorio extends JpaRepository<Reclamo, String> {

    @Query("SELECT r FROM Reclamo r WHERE r.idReclamo =  :idReclamo")
    public Reclamo buscarPorIdReclamo(@Param("idReclamo") String idReclamo);

    @Query("SELECT r FROM Reclamo r WHERE r.inmueble.cuentaTributaria =  :idcuentaTributaria")
    public List<Reclamo> buscarPorIdCuentaTributaria(@Param("idcuentaTributaria") String idcuentaTributaria);

}
