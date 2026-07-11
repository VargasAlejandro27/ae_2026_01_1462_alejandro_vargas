# Sistema de Estacionamiento

Este proyecto es una aplicación backend en Kotlin + Spring Boot para gestionar un estacionamiento.

## Qué hace

- Muestra los espacios de estacionamiento disponibles.
- Registra la entrada de un vehículo generando un ticket.
- Registra la salida de un vehículo cerrando el ticket y liberando el espacio.
- Controla que no se ingrese a espacios ya ocupados.
- Controla que el estacionamiento no supere una capacidad fija de 20 espacios.
- Usa seguridad OAuth2 JWT para proteger los endpoints de entrada y salida.

## Tecnologías

- Kotlin
- Spring Boot
- Spring Data JPA
- Spring Security OAuth2 Resource Server
- Validación con Jakarta Bean Validation
- PostgreSQL como base de datos
- Actuator con endpoint de salud

## Endpoints

### Obtener espacios libres

- `GET /api/parking/available`
- Público, no requiere autenticación
- Devuelve una lista de espacios libres con los campos:
  - `id`
  - `code`
  - `occupied`

Ejemplo de respuesta:

```json
[
  { "id": 1, "code": "A1", "occupied": false },
  { "id": 2, "code": "A2", "occupied": false }
]
```

### Registrar entrada

- `POST /api/parking/entry`
- Requiere usuario autenticado
- Request body:
  - `plate` (String) - Patente del vehículo
  - `parkingSpaceId` (Long) - ID del espacio de estacionamiento

Ejemplo de request:

```json
{
  "plate": "ABC123",
  "parkingSpaceId": 5
}
```

Devuelve un ticket con:
- `id`
- `plate`
- `entryTime`
- `exitTime` (null mientras esté activo)
- `parkingSpaceId`
- `parkingSpaceCode`

### Registrar salida

- `POST /api/parking/exit`
- Requiere usuario autenticado
- Request body:
  - `ticketId` (Long) - ID del ticket generado en la entrada

Ejemplo de request:

```json
{
  "ticketId": 10
}
```

Devuelve el ticket actualizado con `exitTime` y libera el espacio.

## Seguridad

- El endpoint `GET /api/parking/available` está abierto.
- Los endpoints `POST /api/parking/entry` y `POST /api/parking/exit` están protegidos y requieren JWT válido.
- La configuración de seguridad utiliza OAuth2 Resource Server con JWT.

## Configuración

Las propiedades se configuran en `src/main/resources/application.yaml` y pueden ser sobreescritas con variables de entorno:

- `SPRING_DATASOURCE_URL` (por defecto `jdbc:postgresql://localhost:5432/parkingdb`)
- `SPRING_DATASOURCE_USERNAME` (por defecto `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (por defecto `postgres`)
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` (por defecto un issuer de Cognito)

## Base de datos

El proyecto define dos entidades principales:

- `ParkingSpace` con `id`, `code` y `occupied`
- `Ticket` con `id`, `plate`, `entryTime`, `exitTime` y referencia al `ParkingSpace`

La capacidad del estacionamiento está fijada en 20 espacios en `ParkingService`.

## Ejecución

Desde la raíz del proyecto:

```powershell
./gradlew bootRun
```

Para pruebas:

```powershell
./gradlew test
```

## Notas importantes

- El servicio asume que los espacios de estacionamiento ya existen en la base de datos.
- No se permiten entradas a espacios ocupados ni salidas de tickets ya cerrados.
- La lógica de capacidad evita que se registren más de 20 vehículos al mismo tiempo.
# ae_2026_01_1463_alejandro_vargas_roles
