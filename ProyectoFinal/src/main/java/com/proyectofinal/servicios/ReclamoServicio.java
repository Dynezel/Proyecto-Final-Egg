package com.proyectofinal.servicios;

import com.proyectofinal.entidades.Inmueble;
import com.proyectofinal.entidades.Reclamo;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.repositorios.InmuebleRepositorio;
import com.proyectofinal.repositorios.ReclamoRepositorio;
import com.proyectofinal.repositorios.UsuarioRepositorio;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReclamoServicio {

    @Autowired
    private ReclamoRepositorio reclamoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private InmuebleRepositorio inmuebleRepositorio;

    public void crearReclamo(String cuentaTributaria, String idUsuario, String reclamoDescrip, Date fechaReclamo) {

        Optional<Usuario> respuestaUsuario = usuarioRepositorio.findById(idUsuario);
        Optional<Inmueble> respuestaInmueble = inmuebleRepositorio.findById(cuentaTributaria);

        Usuario usuario = new Usuario();
        Inmueble inmueble = new Inmueble();

        if (respuestaUsuario.isPresent()) {
            usuario = respuestaUsuario.get();
        }
        if (respuestaInmueble.isPresent()) {
            inmueble = respuestaInmueble.get();
        }
        Reclamo reclamo = new Reclamo();

        reclamo.setUsuario(usuario);
        reclamo.setInmueble(inmueble);
        reclamo.setReclamoDescrip(reclamoDescrip);
        reclamo.setFechaReclamo(fechaReclamo);
        reclamo.setEstado("Pendiente");
        reclamoRepositorio.save(reclamo);

    }

    public Reclamo getOne(String idReclamo) {
        return reclamoRepositorio.getOne(idReclamo);
    }

    public void contestarReclamo(String idReclamo, String respuesta) {

        Optional<Reclamo> respuestaReclamo = reclamoRepositorio.findById(idReclamo);

        Reclamo reclamo = new Reclamo();

        if (respuestaReclamo.isPresent()) {
            reclamo = respuestaReclamo.get();
        }
        reclamo.setRespuesta(respuesta);
        reclamo.setEstado("Resuelto");
        reclamoRepositorio.save(reclamo);

    }

    @Transactional(readOnly = true)
    public List<Reclamo> listarReclamo(String idcuentaTributaria) {

        return reclamoRepositorio.buscarPorIdCuentaTributaria(idcuentaTributaria);

    }

    @Transactional(readOnly = true)
    public List<Reclamo> listarReclamos() {

        return reclamoRepositorio.findAll();

    }

    public List<Reclamo> listarReclamosOrdenados(String orden) {
        Sort.Order order;
        if ("fechaAsc".equalsIgnoreCase(orden)) {
            order = Sort.Order.asc("fechaReclamo");
        } else if ("fechaDesc".equalsIgnoreCase(orden)) {
            order = Sort.Order.desc("fechaReclamo");
        } else if ("estadoAsc".equalsIgnoreCase(orden)) {
            order = Sort.Order.asc("estado");
        } else if ("estadoDesc".equalsIgnoreCase(orden)) {
            order = Sort.Order.desc("estado");
        } else {
            // Por defecto, ordenar por fecha ascendente
            order = Sort.Order.asc("fechaReclamo");
        }

        Sort sort = Sort.by(order);
        return reclamoRepositorio.findAll(sort);
    }

}
