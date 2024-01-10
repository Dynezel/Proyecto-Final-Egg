package com.proyectofinal.controladores;

import com.proyectofinal.entidades.Imagen;
import com.proyectofinal.entidades.Inmueble;
import com.proyectofinal.entidades.RangoHorario;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.servicios.ImagenServicio;
import com.proyectofinal.servicios.InmuebleServicio;
import com.proyectofinal.servicios.RangoHorarioServicio;
import com.proyectofinal.servicios.UsuarioServicio;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/inmueble")
public class InmuebleControlador {

    private final InmuebleServicio inmuebleServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    public InmuebleControlador(@Lazy InmuebleServicio inmuebleServicio) {
        this.inmuebleServicio = inmuebleServicio;
    }

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    RangoHorarioServicio rangoHorarioServicio;

    @GetMapping("/registrar")
    public String registrarInmueble() {
        return "inmueble_form.html";
    }

    @PostMapping("/registrar")
    public String registrarInmueble(ModelMap modelo,
            @RequestParam("archivoPrincipal") MultipartFile archivoPrincipal,
            @RequestParam("archivosSecundarios") MultipartFile[] archivosSecundarios,
            @RequestParam("cuentaTributaria") String cuentaTributaria,
            @RequestParam("direccion") String direccion,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("provincia") String provincia,
            @RequestParam("transaccion") String transaccion,
            @RequestParam("tipoInmueble") String tipoInmueble,
            @RequestParam("tituloAnuncio") String tituloAnuncio,
            @RequestParam("descripcionAnuncio") String descripcionAnuncio,
            @RequestParam("moneda") String moneda,
            @RequestParam("precio") Integer precio,
            @RequestParam("cantidadHabitaciones") Integer cantidadHabitaciones,
            @RequestParam("banios") Integer banios,
            @RequestParam("cantidadAmbientes") Integer cantidadAmbientes,
            @RequestParam("superficieTotal") int superficieTotal,
            @RequestParam("superficieCubierta") int superficieCubierta,
            @RequestParam("diaSemana") List<String> diaSemanaList,
            @RequestParam("horaInicio") List<String> horaInicioList,
            @RequestParam("horaFin") List<String> horaFinList,
            @RequestParam("fecha") String fecha, // Agrega el parámetro fecha
            HttpSession session) {

        // Obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        try {
            // Parsear la lista de fechas
            List<LocalDate> fechas = Arrays.stream(fecha.split(","))
                    .map(LocalDate::parse)
                    .collect(Collectors.toList());
            // Guardar el inmueble y capturar la instancia guardada
            Inmueble inmuebleGuardado = inmuebleServicio.registrarInmueble(
                    archivoPrincipal, archivosSecundarios, cuentaTributaria, direccion, ciudad, provincia,
                    transaccion, tipoInmueble, tituloAnuncio, descripcionAnuncio, moneda,
                    precio, cantidadHabitaciones, banios, cantidadAmbientes,
                    superficieTotal, superficieCubierta, usuario, fechas, diaSemanaList, horaInicioList, horaFinList);

            modelo.put("exito", "El inmueble fue cargado correctamente!");
        } catch (Exception ex) {
            modelo.put("error", ex.getMessage());
            // Log para el mensaje de la excepción
            System.out.println(ex.getMessage());
            return "inmueble_form.html";
        }
        return "redirect:/";
    }

    @GetMapping("/lista")
    public String listarInmueblesInmueble(ModelMap modelo, @RequestParam(name = "orden", required = false) String orden) throws Exception {
        List<Inmueble> inmuebles = inmuebleServicio.listarTodosLosInmuebles();
        for (Inmueble inmueble : inmuebles) {
            List<RangoHorario> rangosHorarios = rangoHorarioServicio.obtenerTodosLosRangosHorarios();
            inmueble.setRangosHorarios(rangosHorarios);
        }

        switch (orden) {
            case "":
                inmuebles = inmuebleServicio.listarTodosLosInmuebles();
                break;
            case "precioAsc":
                inmuebles = inmuebleServicio.listarInmueblesPorPrecioAsc();
                break;
            case "precioDesc":
                inmuebles = inmuebleServicio.listarInmueblesPorPrecioDesc();
                break;
            case "transaccionAsc":
                inmuebles = inmuebleServicio.listarInmueblesPorTransaccionAsc();
                break;
            case "transaccionDesc":
                inmuebles = inmuebleServicio.listarInmueblesPorTransaccionDesc();
                break;
            case "estadoAsc":
                inmuebles = inmuebleServicio.listarInmueblesPorEstadoAsc();
                break;
            case "estadoDesc":
                inmuebles = inmuebleServicio.listarInmueblesPorEstadoDesc();
                break;
            case "altaAsc":
                inmuebles = inmuebleServicio.listarInmueblesPorAltaAsc();
                break;
            case "altaDesc":
                inmuebles = inmuebleServicio.listarInmueblesPorAltaDesc();
                break;
            default:
                // Orden predeterminado (puedes cambiar esto según tus necesidades)
                inmuebles = inmuebleServicio.listarTodosLosInmuebles();
                break;
        }

        modelo.addAttribute("inmuebles", inmuebles);

        return "inmueble_lista";
    }



    @GetMapping("/modificar/{cuentaTributaria}")
    public String editarInmueble(@PathVariable String cuentaTributaria, ModelMap model) throws Exception {
        Inmueble inmueble = inmuebleServicio.obtenerInmueblePorCuentaTributaria(cuentaTributaria);
        List<RangoHorario> rangoHorario = rangoHorarioServicio.obtenerRangoHorarioPorCuentaTributaria(cuentaTributaria);
        model.put("rangoHorario", rangoHorario);

        model.addAttribute("inmueble", inmueble);
        model.addAttribute("cuentaTributaria", cuentaTributaria);
        return "inmueble_modificar";
    }

    @PostMapping("/modificar/{cuentaTributaria}")
    public String actualizarInmueble(@PathVariable("cuentaTributaria") String cuentaTributaria,
            @RequestParam("imagenPrincipal") MultipartFile archivoPrincipal,
            @RequestParam("archivos") MultipartFile[] archivosSecundarios,
            @RequestParam("tituloAnuncio") String tituloAnuncio,
            @RequestParam("descripcionAnuncio") String descripcionAnuncio,
            @RequestParam("estado") String estado,
            @RequestParam("diaSemana") List<String> diaSemanaList,
            @RequestParam("horaInicio") List<String> horaInicioList,
            @RequestParam("horaFin") List<String> horaFinList,
            ModelMap model) {
        try {
            // Modificar inmueble
            inmuebleServicio.modificarInmueble(cuentaTributaria, archivoPrincipal, archivosSecundarios, tituloAnuncio, descripcionAnuncio, estado);

            rangoHorarioServicio.actualizarRangoHorario(diaSemanaList, horaInicioList, horaFinList);
            model.put("exito", "Los cambios fueron guardados correctamente!");
            return "redirect:/"; // Redirige a la página principal o la página de éxito, según sea necesario
        } catch (Exception ex) {
            Inmueble inmueble = inmuebleServicio.obtenerInmueblePorCuentaTributaria(cuentaTributaria);
            model.put("inmueble", inmueble);
            model.put("error", ex.getMessage());
            return "inmueble_modificar";// Permanece en la página de edición y muestra el mensaje de error
        }
    }

    @GetMapping("/busqueda")
    public String buscarInmuebles(
            @RequestParam(name = "ubicacion", required = false) String ubicacion,
            @RequestParam(name = "transaccion", required = false) String transaccion,
            @RequestParam(name = "tipoInmueble", required = false) String tipoInmueble,
            @RequestParam(name = "ciudad", required = false) String ciudad,
            @RequestParam(name = "provincia", required = false) String provincia,
            @RequestParam(name = "moneda", required = false) String moneda,
            @RequestParam(name = "precioMinimo", required = false) Integer precioMinimo,
            @RequestParam(name = "precioMaximo", required = false) Integer precioMaximo,
            @RequestParam(name = "habitacionesMinimas", required = false) Integer habitacionesMinimas,
            @RequestParam(name = "habitacionesMaximas", required = false) Integer habitacionesMaximas,
            @RequestParam(name = "baniosMinimos", required = false) Integer baniosMinimos,
            @RequestParam(name = "baniosMaximos", required = false) Integer baniosMaximos,
            @RequestParam(name = "superficieCubiertaMinima", required = false) Integer superficieCubiertaMinima,
            @RequestParam(name = "superficieCubiertaMaxima", required = false) Integer superficieCubiertaMaxima,
            @RequestParam(name = "superficieTotalMinima", required = false) Integer superficieTotalMinima,
            @RequestParam(name = "superficieTotalMaxima", required = false) Integer superficieTotalMaxima,
            @RequestParam(name = "archivo", required = false) MultipartFile archivo,
            Model model
    ) {
        // Llama al servicio con los parámetros adecuados, incluyendo tipoInmueble como String.
        try {
            // Llama al servicio con los parámetros adecuados
            List<Inmueble> inmuebles = inmuebleServicio.buscarInmueblesPorFiltros(ubicacion, transaccion, tipoInmueble,
                    ciudad, provincia, moneda, precioMaximo, precioMinimo, habitacionesMinimas,
                    habitacionesMaximas, baniosMinimos, baniosMaximos, superficieCubiertaMinima, superficieCubiertaMaxima, superficieTotalMinima, superficieTotalMaxima);
            // Agrega los resultados al modelo.
            model.addAttribute("inmuebles", inmuebles);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect/";
        }

        return "inmueble_busqueda.html";
    }

    @GetMapping("/detalle/{cuentaTributaria}")
    public String detalleInmueble(@PathVariable String cuentaTributaria, Model model, Principal principal) throws Exception {
        Inmueble inmueble = inmuebleServicio.obtenerInmueblePorCuentaTributaria(cuentaTributaria);
        List<RangoHorario> rangoHorario = rangoHorarioServicio.obtenerRangoHorarioPorCuentaTributaria(cuentaTributaria);

        Usuario usuarioActual = null;
        boolean esPropietario = false;

        if (principal != null) {
            usuarioActual = usuarioServicio.obtenerUsuarioPorUsername(principal.getName());
            esPropietario = inmueble != null && usuarioActual.getPropiedades().stream()
                    .anyMatch(propiedad -> propiedad.getCuentaTributaria().equals(cuentaTributaria));
        }
        if (inmueble != null) {
            List<Imagen> imagenes = inmueble.getImagenesSecundarias();
            List<Map<String, String>> imagenesInfo = new ArrayList<>();

            for (Imagen imagen : imagenes) {
                Map<String, String> imageData = new HashMap<>();
                byte[] imagenContenido = imagen.getContenido();
                String imagenBase64 = Base64.getEncoder().encodeToString(imagenContenido);
                imageData.put("base64", imagenBase64);
                imageData.put("mime", imagen.getMime());
                imagenesInfo.add(imageData);
            }

            model.addAttribute("imagenesInfo", imagenesInfo);
            model.addAttribute("inmueble", inmueble);
            model.addAttribute("rangoHorario", inmueble.getRangosHorarios());
            model.addAttribute("esPropietario", esPropietario);
            return "inmueble_detalle";
        } else {
            // Manejar el caso en el que no se encuentra el inmueble
            return "error"; // Puedes crear una vista específica para errores.
        }
    }

    @GetMapping("/eliminar/{cuentaTributaria}")
    public String eliminarInmueble(@PathVariable String cuentaTributaria) throws Exception {
        inmuebleServicio.eliminarInmueblePorCuentaTributaria(cuentaTributaria);
        rangoHorarioServicio.eliminarInmueblePorCuentaTributaria(cuentaTributaria);
        return "redirect:/";
    }

    @GetMapping("/dar-baja/{cuentaTributaria}")
    public String darBajaInmueble(@PathVariable("cuentaTributaria") String cuentaTributaria,
            ModelMap modelo) throws Exception {
        inmuebleServicio.darBajaInmueble(cuentaTributaria);

        return "index.html";
    }

    @GetMapping("/dar-alta/{cuentaTributaria}")
    public String darAltaInmueble(@PathVariable("cuentaTributaria") String cuentaTributaria,
            ModelMap modelo) throws Exception {
        inmuebleServicio.darAltaInmueble(cuentaTributaria);

        return "index.html";
    }
}
