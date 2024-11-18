package apifestivos.apifestivos.aplicacion;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public String verificarSiEsFestivo(Date fecha) {
        try {
            // Validar que la fecha no sea nula y que sea válida
            if (fecha == null || !esFechaValida(fecha)) {
                return "Fecha no válida";
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            int anio = cal.get(Calendar.YEAR);

            List<Festivo> festivos = festivoRepositorio.findAll();
            // Iterar sobre cada festivo para verificar si coincide con la fecha
            for (Festivo festivo : festivos) {
                Date fechaFestivo = calcularFechaFestivo(
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
                Date fechaFestivo = calcularFechaFestivo(
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

    private boolean esFechaValida(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return fecha != null && cal.get(Calendar.YEAR) > 0;
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

    private Date calcularFechaFestivo(int tipoFestivo, Integer dia, Integer mes, Integer diasPascua, int anio) {
        Date fechaFestivo;
        Date fechaPascua;

        switch (tipoFestivo) {
            case 1:
                // Festivo fijo que no se traslada
                fechaFestivo = new Date(anio - 1900, mes - 1, dia);
                break;

            case 2:
                // Festivo fijo que se traslada al siguiente lunes (Ley de Puente Festivo)
                fechaFestivo = new Date(anio - 1900, mes - 1, dia);
                fechaFestivo = siguienteLunes(fechaFestivo);
                break;

            case 3:
                // Festivo basado en el domingo de Pascua
                fechaPascua = getDomingoDePascua(anio);
                fechaFestivo = incrementarDias(fechaPascua, diasPascua);
                break;

            case 4:
                // Festivo basado en Pascua y que se traslada al siguiente lunes
                fechaPascua = getDomingoDePascua(anio);
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
    
     public Date getDomingoDePascua(int anio) {
        int a = anio % 19;
        int b = anio / 100;
        int c = anio % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;

        return new Date(anio - 1900, mes - 1, dia);
    }

    /**
     * Incrementa una fecha en una cantidad de días dada.
     * @param fecha Fecha inicial.
     * @param dias Días a incrementar.
     * @return Fecha incrementada.
     */
    public static Date incrementarDias(Date fecha, int dias) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(fecha);
        cld.add(Calendar.DATE, dias);
        return cld.getTime();

    }

    /**
     * Calcula el siguiente lunes de una fecha dada.
     * @param fecha Fecha de referencia.
     * @return Fecha del siguiente lunes.
     */
    public static Date siguienteLunes(Date fecha) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(fecha);

        int diaSemana = cld.get(Calendar.DAY_OF_WEEK);
        if (diaSemana != Calendar.MONDAY) {
            if (diaSemana > Calendar.MONDAY) {
                fecha = incrementarDias(fecha, 9 - diaSemana);
            } else {
                fecha = incrementarDias(fecha, 1);
            }
        }

        return fecha;
    }
   
}

