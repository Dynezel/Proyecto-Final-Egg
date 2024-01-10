package com.proyectofinal.controladores;

import com.proyectofinal.entidades.RangoHorario;
import com.proyectofinal.servicios.InmuebleServicio;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/horario")
public class RangoHorarioControlador {

    @Autowired
    private InmuebleServicio inmuebleServicio;

    @GetMapping("/horarios-disponibles/{cuentaTributaria}")
    public ResponseEntity<Map<LocalDate, List<LocalTime>>> obtenerHorariosDisponibles(@PathVariable String cuentaTributaria) {
        try {
            List<RangoHorario> rangoHorarioList = inmuebleServicio.obtenerRangosHorariosPorCuentaTributaria(cuentaTributaria);

            if (rangoHorarioList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<LocalDate, List<LocalTime>> horariosPorFecha = rangoHorarioList.stream()
                    .collect(Collectors.groupingBy(
                            RangoHorario::getFecha,
                            Collectors.mapping(
                                    RangoHorario::getHoraInicio,
                                    Collectors.toList())
                    ));

            return ResponseEntity.ok(horariosPorFecha);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
