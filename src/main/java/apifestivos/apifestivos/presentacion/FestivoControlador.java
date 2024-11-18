package apifestivos.apifestivos.presentacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            if (mes < 1 || mes > 12 || dia < 1 || dia > 31) {
                return ResponseEntity.ok("Fecha no válida");
            }
            // Crear una instancia de LocalDate con los valores de año, mes y día
                Date fecha = new Date(anio - 1900, mes - 1, dia);

            // Verificar si es festivo usando el servicio
            String resultado = festivoServicio.verificarSiEsFestivo(fecha);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            // Capturar cualquier excepción que ocurra (por ejemplo, fechas inválidas)
            return ResponseEntity.ok("Fecha no válida");
        }
    }

    @GetMapping("/obtener/{anio}")
    public ResponseEntity<List<FestivoDTO>> obtenerFestivosPorAnio(@PathVariable int anio) {
        try {
            List<FestivoDTO> festivos = festivoServicio.obtenerFestivosPorAnio(anio);
            return ResponseEntity.ok(festivos);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }

    }

}
