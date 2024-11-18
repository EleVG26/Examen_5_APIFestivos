package apifestivos.apifestivos.aplicacion;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import apifestivos.apifestivos.core.interfaces.repositorios.IFestivoRepositorio;
import apifestivos.apifestivos.core.interfaces.servicios.IFestivoServicio;
import apifestivos.apifestivos.dominio.DTOs.FestivoDTO;
import apifestivos.apifestivos.dominio.entidades.Festivo;

/**
 * Servicio que implementa la lógica de negocio relacionada con los festivos.
 */
@Service
public class FestivoServicio implements IFestivoServicio {

    private final IFestivoRepositorio festivoRepositorio;
    private static final Logger logger = Logger.getLogger(FestivoServicio.class.getName());

    /**
     * Constructor que inyecta el repositorio de festivos.
     * @param festivoRepositorio Repositorio para manejar la persistencia de los festivos.
     */
    public FestivoServicio(IFestivoRepositorio festivoRepositorio) {
        this.festivoRepositorio = festivoRepositorio;
    }

    /**
     * Verifica si una fecha determinada es un día festivo.
     * @param fecha Fecha a verificar.
     * @return "Es Festivo" si la fecha corresponde a un día festivo, "No es festivo" si no, o "Fecha no válida" si la fecha es incorrecta.
     */

    public String verificarSiEsFestivo(LocalDate fecha) {
        try {
            // Validar que la fecha no sea nula y que sea válida
            if (fecha == null || !esFechaValida(fecha)) {
                return "Fecha no válida";
            }

            int anio = fecha.getYear();

            // Obtener todos los festivos
            List<Festivo> festivos = festivoRepositorio.findAll();
            // Iterar sobre cada festivo para verificar si coincide con la fecha
            for (Festivo festivo : festivos) {
                LocalDate fechaFestivo = calcularFechaFestivo(
                        festivo.getTipo().getId(),
                        festivo.getDia(),
                        festivo.getMes(),
                        festivo.getDiasPascua(),
                        anio
                );
                // Comparar la fecha ingresada con la fecha del festivo
                if (fecha.equals(fechaFestivo)) {
                    return "Es Festivo";
                }
            }

            return "No es festivo";

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Se produjo una excepción", e);
            return "Error interno del servidor: " + e.getMessage();
        }
    }
    /**
     * Obtiene todos los festivos de un año determinado.
     * @param anio Año del que se quieren obtener los festivos.
     * @return Lista de festivos representados como DTOs.
     */
    public List<FestivoDTO> obtenerFestivosPorAnio(int anio) {
        List<Festivo> festivos = festivoRepositorio.findAll();
        List<FestivoDTO> festivosDTO = new ArrayList<>();
        // Iterar sobre cada festivo para calcular su fecha en el año dado
        for (Festivo festivo : festivos) {
            LocalDate fechaFestivo = calcularFechaFestivo(
                    festivo.getTipo().getId(),
                    festivo.getDia(),
                    festivo.getMes(),
                    festivo.getDiasPascua(),
                    anio);

            FestivoDTO dto = new FestivoDTO(festivo.getNombre(), fechaFestivo);
            festivosDTO.add(dto);
        }

        return festivosDTO;
    }
    /**
     * Valida si una fecha es válida.
     * @param fecha Fecha a validar.
     * @return True si la fecha es válida, false en caso contrario.
     */

    private boolean esFechaValida(LocalDate fecha) {
        return fecha != null && fecha.getYear() > 0; // Verifica que la fecha no sea nula y el año sea válido.
    }

    /**
     * Calcula la fecha de un festivo según su tipo.
     * @param tipoFestivo Tipo de festivo (fijo, que se traslada, etc.).
     * @param dia Día del mes del festivo.
     * @param mes Mes del festivo.
     * @param diasPascua Días desde Pascua (si aplica).
     * @param anio Año del que se quiere calcular el festivo.
     * @return Fecha del festivo calculada.
     */

    private LocalDate calcularFechaFestivo(int tipoFestivo, Integer dia, Integer mes, Integer diasPascua, int anio) {
        LocalDate fechaFestivo;

        switch (tipoFestivo) {
            case 1:
                // Festivo fijo que no se traslada
                fechaFestivo = LocalDate.of(anio, mes, dia);
                break;

            case 2:
                // Festivo fijo que se traslada al siguiente lunes (Ley de Puente Festivo)
                fechaFestivo = LocalDate.of(anio, mes, dia);
                fechaFestivo = siguienteLunes(fechaFestivo);
                break;

            case 3:
                // Festivo basado en el domingo de Pascua
                LocalDate fechaPascua = getSemanaSanta(anio);
                fechaFestivo = incrementarDias(fechaPascua, diasPascua);
                break;

            case 4:
                // Festivo basado en Pascua y que se traslada al siguiente lunes
                fechaPascua = getSemanaSanta(anio);
                fechaFestivo = incrementarDias(fechaPascua, diasPascua);
                fechaFestivo = siguienteLunes(fechaFestivo);
                break;

            default:
                throw new IllegalArgumentException("Tipo de festivo desconocido: " + tipoFestivo);
        }

        return fechaFestivo;
    }

    /**
     * Calcula la fecha del Domingo de Pascua para un año dado.
     * @param anio Año del que se quiere calcular la fecha de Pascua.
     * @return Fecha del Domingo de Pascua.
     */
    
    public LocalDate getSemanaSanta(int anio) {
        int a = anio % 19;
        int b = anio % 4;
        int c = anio % 7;
        int d = (19 * a + 24) % 30;
        int e = (2 * b + 4 * c + 6 * d + 5) % 7;
        int dia = 15 + d + e;
        return LocalDate.of(anio, 3, 1).plusDays(dia - 1);
    }

    /**
     * Incrementa una fecha en una cantidad de días dada.
     * @param fecha Fecha inicial.
     * @param dias Días a incrementar.
     * @return Fecha incrementada.
     */
    public LocalDate incrementarDias(LocalDate fecha, int dias) {
        return fecha.plusDays(dias);
    }

    /**
     * Calcula el siguiente lunes de una fecha dada.
     * @param fecha Fecha de referencia.
     * @return Fecha del siguiente lunes.
     */
    public LocalDate siguienteLunes(LocalDate fecha) {
        return fecha.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    }
}

