<img width="488" height="157" alt="image" src="https://github.com/user-attachments/assets/e4b3565f-f210-4cee-ac43-b91791d97880" />

# 📚 Evaluación Final Transversal – Sistema de Gestión de Biblioteca Escolar

---

## 👤 Datos del estudiante

**Nombre:** Camilo Pinto

**Carrera:** Analista Programador

**Asignatura:** Desarrollo Orientado a Objetos II

**Semana:** 9

**Caso:** Sistema de Gestión de Biblioteca Escolar


---

## 📌 Descripción

En esta evaluación se desarrolla una aplicación de escritorio en **Java** para gestionar una biblioteca escolar.

El sistema permite registrar libros y estudiantes, controlar préstamos y devoluciones, detectar atrasos y generar reportes.

La aplicación utiliza **Java Swing** para la interfaz gráfica, **MySQL** con **JDBC** para la base de datos, y los patrones **MVC**, **DAO** y **Singleton**. Además, incorpora **hilos** y **sincronización** para registrar los préstamos sin congelar la interfaz y sin errores en el stock.

---

## 🔐 Roles del sistema

### Bibliotecario

Tiene acceso a todas las funciones:

* Gestión de libros.
* Gestión de estudiantes.
* Gestión de usuarios.
* Préstamos y devoluciones.
* Reportes.

### Estudiante

Tiene acceso limitado:

* Consulta del catálogo de libros.
* Registro de sus propios préstamos y devoluciones.
* Consulta de su historial de préstamos.

---

## 🗃️ Base de datos

La base de datos `biblioteca` contiene las siguientes tablas:

* `usuarios`: usuarios que pueden iniciar sesión, con su rol.
* `estudiantes`: información personal de los estudiantes.
* `categorias`: categorías de los libros.
* `libros`: inventario de libros con su stock.
* `prestamos`: préstamos realizados, con fecha de préstamo y fecha de vencimiento.

Además, la aplicación crea automáticamente la tabla `historial_prestamos`, que deja constancia de cada préstamo, devolución y atraso.

Los scripts utilizados son:

* `Script_crea_tablas_biblioteca.sql`
* `Script_poblado_tablas_biblioteca.sql`

---

## 🏗️ Estructura del proyecto

```text
src/main/java
│
├── cl.duoc
│   │
│   ├── conexion
│   │   └── DatabaseConnection.java
│   │
│   ├── controlador
│   │   ├── EstudianteController.java
│   │   ├── LibroController.java
│   │   ├── LoginController.java
│   │   ├── PrestamoController.java
│   │   ├── ReporteController.java
│   │   └── UsuarioController.java
│   │
│   ├── dao
│   │   ├── CategoriaDAO.java
│   │   ├── EstudianteDAO.java
│   │   ├── HistorialDAO.java
│   │   ├── LibroDAO.java
│   │   ├── PrestamoDAO.java
│   │   ├── ReporteDAO.java
│   │   └── UsuarioDAO.java
│   │
│   ├── modelo
│   │   ├── Categoria.java
│   │   ├── Estudiante.java
│   │   ├── Historial.java
│   │   ├── Identificable.java
│   │   ├── Libro.java
│   │   ├── LibroRanking.java
│   │   ├── Prestamo.java
│   │   ├── Rol.java
│   │   └── Usuario.java
│   │
│   ├── servicio
│   │   └── PrestamoService.java
│   │
│   ├── util
│   │   ├── Combos.java
│   │   ├── Mensajes.java
│   │   ├── Tablas.java
│   │   └── Validador.java
│   │
│   └── vista
│       ├── EstudiantesPanel.java
│       ├── LibrosPanel.java
│       ├── LoginFrame.java
│       ├── MainFrame.java
│       ├── PrestamosPanel.java
│       ├── Refrescable.java
│       ├── ReportesPanel.java
│       └── UsuariosPanel.java
│
└── org.example
    └── Main.java
```

---

## 🧩 Patrones de diseño

### MVC (Modelo - Vista - Controlador)

* **Modelo:** clases que representan los datos del sistema (`Usuario`, `Estudiante`, `Libro`, `Categoria`, `Prestamo`).
* **Vista:** ventanas y paneles creados con `JFrame` y `JPanel`.
* **Controlador:** clases que reciben las acciones de la vista, validan los datos y llaman a los DAO.

### DAO (Data Access Object)

Cada entidad tiene su propia clase DAO, encargada exclusivamente de la comunicación con la base de datos.

Los DAO implementan los métodos `create()`, `readAll()`, `update()` y `delete()` usando `PreparedStatement` y `ResultSet`.

### Singleton

La clase `DatabaseConnection` aplica el patrón Singleton.

Su método `getInstance()` garantiza que exista una única instancia de conexión a la base de datos en toda la aplicación.

---

## 📦 Descripción de las clases

### `DatabaseConnection`

Gestiona la conexión con MySQL mediante `DriverManager.getConnection`, usando el patrón Singleton.

### `Usuario`

