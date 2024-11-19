package apifestivos.apifestivos.presentacion;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apifestivos.apifestivos.core.interfaces.servicios.IFestivoServicio;
import apifestivos.apifestivos.dominio.DTOs.FestivoDTO;

/**
 * Controlador REST para manejar las solicitudes relacionadas con los festivos.
 */

@RestController
@RequestMapping("/festivos")
public class FestivoControlador {

    private final IFestivoServicio festivoServicio;
    private static final Logger logger = LoggerFactory.getLogger(FestivoControlador.class);


    /**
     * Inyección de dependencias a través del constructor.
     * 
     * @param festivoServicio Servicio de festivos para manejar la lógica de
     *                        negocio.
     */
    public FestivoControlador(IFestivoServicio festivoServicio) {
        this.festivoServicio = festivoServicio;
    }

    /**
     * Endpoint para verificar si una fecha es festiva.
     * 
     * @param anio Año de la fecha a verificar.
     * @param mes  Mes de la fecha a verificar.
     * @param dia  Día de la fecha a verificar.
     * @return Respuesta HTTP con el resultado de la verificación.
     */
    @GetMapping("/verificar/{anio}/{mes}/{dia}")
    public ResponseEntity<String> verificarFestivo(@PathVariable int anio, @PathVariable int mes,
        @PathVariable int dia) {
        try {
            // Validar que el año esté entre 1983 y 9999
            if (anio < 1984 || anio > 9999) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Año no válido. Debe estar entre 1983 y 9999");
            }
            // Intentar crear una instancia de LocalDate con los valores proporcionados
            LocalDate fechaLocalDate = LocalDate.of(anio, mes, dia);

            // Convertir LocalDate a Date para usar con el servicio
            Date fecha = Date.from(fechaLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Verificar si es festivo usando el servicio
            String resultado = festivoServicio.verificarSiEsFestivo(fecha);
            return ResponseEntity.ok(resultado);
        } catch (DateTimeException e) {
            // Capturar excepciones de fechas inválidas
            return ResponseEntity.ok("Fecha no válida");
        }
    }

    @GetMapping("/obtener/{anio}")
    public ResponseEntity<List<FestivoDTO>> obtenerFestivosPorAnio(@PathVariable int anio) {
        try {
            if (anio < 1983 || anio > 9999) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            List<FestivoDTO> festivos = festivoServicio.obtenerFestivosPorAnio(anio);
            return ResponseEntity.ok(festivos);
        } catch (Exception e) {
            logger.error("Error al obtener festivos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
