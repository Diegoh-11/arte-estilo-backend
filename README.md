# 🛋️ Arte & Estilo — Backend

Backend REST para la plataforma e-commerce de muebles y decoración **Arte & Estilo**, desarrollado como proyecto académico para el **CESDE**.

Construido con **Java 17 + Spring Boot 3 + Spring Data JPA + PostgreSQL (Neon)** y siguiendo una arquitectura por capas (controller → service → repository → model).

---

## 📋 Tabla de contenidos

- [Características](#-características)
- [Stack tecnológico](#-stack-tecnológico)
- [Arquitectura del proyecto](#-arquitectura-del-proyecto)
- [Modelo de datos](#-modelo-de-datos)
- [Reglas de negocio](#-reglas-de-negocio)
- [Requisitos previos](#-requisitos-previos)
- [Configuración y ejecución](#-configuración-y-ejecución)
- [Endpoints principales](#-endpoints-principales)
- [Manejo de errores](#-manejo-de-errores)
- [Flujo de trabajo Git](#-flujo-de-trabajo-git)
- [Autor](#-autor)

---

## ✨ Características

- API REST versionada (`/api/v1/...`).
- Persistencia con **JPA / Hibernate** sobre **PostgreSQL**.
- Entidades con herencia vía `@MappedSuperclass` para campos auditables.
- Componente embebido (`@Embeddable`) para direcciones de envío.
- Enumeraciones persistidas como `STRING` (`EstadoPedido`).
- Manejo centralizado de excepciones con `@RestControllerAdvice`.
- Respuestas HTTP tipadas con `ResponseEntity<T>` (200, 201, 400, 404).
- Validación y descuento de stock transaccional.
- Aplicación automática de descuento por compras superiores a **$2.000.000 COP**.
- Cancelación controlada de pedidos con reintegro de inventario.

---

## 🧰 Stack tecnológico

| Componente | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Web (MVC) | incluido en Boot |
| Spring Data JPA (Hibernate) | incluido en Boot |
| PostgreSQL Driver | runtime |
| Lombok | última estable |
| Spring Boot DevTools | runtime |
| Maven | 3.9+ |
| Base de datos | PostgreSQL en Neon (cloud) |

---

## 🏗️ Arquitectura del proyecto

```
com.cesde.arteestilo
 ├── ArteEstiloApplication.java
 ├── controller/     → Endpoints REST (@RestController)
 ├── service/        → Reglas de negocio (@Service, @Transactional)
 ├── repository/     → Acceso a datos (JpaRepository)
 ├── model/          → Entidades JPA, enums y embebidos
 └── exception/      → Excepciones personalizadas + GlobalExceptionHandler
```

---

## 🗃️ Modelo de datos

Entidades principales (todas heredan de `BaseEntity`):

| Entidad | Descripción | Relaciones clave |
|---|---|---|
| `BaseEntity` | Clase abstracta `@MappedSuperclass` con `id`, `fechaCreacion`, `fechaActualizacion`, `estadoActivo`. | — |
| `Usuario` | Cliente de la tienda. | `@OneToOne` con `PerfilUsuario`, `@ManyToMany` con `Producto` (favoritos). |
| `PerfilUsuario` | Información extendida del usuario. | `@OneToOne(mappedBy="perfilUsuario")`. |
| `Categoria` | Categoría de productos (Sillas, Mesas, etc.). | `@OneToMany` con `Producto`. |
| `Producto` | Artículo de decoración o mueble. | `@ManyToOne` con `Categoria`, `@ManyToMany(mappedBy="favoritos")`. |
| `Pedido` | Orden de compra. | `@ManyToOne` con `Usuario`, `@OneToMany` con `DetallePedido`, `@Embedded DireccionEnvio`. |
| `DetallePedido` | Línea de un pedido. | `@ManyToOne` con `Pedido` y `Producto`. |
| `DireccionEnvio` | `@Embeddable` con dirección, ciudad, departamento, referencia. | — |
| `EstadoPedido` | Enum: `PENDIENTE`, `EN_CAMINO`, `ENTREGADO`, `CANCELADO`. | — |

---

## 📐 Reglas de negocio

### Regla 1 — Validación y deducción de stock al crear pedido
Al registrar un pedido se verifica que el stock del producto sea **≥** a la cantidad solicitada.
- ❌ Si no hay stock → `StockInsuficienteException` → **HTTP 400**.
- ✅ Si hay stock → descuenta inventario, calcula el total y persiste el pedido.

### Regla 2 — Descuento por compra superior
Si el total del pedido **supera $2.000.000 COP**, se aplica automáticamente un **10 % de descuento**.
- ❌ Si el monto es nulo o negativo → `MontoInvalidoException` → **HTTP 400**.
- ✅ Si no supera el monto → se procesa sin descuento.

### Regla 3 — Cancelación controlada de pedidos
Un pedido solo se puede cancelar si está en estado `PENDIENTE`.
- ❌ Si está `EN_CAMINO` o `ENTREGADO` → `PedidoNoCancelableException` → **HTTP 400**.
- ✅ Si está `PENDIENTE` → cambia a `CANCELADO` y **reintegra el stock** de todos sus productos.

---

## ✅ Requisitos previos

- **JDK 17** o superior.
- **Maven 3.9+** (o usar el wrapper `./mvnw`).
- Cuenta en **Neon** (o cualquier PostgreSQL accesible vía JDBC con `sslmode=require`).
- **Git** configurado.
- Variables de entorno configuradas (ver siguiente sección).

---

## ⚙️ Configuración y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/arte-estilo-backend.git
cd arte-estilo-backend
```

### 2. Crear el archivo `.env`

Copia la plantilla `.env.template` y completa los valores reales:

```bash
cp .env.template .env
```

Contenido esperado:

```properties
DB_URL=jdbc:postgresql://<HOST_NEON>:<PORT>/<DB_NAME>?sslmode=require
DB_USERNAME=<USUARIO>
DB_PASSWORD=<PASSWORD>
```

> El archivo `application.yml` lee estas variables con `${DB_URL}`, `${DB_USERNAME}` y `${DB_PASSWORD}`.

### 3. Ejecutar la aplicación

**Linux / macOS:**

```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

**PowerShell:**

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
.\mvnw.cmd spring-boot:run
```

**IntelliJ IDEA / VS Code:** configurar las variables en la *Run Configuration* o instalar el plugin de `.env`.

La API quedará disponible en:

```
http://localhost:8080/api/v1
```

> `spring.jpa.hibernate.ddl-auto: update` crea/actualiza automáticamente el esquema en el primer arranque.

---

## 🌐 Endpoints principales

### Productos

| Método | Endpoint | Descripción | Códigos |
|---|---|---|---|
| `GET` | `/api/v1/productos?categoria=Sillas&minStock=5` | Lista productos filtrando por categoría y/o stock mínimo. | 200 |
| `GET` | `/api/v1/productos/{id}` | Obtiene un producto por ID. | 200 / 404 |
| `POST` | `/api/v1/productos` | Crea un nuevo producto. | 201 / 400 |

### Pedidos

| Método | Endpoint | Descripción | Códigos |
|---|---|---|---|
| `POST` | `/api/v1/pedidos` | Crea un pedido (aplica Reglas 1 y 2). | 201 / 400 / 404 |
| `POST` | `/api/v1/pedidos/{id}/cancelar` | Cancela un pedido (aplica Regla 3). | 200 / 400 / 404 |
| `GET` | `/api/v1/pedidos?usuarioId=1&estado=PENDIENTE` | Lista pedidos por usuario y estado. | 200 |
| `GET` | `/api/v1/pedidos/{id}` | Obtiene un pedido por ID. | 200 / 404 |

### Usuarios

| Método | Endpoint | Descripción | Códigos |
|---|---|---|---|
| `GET` | `/api/v1/usuarios/{id}` | Obtiene un usuario por ID. | 200 / 404 |

### Ejemplo de request — crear pedido

```bash
curl -X POST http://localhost:8080/api/v1/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": { "id": 1 },
    "direccionEnvio": {
      "direccion": "Cra 45 #12-34",
      "ciudad": "Medellín",
      "departamento": "Antioquia",
      "referencia": "Apto 502"
    },
    "detalles": [
      { "producto": { "id": 1 }, "cantidad": 2 },
      { "producto": { "id": 3 }, "cantidad": 1 }
    ]
  }'
```

---

## 🚨 Manejo de errores

Todas las excepciones se traducen a un DTO estándar mediante `GlobalExceptionHandler`:

```json
{
  "fecha": "2025-01-15T14:32:11.482",
  "mensaje": "Stock insuficiente para el producto 'Silla Nórdica'. Disponible: 3, solicitado: 5",
  "codigoHttp": 400
}
```

| Excepción | HTTP |
|---|---|
| `StockInsuficienteException` | 400 Bad Request |
| `MontoInvalidoException` | 400 Bad Request |
| `PedidoNoCancelableException` | 400 Bad Request |
| `RecursoNoEncontradoException` | 404 Not Found |
| `IllegalArgumentException` | 400 Bad Request |

---

## 🌿 Flujo de trabajo Git

Este proyecto utiliza **Git Flow** con **Conventional Commits**.

- Rama de producción: **`main`**
- Rama de integración: **`develop`**
- Ramas de características: **`feature/XX-nombre`**

### Convención de commits

```
feat:     nueva funcionalidad
fix:      corrección de bug
chore:    tareas de mantenimiento / configuración
docs:     documentación
refactor: reestructuración sin cambio funcional
```

### Ramas utilizadas

| Rama | Propósito |
|---|---|
| `feature/01-modelos-jpa` | Entidades JPA, enums, embeddables y `BaseEntity`. |
| `feature/02-capa-repositorios` | Repositorios JpaRepository + queries personalizadas. |
| `feature/03-capa-servicios` | Excepciones, servicios y reglas de negocio. |
| `feature/04-capa-controladores` | Controladores REST, ResponseEntity y este README. |

### Versionado

Entrega etiquetada como **`v1.0.0`** en `main`.

---

## 👨‍💻 Autor

**Proyecto académico — CESDE**
Repositorio: [arte-estilo-backend](https://github.com/tu-usuario/arte-estilo-backend)
Año: 2025

---

## 📄 Licencia

Proyecto con fines educativos. Uso libre para propósitos académicos.