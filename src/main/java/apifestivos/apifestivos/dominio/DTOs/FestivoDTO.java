package apifestivos.apifestivos.dominio.DTOs;


import java.util.Date;

/**
 * Clase DTO (Data Transfer Object) para representar un festivo.
 * Utilizada para transferir información de festivos entre capas de la aplicación.
 */

public class FestivoDTO {
     
    private String nombre; // Nombre del festivo
    private Date fecha; // Fecha del festivo

    // Constructor vacío
    public FestivoDTO() {
    }

    // Constructor con parámetros
    public FestivoDTO(String nombre, Date fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

}
