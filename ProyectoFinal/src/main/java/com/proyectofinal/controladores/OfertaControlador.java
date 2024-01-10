package com.proyectofinal.controladores;

import com.proyectofinal.entidades.Inmueble;
import com.proyectofinal.entidades.Oferta;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.servicios.InmuebleServicio;
import com.proyectofinal.servicios.OfertaServicio;
import com.proyectofinal.servicios.UsuarioDetalles;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oferta")
// ver que tiene que estar asociada a inmueble.
public class OfertaControlador {

    @Autowired
    private OfertaServicio ofertaServicio;

    @Autowired
    private InmuebleServicio inmuebleServicio;  //ver si es neceario

    //LOGUEAR ANTES DE USAR //
    // el role client es el unico autorizado a ofertar y entra por la vista inmueble
    // se envia oferta relacionando cuenta tributaria y usuario
    @GetMapping("/enviar/{cuentaTributaria}")
    public String enviarOferta(
            @PathVariable("cuentaTributaria") String cuentaTributaria,
            ModelMap modelo,
            HttpSession session) {
        modelo.put("inmueble", inmuebleServicio.getOne(cuentaTributaria));
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");//enviar la session del usuario logueado
        modelo.put("usuario", usuario);
        return "oferta_form.html";
    }
    //LOGUEAR ANTES DE USAR //

    // @RequestMapping(value = "/inmueble/aceptar/{cuentaTributaria}", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping("/aceptar/{cuentaTributaria}")
    public String enviarOferta(
            @PathVariable("cuentaTributaria") String cuentaTributaria,
            @RequestParam("idCodigoTributario") String idCodigoTributario,
            @RequestParam("valorOferta") Integer valorOferta,
            @RequestParam("fechaOferta") String fechaOferta,
            @RequestParam("moneda") String moneda,
            ModelMap modelo,
            HttpSession session) { // VER SI FUNCIONA

        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = formato.parse(fechaOferta);// transformar date en string

            ofertaServicio.realizarOferta(cuentaTributaria, idCodigoTributario, moneda, valorOferta, fecha);

            modelo.put("exito", "La oferta fue aceptada por el Ente");
            return "redirect:/"; //se puede usar el mismo formulario de enviar oferta?

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());

            return "redirect:/";
        }

    }

//   
//     buscar oferta por la cuenta tributaria del inmueble.
    @GetMapping("/recibidas/{cuentaTributaria}")
    public String listarOfertasPorInmueble(@PathVariable String cuentaTributaria,
            ModelMap modelo) {
        List<Oferta> ofertas = ofertaServicio.mostrarOfertasPorInmueble(cuentaTributaria);
        modelo.addAttribute("ofertas", ofertas);

        return "oferta_lista_por_inmueble.html";
    }

    @GetMapping("/realizadas/{idCodigoTributario}")
    public String listarOfertasPorUsuario(@PathVariable String idCodigoTributario,
            ModelMap modelo,
            Principal principal) {
        String idCodigoTributarioAutenticado = obtenerIdCodigoTributarioDelPrincipal(principal);

        if (idCodigoTributarioAutenticado != null && idCodigoTributarioAutenticado.equals(idCodigoTributario)) {
            List<Oferta> ofertas = ofertaServicio.mostrarOfertasPorUsuario(idCodigoTributario);
            modelo.addAttribute("ofertas", ofertas);
            modelo.addAttribute("idCodigoTributarioAutenticado", idCodigoTributarioAutenticado);
        } else {
            return "redirect:/error";
        }

        return "oferta_lista.html";
    }

    private String obtenerIdCodigoTributarioDelPrincipal(Principal principal) {
        if (principal != null && principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            Object principalObj = authentication.getPrincipal();

            if (principalObj instanceof UsuarioDetalles) {
                return ((UsuarioDetalles) principalObj).getIdCodigoTributario();
            }
        }
        return null;
    }

    @GetMapping("/respuesta/{idOferta}")
    public String contestacionOferta(@PathVariable String idOferta,
            @RequestParam String respuesta,
            ModelMap modelo) throws Exception {
        //  modelo.put("oferta", ofertaServicio.getOne(cuentaTributaria));
        return "oferta_lista.html";
    }

    @PostMapping("/respuesta/{idOferta}")
    public String respuestaOferta(@PathVariable String idOferta,
            @RequestParam String respuesta,
            ModelMap modelo) throws Exception {
        Oferta oferta = ofertaServicio.getOne(idOferta);
        Inmueble inmueble = oferta.getInmueble();
        try {
            ofertaServicio.resolverOferta(idOferta, respuesta);
            //  contestacionOferta(cuentaTributaria, respuesta);

        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
        } finally {
            return "redirect:/oferta/lista/" + inmueble.getCuentaTributaria();
        }
    }
}
