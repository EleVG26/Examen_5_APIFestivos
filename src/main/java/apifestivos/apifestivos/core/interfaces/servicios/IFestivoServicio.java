package apifestivos.apifestivos.core.interfaces.servicios;


import java.util.Date;
import java.util.List;
import apifestivos.apifestivos.dominio.DTOs.FestivoDTO;

public interface IFestivoServicio {
    
    //  Verifica si una fecha específica es un festivo.
    String verificarSiEsFestivo(Date fecha);
    // Obtiene todos los festivos del año especificado
    List<FestivoDTO> obtenerFestivosPorAnio(int anio);

}


