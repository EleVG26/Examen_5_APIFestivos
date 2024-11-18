package apifestivos.apifestivos.core.interfaces.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import apifestivos.apifestivos.dominio.entidades.Tipo;


/**
 * Interfaz para el repositorio de la entidad Tipo.
 * Esta interfaz extiende JpaRepository, lo que permite realizar operaciones CRUD
 * básicas para la entidad Tipo sin necesidad de escribir implementaciones específicas.
 */

public interface ITipoRepositorio extends JpaRepository<Tipo, Integer>{

}
