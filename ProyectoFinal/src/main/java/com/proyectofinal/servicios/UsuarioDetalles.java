package com.proyectofinal.servicios;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioDetalles extends User {

    private String idCodigoTributario;

    public UsuarioDetalles(String username, String password, Collection<? extends GrantedAuthority> authorities, String idCodigoTributario) {
        super(username, password, authorities);
        this.idCodigoTributario = idCodigoTributario;
    }

    // Métodos adicionales, como getters y setters
    public String getIdCodigoTributario() {
        return idCodigoTributario;
    }

    public void setIdCodigoTributario(String idCodigoTributario) {
        this.idCodigoTributario = idCodigoTributario;
    }
}
