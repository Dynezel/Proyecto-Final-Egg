package com.proyectofinal.repositorios;

import com.proyectofinal.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.email =  :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.resetPwToken =  :resetPwToken")
    public Usuario buscarPorResetPwToken(@Param("resetPwToken") String resetPwToken);

    @Query("SELECT u FROM Usuario u WHERE u.idCodigoTributario =  :idCodigoTributario")
    public Usuario buscarPorIdCodigoTributario(@Param("idCodigoTributario") String idCodigoTributario);

    boolean existsByDNI(String dni);

//
//    @Query("SELECT u FROM Usuario u WHERE u.resetPwToken =  :resetPwToken")
//    public Usuario buscarPorResetPwToken(@Param("resetPwToken") String resetPwToken);
}
