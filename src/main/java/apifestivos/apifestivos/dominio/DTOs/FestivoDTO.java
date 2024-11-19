package apifestivos.apifestivos.dominio.DTOs;


import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * Clase DTO (Data Transfer Object) para representar un festivo.
 * Utilizada para transferir información de festivos entre capas de la aplicación.
 */

public class FestivoDTO {
     
    private String nombre; // Nombre del festivo
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
    private OffsetDateTime fecha; // Fecha del festivo con tiempo y zona horaria

    // Constructor vacío
    public FestivoDTO() {
    }

    // Constructor con parámetros
    public FestivoDTO(String nombre, OffsetDateTime fecha) {
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

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

}
