package com.proyectofinal.repositorios;

import com.proyectofinal.entidades.Oferta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfertaRepositorio extends JpaRepository<Oferta, String> {

    // buscar oferta por inmueble
    @Query("SELECT o FROM Oferta o WHERE o.inmueble.cuentaTributaria =  :idcuentaTributaria")
    public List<Oferta> buscarPorIdCuentaTributaria(@Param("idcuentaTributaria") String idcuentaTributaria);

    @Query("SELECT o FROM Oferta o WHERE o.usuario.idCodigoTributario =  :idCodigoTributario")
    public List<Oferta> buscarPorIdCodigoTributario(@Param("idCodigoTributario") String idCodigoTributario);

}
