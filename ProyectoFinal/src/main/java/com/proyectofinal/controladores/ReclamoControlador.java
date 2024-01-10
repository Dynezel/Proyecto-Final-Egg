package com.proyectofinal.controladores;

import com.proyectofinal.entidades.Reclamo;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.servicios.InmuebleServicio;
import com.proyectofinal.servicios.ReclamoServicio;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reclamo")
public class ReclamoControlador {

    @Autowired
    private ReclamoServicio reclamoServicio;
    @Autowired
    private InmuebleServicio inmuebleServicio;
//    @Autowired
//    private JavaMailSender emailSender;
//    

    @GetMapping("/reclamar/{cuentaTributaria}")
    public String reclamar(
            @PathVariable("cuentaTributaria") String cuentaTributaria,
            ModelMap modelo,
            HttpSession session) {
        modelo.put("inmueble", inmuebleServicio.getOne(cuentaTributaria));
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");//enviar la session del usuario logueado
        modelo.put("usuario", usuario);

        return "reclamo_form.html";
    }

    // @PreAuthorize("hasRole('ROLE_CLIENT')") // SOLO CLIENTE PUEDE RECLAMAR
    @PostMapping("/reclamar/{cuentaTributaria}")
    public String reclamo(
            @PathVariable("cuentaTributaria") String cuentaTributaria,
            @RequestParam("idUsuario") String idUsuario,
            @RequestParam("reclamoDescrip") String reclamoDescrip,
            @RequestParam("fechaReclamo") String fechaReclamo,
            ModelMap modelo,
            HttpSession session) {

        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = formato.parse(fechaReclamo);// transformar date en string

            reclamoServicio.crearReclamo(cuentaTributaria, idUsuario, reclamoDescrip, fecha);

            modelo.put("exito", "Reclamo registrado correctamente");
            return "redirect:/";

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());

            return "redirect:/";
        }

    }

    @GetMapping("/lista")
    public String listarReclamos(ModelMap modelo, HttpSession session) throws Exception {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
//        if(usuario.getRol().toString().equals("ENTE")){
//            
//        }
        List<Reclamo> reclamos = reclamoServicio.listarReclamos();
        modelo.addAttribute("reclamos", reclamos);

        return "reclamo_lista.html";
    }

    @GetMapping("/lista/{cuentaTributaria}")
    public String listarReclamo(@PathVariable String cuentaTributaria, ModelMap modelo) throws Exception {
        List<Reclamo> reclamos = reclamoServicio.listarReclamo(cuentaTributaria);

        modelo.addAttribute("reclamos", reclamos);

        return "reclamo_lista.html";
    }

    @GetMapping("/respuesta/{idReclamo}")
    public String contestacionReclamo(@PathVariable String idReclamo, ModelMap modelo) throws Exception {
        modelo.put("reclamo", reclamoServicio.getOne(idReclamo));
        Reclamo reclamo = reclamoServicio.getOne(idReclamo);
        String cuentaTributaria = reclamo.getInmueble().getCuentaTributaria();
        modelo.put("cuentaTributaria", cuentaTributaria);

        return "reclamo_respuesta.html";
    }

    @PostMapping("/respuesta/{idReclamo}")
    public String respuestaReclamo(@PathVariable String idReclamo,
            @RequestParam String respuesta,
            @RequestParam String cuentaTributaria,
            ModelMap modelo) throws Exception {
        try {
            reclamoServicio.contestarReclamo(idReclamo, respuesta);

            return "redirect:/reclamo/lista/" + cuentaTributaria;

        } catch (Exception ex) {
            return "reclamo_respuesta.html";

        }

    }
}
