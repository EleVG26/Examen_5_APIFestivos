package apifestivos.apifestivos.aplicacion;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
     * 
     * @param festivoRepositorio Repositorio para manejar la persistencia de los
     *                           festivos.
     */
    public FestivoServicio(IFestivoRepositorio festivoRepositorio) {
        this.festivoRepositorio = festivoRepositorio;
    }

    /**
     * Verifica si una fecha determinada es un día festivo.
     * 
     * @param fecha Fecha a verificar.
     * @return "Es Festivo" si la fecha corresponde a un día festivo, "No es
     *         festivo" si no, o "Fecha no válida" si la fecha es incorrecta.
     */
    @Override
    public String verificarSiEsFestivo(Date fecha) {
        try {
            // Validar que la fecha no sea nula y que sea válida
            if (fecha == null || !esFechaValida(fecha)) {
                return "Fecha no válida";
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            int anio = cal.get(Calendar.YEAR);

            // Obtener todos los festivos
            List<Festivo> festivos = festivoRepositorio.findAll();
            // Iterar sobre cada festivo para verificar si coincide con la fecha
            for (Festivo festivo : festivos) {
                Date fechaFestivo;
                try {
                    fechaFestivo = calcularFechaFestivo(
                            festivo.getTipo().getId(),
                            festivo.getDia(),
                            festivo.getMes(),
                            festivo.getDiasPascua(),
                            anio);
                } catch (IllegalArgumentException e) {
                    // Fecha de festivo inválida, continuar con el siguiente
                    continue;
                }

                // Comparar la fecha ingresada con la fecha del festivo
                if (mismasFechasSinHora(fecha, fechaFestivo)) {
                    return "Es Festivo ";
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
     * 
     * @param anio Año del que se quieren obtener los festivos.
     * @return Lista de festivos representados como DTOs.
     */
    public List<FestivoDTO> obtenerFestivosPorAnio(int anio) {
        List<Festivo> festivos = festivoRepositorio.findAll();
        List<FestivoDTO> festivosDTO = new ArrayList<>();
        // Iterar sobre cada festivo para calcular su fecha en el año dado
        for (Festivo festivo : festivos) {
            try {
                Date fechaFestivo = calcularFechaFestivo(
                        festivo.getTipo().getId(),
                        festivo.getDia(),
                        festivo.getMes(),
                        festivo.getDiasPascua(),
                        anio);

                // OffsetDateTime fechaOffsetDateTime = fechaFestivo.toInstant()
                // .atOffset(ZoneOffset.UTC);
                LocalDateTime fechaLocalDateTime = fechaFestivo.toInstant()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDateTime();
                OffsetDateTime fechaOffsetDateTime = fechaLocalDateTime.atOffset(ZoneOffset.UTC);

                FestivoDTO dto = new FestivoDTO(festivo.getNombre(), fechaOffsetDateTime);
                festivosDTO.add(dto);
            } catch (IllegalArgumentException e) {
                // Registrar el error y continuar con el siguiente festivo
                logger.log(Level.WARNING, "Fecha inválida para festivo: " + festivo.getNombre(), e);
                continue;
            }

        }
        return festivosDTO;
    }

    /**
     * Valida si una fecha es válida.
     * 
     * @param fecha Fecha a validar.
     * @return True si la fecha es válida, false en caso contrario.
     */
    private boolean esFechaValida(Date fecha) {
        if (fecha == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false); // Establecer leniencia a false
        cal.setTime(fecha);
        try {
            // Verificar que el año, mes y día sean válidos
            cal.get(Calendar.YEAR);
            cal.get(Calendar.MONTH);
            cal.get(Calendar.DAY_OF_MONTH);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calcula la fecha de un festivo según su tipo.
     * 
     * @param tipoFestivo Tipo de festivo (fijo, que se traslada, etc.).
     * @param dia         Día del mes del festivo.
     * @param mes         Mes del festivo.
     * @param diasPascua  Días desde Pascua (si aplica).
     * @param anio        Año del que se quiere calcular el festivo.
     * @return Fecha del festivo calculada.
     */
    private Date calcularFechaFestivo(int tipoFestivo, Integer dia, Integer mes, Integer diasPascua, int anio) {
        Date fechaFestivo;
        Date fechaPascua;

        switch (tipoFestivo) {
            case 1:
                // Festivo fijo que no se traslada
                fechaFestivo = crearFecha(anio, mes, dia);
                break;

            case 2:
                // Festivo fijo que se traslada al siguiente lunes si cae entre martes y domingo
                fechaFestivo = crearFecha(anio, mes, dia);
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaFestivo);
                int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
                if (diaSemana != Calendar.MONDAY) {
                    fechaFestivo = siguienteLunes(fechaFestivo);
                }
                break;

            case 3:
                // Festivo basado en el domingo de Pascua
                fechaPascua = getDomingoDePascua(anio);
                fechaFestivo = incrementarDias(fechaPascua, diasPascua);
                break;

            case 4:
                // Festivo basado en Pascua y que se traslada al siguiente lunes si cae entre
                // martes y domingo
                fechaPascua = getDomingoDePascua(anio);
                fechaFestivo = incrementarDias(fechaPascua, diasPascua);
                cal = Calendar.getInstance();
                cal.setTime(fechaFestivo);
                diaSemana = cal.get(Calendar.DAY_OF_WEEK);
                if (diaSemana != Calendar.MONDAY) {
                    fechaFestivo = siguienteLunes(fechaFestivo);
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de festivo desconocido: " + tipoFestivo);
        }

        return fechaFestivo;
    }

    /**
     * Crea una fecha a partir de año, mes y día, validando que sea correcta.
     * 
     * @param anio Año.
     * @param mes  Mes.
     * @param dia  Día.
     * @return Objeto Date con la fecha especificada.
     * @throws IllegalArgumentException Si la fecha es inválida.
     */
    private Date crearFecha(int anio, int mes, int dia) throws IllegalArgumentException {
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false); // Establecer leniencia a false
        cal.clear();
        cal.set(Calendar.YEAR, anio);
        cal.set(Calendar.MONTH, mes - 1); // Meses van de 0 a 11
        cal.set(Calendar.DAY_OF_MONTH, dia);
        try {
            return cal.getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha inválida: " + dia + "/" + mes + "/" + anio);
        }
    }

    /**
     * Calcula la fecha del Domingo de Pascua para un año dado.
     * 
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

        return crearFecha(anio, mes, dia);
    }

    /**
     * Incrementa una fecha en una cantidad de días dada.
     * 
     * @param fecha Fecha inicial.
     * @param dias  Días a incrementar.
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
     * 
     * @param fecha Fecha de referencia.
     * @return Fecha del siguiente lunes.
     */
    public static Date siguienteLunes(Date fecha) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(fecha);

        int diaSemana = cld.get(Calendar.DAY_OF_WEEK);
        int diasParaLunes = ((Calendar.MONDAY - diaSemana + 7) % 7);
        if (diasParaLunes == 0) {
            diasParaLunes = 7;
        }
        cld.add(Calendar.DATE, diasParaLunes);
        return cld.getTime();
    }

    /**
     * Compara dos fechas sin considerar la hora.
     * 
     * @param fecha1 Primera fecha.
     * @param fecha2 Segunda fecha.
     * @return true si las fechas son iguales en año, mes y día.
     */
    private boolean mismasFechasSinHora(Date fecha1, Date fecha2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(fecha1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(fecha2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

}
