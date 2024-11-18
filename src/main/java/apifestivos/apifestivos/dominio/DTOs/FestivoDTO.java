package apifestivos.apifestivos.dominio.DTOs;

import java.time.LocalDate;

/**
 * Clase DTO (Data Transfer Object) para representar un festivo.
 * Utilizada para transferir información de festivos entre capas de la aplicación.
 */

public class FestivoDTO {
     
    private String nombre; // Nombre del festivo
    private LocalDate fecha; // Fecha del festivo

    // Constructor vacío
    public FestivoDTO() {
    }

    // Constructor con parámetros
    public FestivoDTO(String nombre, LocalDate fecha) {
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

}
