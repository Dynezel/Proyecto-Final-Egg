package com.proyectofinal.controladores;

import com.proyectofinal.entidades.FechaHoraContainer;
import com.proyectofinal.entidades.Inmueble;
import com.proyectofinal.entidades.RangoHorario;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.servicios.CitaServicio;
import com.proyectofinal.servicios.InmuebleServicio;
import com.proyectofinal.servicios.RangoHorarioServicio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cita")
public class CitaControlador {

    @Autowired
    private CitaServicio citaServicio;

    @Autowired
    private RangoHorarioServicio rangoHorarioServicio;

    @Autowired
    private InmuebleServicio inmuebleServicio;

    @GetMapping("/registrar/{cuentaTributaria}")
    public String registrarCita(@PathVariable("cuentaTributaria") String cuentaTributaria,
                                ModelMap model, HttpSession session) throws Exception {
        Inmueble inmueble = inmuebleServicio.obtenerInmueblePorCuentaTributaria(cuentaTributaria);
        List<RangoHorario> rangoHorario = rangoHorarioServicio.obtenerRangoHorarioPorCuentaTributaria(cuentaTributaria);
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        model.put("inmueble", inmueble);

        // Cambia la clave de r.getId() a r.getFecha()
        Map<String, List<LocalTime>> horariosDisponiblesMap = new HashMap<>();
        for (RangoHorario r : rangoHorario) {
            List<LocalTime> horariosDisponibles = citaServicio.obtenerHorariosDisponibles(r.getHoraInicio(), r.getHoraFin());
            String fechaFormateada = r.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            horariosDisponiblesMap.put(fechaFormateada, horariosDisponibles);
        }

        model.put("horariosDisponiblesMap", horariosDisponiblesMap);
        model.put("rangoHorario", rangoHorario);
        model.put("usuario", usuario);
        return "cita_form.html";
    }

    @PostMapping("/registrar/{cuentaTributaria}")
    public String registrarCita(@RequestParam String idEnte, @RequestParam String idCliente,
                                @RequestParam Long idHorario, @RequestParam(required = false) String nota,
                                ModelMap modelo) {
        try {
            System.out.println("Id del ente: " + idEnte);
            System.out.println("Id del CLiente: " + idCliente);
            System.out.println("Id del Horario: " + idHorario);
            citaServicio.crearCita(idEnte, idCliente, idHorario, nota);
            modelo.put("exito", "la cita fue cargada correctamente");
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("El id del horario es: " + idHorario);
            return "cita_form";
        }

        return "redirect:/";
    }

    @GetMapping("/horarios-disponibles/{cuentaTributaria}")
    @ResponseBody
    public ResponseEntity<Map<String, List<LocalTime>>> obtenerHorariosDisponibles(@PathVariable String cuentaTributaria) {
        try {
            List<RangoHorario> rangoHorarioList = inmuebleServicio.obtenerRangosHorariosPorCuentaTributaria(cuentaTributaria);

            if (rangoHorarioList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Formatea las fechas como cadenas en el formato "yyyy-MM-dd"
            Map<String, List<LocalTime>> horariosPorFecha = rangoHorarioList.stream()
                    .collect(Collectors.groupingBy(
                            rango -> rango.getFecha().toString(),
                            Collectors.flatMapping(
                                    rango -> {
                                        List<LocalTime> horas = new ArrayList<>();
                                        LocalTime horaInicio = rango.getHoraInicio();
                                        LocalTime horaFin = rango.getHoraFin();

                                        while (horaInicio.isBefore(horaFin) || horaInicio.equals(horaFin)) {
                                            horas.add(horaInicio);
                                            horaInicio = horaInicio.plusMinutes(30);
                                        }

                                        return horas.stream();
                                    },
                                    Collectors.toList()
                            )
                    ));

            return ResponseEntity.ok(horariosPorFecha);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
