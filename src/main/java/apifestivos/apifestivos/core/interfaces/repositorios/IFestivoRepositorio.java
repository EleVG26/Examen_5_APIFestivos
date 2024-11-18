package apifestivos.apifestivos.core.interfaces.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import apifestivos.apifestivos.dominio.entidades.Festivo;

/**
 * Anotamos la clase con @Repository para que Spring la reconozca como un componente de persistencia.
 * Esta interfaz extiende JpaRepository para aprovechar los métodos CRUD (Crear, Leer, Actualizar, Borrar)
 * ya implementados por defecto para la entidad Festivo.
 */
@Repository
public interface IFestivoRepositorio extends JpaRepository<Festivo, Long> {
   
}



