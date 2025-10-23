# Auth Service API 🔐

Este proyecto es un microservicio de autenticación y gestión de usuarios construido con **Spring Boot**. Su principal responsabilidad es manejar el registro, el login y la generación de **Tokens JWT (JSON Web Tokens)** para asegurar otras APIs en una arquitectura de microservicios.

## ✨ Características

  * **Registro de Usuarios:** Permite a nuevos usuarios crear una cuenta.
  * **Autenticación de Usuarios:** Verifica las credenciales (email y contraseña) de un usuario.
  * **Generación de JWT:** Emite un token JWT firmado y con tiempo de expiración en un login exitoso.
  * **Seguridad:** Hashea las contraseñas usando `BCrypt` antes de almacenarlas en la base de datos.
  * **Arquitectura Stateless:** No utiliza sesiones HTTP, ideal para arquitecturas escalables.

-----

## 🛠️ Stack Tecnológico

  * **Lenguaje:** Java 17
  * **Framework:** Spring Boot 3.x
  * **Seguridad:** Spring Security 6.x
  * **Acceso a Datos:** Spring Data JPA (Hibernate)
  * **Base de Datos de Producción:** PostgreSQL
  * **Base de Datos de Pruebas:** H2 (En memoria)
  * **Gestión de Dependencias:** Maven
  * **Tokens:** JJWT (Java JWT)

-----

## 🚀 Puesta en Marcha (Getting Started)

Sigue estos pasos para configurar y correr el proyecto en tu entorno local.

### Prerrequisitos

  * JDK 17 o superior.
  * Maven 3.8 o superior.
  * Una instancia de PostgreSQL corriendo.
  * Docker (Opcional, recomendado para la base de datos).

### Instalación y Ejecución

1.  **Clona el repositorio:**

    ```bash
    git clone https://github.com/tu-usuario/auth-service.git
    cd auth-service
    ```

2.  **Configura la base de datos:**
    Abre el archivo `src/main/resources/application.properties` y configura los datos de conexión a tu base de datos PostgreSQL.

    ```properties
    # PostgreSQL Configuration
    spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
    spring.datasource.username=postgres
    spring.datasource.password=tu_contraseña_secreta

    # Hibernate Configuration
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```

3.  **Compila el proyecto:**
    Usa Maven para compilar el proyecto y descargar todas las dependencias.

    ```bash
    mvn clean install
    ```

4.  **Ejecuta la aplicación:**
    Puedes correr la aplicación usando el plugin de Spring Boot de Maven:

    ```bash
    mvn spring-boot:run
    ```

    ¡Listo\! El servidor estará corriendo en `http://localhost:8080`.

-----

## 📖 Documentación de la API

La API expone dos endpoints públicos bajo el prefijo `/auth`.

### 1\. Registrar un Nuevo Usuario

Registra un nuevo usuario en la base de datos.

  * **URL:** `/auth/register`
  * **Método:** `POST`
  * **Descripción:** Crea una cuenta para un nuevo usuario. La contraseña se almacena de forma segura (hasheada).

**Request Body (`application/json`):**

```json
{
    "name": "Marcelo Martínez",
    "email": "marcelo@ejemplo.com",
    "password": "unaClaveMuySegura123"
}
```

**Respuestas Posibles:**

| Código | Descripción              | Cuerpo de la Respuesta                               |
| :----- | :----------------------- | :--------------------------------------------------- |
| `200 OK` | Usuario creado con éxito. | `"Usuario registrado exitosamente!"` (String) |
| `500 Internal Server Error` | El email ya está en uso. | (Mensaje de error genérico de Spring) |

-----

### 2\. Autenticar un Usuario (Login)

Autentica a un usuario con su email y contraseña, y si es exitoso, devuelve un token JWT.

  * **URL:** `/auth/login`
  * **Método:** `POST`
  * **Descripción:** Verifica las credenciales y genera un token de acceso.

**Request Body (`application/json`):**

```json
{
    "email": "marcelo@ejemplo.com",
    "password": "unaClaveMuySegura123"
}
```

**Respuestas Posibles:**

| Código | Descripción              | Cuerpo de la Respuesta (`json`)                               |
| :----- | :----------------------- | :------------------------------------------------------------ |
| `200 OK` | Autenticación exitosa.   | `{ "token": "eyJhbGciOiJIUzUxMiJ9..." }`                      |
| `401 Unauthorized` | Credenciales inválidas.  | (Cuerpo de la respuesta vacío o con mensaje de error) |

-----

### Cómo usar el Token JWT

Una vez obtenido el token, el cliente debe enviarlo en la cabecera `Authorization` de cada petición a los endpoints protegidos de otros microservicios.

```http
GET /api/products
Host: localhost:8081
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

-----

## 🧪 Pruebas (Testing)

El proyecto incluye pruebas de integración y unitarias. Para ejecutar la suite de pruebas, usa el siguiente comando de Maven:

```bash
mvn test
```

Las pruebas se ejecutarán usando una base de datos H2 en memoria para no afectar la base de datos de desarrollo.
