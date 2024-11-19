# API Festivos

Esta es una API RESTful desarrollada en Java usando Spring Boot, cuyo propósito es determinar si una fecha específica es un festivo en Colombia y obtener la lista de festivos de un año dado, siguiendo la arquitectura Onion.

## Estructura del Proyecto

El proyecto sigue la arquitectura Onion y está dividido de la siguiente manera:

- **aplicacion**: Contiene la lógica de negocio, como los servicios para verificar si una fecha es festiva y obtener los festivos de un año (`FestivoServicio.java`).
- **core/interfaces**: Contiene las interfaces para los contratos:
  - **repositorios**: Interfaces que manejan la comunicación con la base de datos (`IFestivoRepositorio.java` y `ITipoRepositorio.java`).
  - **servicios**: Contratos de servicio (`IFestivoServicio.java`).
- **dominio**: Contiene los elementos de la API:
  - **entidades**: Representan las tablas de la base de datos (`Festivo.java` y `Tipo.java`).
  - **DTOs**: Objetos de Transferencia de Datos utilizados para transferir información entre capas (`FestivoDTO.java`).
- **presentacion**: Contiene los controladores que exponen los endpoints de la API (`FestivoControlador.java`).

## Tecnologías Utilizadas

- **Java 11**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **IDE: Visual Studio Code**

## Configuración de la Base de Datos

La base de datos PostgreSQL se llama `Festivos` y tiene dos tablas: `Tipo` y `Festivo`. A continuación, se detallan las instrucciones para crear y poblar la base de datos:

### Script DDL (Definición de las Tablas)

```sql
-- Crear la base de datos Festivos
CREATE DATABASE Festivos;

-- Crear la tabla Tipo
CREATE TABLE Tipo(
  Id SERIAL PRIMARY KEY,
  Tipo VARCHAR(100) NOT NULL
);

-- Crear la tabla Festivo
CREATE TABLE Festivo(
  Id SERIAL PRIMARY KEY,
  Nombre VARCHAR(100) NOT NULL,
  Dia INT,
  Mes INT,
  DiasPascua INT,
  IdTipo INT NOT NULL,
  CONSTRAINT fkFestivo_Tipo FOREIGN KEY (IdTipo) REFERENCES Tipo(Id)
);
```

### Población de la Base de Datos

### Script DML (Inserción de Datos)

```sql
-- Registros en la tabla Tipo
INSERT INTO Tipo(Id, Tipo) VALUES(1, 'Fijo');
INSERT INTO Tipo(Id, Tipo) VALUES(2, 'Ley Puente Festivo');
INSERT INTO Tipo(Id, Tipo) VALUES(3, 'Basado en Pascua');
INSERT INTO Tipo(Id, Tipo) VALUES(4, 'Basado en Pascua y Ley Puente Festivo');

-- Registros en la tabla Festivo
INSERT INTO Festivo (Dia, Mes, Nombre, IdTipo, DiasPascua) VALUES(1, 1, 'Año Nuevo', 1, 0);
INSERT INTO Festivo (Dia, Mes, Nombre, IdTipo, DiasPascua) VALUES(6, 1, 'Santos Reyes', 2, 0);
-- (más registros de ejemplo)
```

## Endpoints de la API

### Verificar si una fecha es festivo

- **URL**: `/festivos/verificar/{anio}/{mes}/{dia}`
- **Método**: `GET`
- **Descripción**: Verifica si la fecha ingresada es festiva, no es festiva o si la fecha no es válida.

#### Ejemplos

1. **URL**: `http://localhost:8080/festivos/verificar/2023/6/12`
   - **Respuesta**: `Es festivo`

2. **URL**: `http://localhost:8080/festivos/verificar/2023/2/28`
   - **Respuesta**: `No es festivo`

3. **URL**: `http://localhost:8080/festivos/verificar/2023/2/35`
   - **Respuesta**: `Fecha no válida`

### Obtener festivos de un año

- **URL**: `/festivos/obtener//{anio}`
- **Método**: `GET`
- **Descripción**: Obtiene todos los festivos de un año.

#### Ejemplo

1. **URL**: `http://localhost:8080/festivos/obtener/20223^`
    - **Respuesta**:
  
                   `{
                        "nombre": "Año nuevo",
                        "fecha": "2023-01-01T05:00:00.000"
                    },`
                    
                    {
                        "nombre": "Santos Reyes",
                        "fecha": "2023-01-09T05:00:00.000"
                    },

                    {
                        "nombre": "San José",
                        "fecha": "2023-03-20T05:00:00.000"
                    },

                    {
                        "nombre": "Jueves Santo",
                        "fecha": "2023-04-06T05:00:00.000"
                    },
                    
                    {
                        "nombre": "Viernes Santo",
                        "fecha": "2023-04-07T05:00:00.000"
                    },
                    `

## Ejecución del Proyecto

1. **Configurar la Base de Datos**
   - Crear la base de datos PostgreSQL utilizando los scripts proporcionados en la sección anterior.
   - Actualizar el archivo `application.properties` con las credenciales de la base de datos.

2. **Acceder a la API**
   - La aplicación estará disponible en `http://localhost:8080`.

## Consideraciones

- La arquitectura Onion facilita la separación de responsabilidades y hace que el proyecto sea más modular y fácil de mantener.
- La lógica de cálculo de los festivos relacionados con Semana Santa está implementada según las reglas proporcionadas por el docente.

## Autores

- Elena Vargas Grisales (Estudiante de Ingeniería de Sistemas)
  
- Esteban Luna Seña (Estudiante de Ingeniería de Sistemas)
  
- Proyecto desarrollado como parte evaluativa de la asignatura de Técnicas de la Programación

- Docente: Fray León Osorio Rivera- Ingeniero de Sistemas

- Universidad de Antioquia