Representa a las personas que pueden iniciar sesión en el sistema, con su rol de bibliotecario o estudiante.

### `Estudiante`

Representa la información personal de un estudiante: nombre, RUT, curso y correo.

### `Libro`

Representa un libro del inventario, con su título, autor, ISBN, editorial, stock y categoría.

### `Categoria`

Representa la categoría a la que pertenece un libro.

### `Prestamo`

Representa el préstamo de un libro a un estudiante.

Calcula automáticamente si el préstamo está atrasado y cuántos días de atraso tiene.

### `Historial`

Representa cada movimiento registrado en el historial del estudiante (préstamo o devolución).

### `Rol`

Enum que representa los roles del sistema:

* `BIBLIOTECARIO`
* `ESTUDIANTE`

### `PrestamoService`

Contiene la lógica de negocio de los préstamos y devoluciones.

Sus métodos son `synchronized` para que solo un hilo a la vez pueda modificar el stock de un libro.

### Controladores

`LoginController`, `LibroController`, `EstudianteController`, `UsuarioController`, `PrestamoController` y `ReporteController` conectan las vistas con los DAO y validan los datos ingresados.

### `Validador`

Revisa que los datos sean correctos antes de guardarlos, por ejemplo: campos obligatorios, formato de RUT, correo, ISBN y stock.

### `Mensajes`

Muestra mensajes de éxito, advertencia, confirmación y error mediante `JOptionPane`.

### `LoginFrame`

Ventana de inicio de sesión. Permite ingresar con correo o RUT y contraseña.

### `MainFrame`

Ventana principal del sistema.

Contiene un menú superior y un panel de navegación lateral con los módulos disponibles según el rol del usuario.

### Paneles

* `LibrosPanel`: gestión y búsqueda de libros.
* `EstudiantesPanel`: gestión de estudiantes.
* `UsuariosPanel`: gestión de usuarios.
* `PrestamosPanel`: registro de préstamos y devoluciones.
* `ReportesPanel`: visualización de reportes.

---

## 🧵 Concurrencia y control de consistencia

### Hilo en segundo plano

El registro de un préstamo se ejecuta en un hilo independiente mediante `SwingWorker`.

Este proceso registra el préstamo, actualiza el stock del libro y deja registro en el historial del estudiante, sin congelar la interfaz.

Mientras se procesa, se muestra una barra de progreso.

### Sincronización

Los métodos que modifican el stock son `synchronized`, por lo que solo un hilo a la vez puede ejecutarlos.

Además, el stock solo se descuenta si es mayor a cero, por lo que nunca puede quedar negativo.

### Prueba de concurrencia

El módulo de préstamos incluye una **prueba de concurrencia**, donde varios hilos intentan prestar el mismo libro al mismo tiempo.

El sistema solo registra tantos préstamos como stock disponible tenga el libro, y rechaza el resto.

---

## 🖥️ Interfaz gráfica

La aplicación utiliza componentes de **Java Swing**, entre ellos:

* `JFrame`
* `JPanel`
* `JMenuBar`
* `CardLayout`
* `JLabel`
* `JTextField`
* `JPasswordField`
* `JComboBox`
* `JButton`
* `JTable`
* `JProgressBar`
* `JOptionPane`

---

## ▶️ Ejecución

La aplicación comienza desde la clase `Main`.

Al ejecutar el programa, se verifica la conexión con la base de datos y se abre la ventana de inicio de sesión.

### Usuarios de prueba

* **Bibliotecario:** `antonia@correo.cl` / `clave123`
* **Estudiante:** `carlos@correo.cl` / `clave123`

### Gestión de libros

Permite registrar, modificar, eliminar y buscar libros.

También se pueden agregar nuevas categorías.

### Gestión de estudiantes

Permite registrar, modificar y eliminar estudiantes.

Al registrar un estudiante, también se crea su usuario para que pueda iniciar sesión.

### Préstamos y devoluciones

Permite registrar préstamos, verificando que el libro tenga stock disponible.

La fecha de vencimiento se calcula automáticamente (7 días después del préstamo).

Al registrar una devolución, si existe atraso, queda constancia en el historial del estudiante.

### Reportes

Permite generar los siguientes informes:

* Libros más prestados.
* Historial por estudiante.
* Libros actualmente en préstamo.
* Movimientos y atrasos.

---

## 🛡️ Validaciones y manejo de errores

* Se validan los campos obligatorios y los formatos antes de guardar.
* Se pide confirmación antes de eliminar un registro.
* No se puede prestar un libro sin stock disponible.
* Un estudiante no puede tener dos veces el mismo libro sin devolver.
* No se puede eliminar un libro o estudiante que tenga préstamos asociados.
* Los errores de la base de datos se muestran con mensajes claros.

---

📁 Repositorio


Proyecto: Sistema de Gestión de Biblioteca Escolar

https://github.com/cpintomartinezsoc-cmyk/Poo2EFT.git

Entrega: 11/10/2026

---
