package cl.duoc.servicio;

import cl.duoc.dao.HistorialDAO;
import cl.duoc.dao.LibroDAO;
import cl.duoc.dao.PrestamoDAO;
import cl.duoc.modelo.Historial;
import cl.duoc.modelo.Prestamo;

import java.sql.SQLException;
import java.time.LocalDate;


public final class PrestamoService {

    public static final int DIAS_PRESTAMO = 7;

    private static final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private static final LibroDAO libroDAO = new LibroDAO();
    private static final HistorialDAO historialDAO = new HistorialDAO();

    private PrestamoService() {
    }


    public static synchronized Prestamo prestar(int idEstudiante, int idLibro) throws SQLException {

        if (prestamoDAO.existeActivo(idEstudiante, idLibro)) {
            throw new IllegalStateException("El estudiante ya tiene este libro en préstamo.");
        }

        if (!libroDAO.descontarStock(idLibro)) {
            throw new IllegalStateException("No hay stock disponible para este libro.");
        }

        try {
            LocalDate hoy = LocalDate.now();
            Prestamo prestamo = new Prestamo(idEstudiante, idLibro, hoy, hoy.plusDays(DIAS_PRESTAMO));
            prestamoDAO.create(prestamo);

            historialDAO.create(new Historial(prestamo.getId(), idEstudiante, idLibro, Historial.PRESTAMO, 0,
                    "Préstamo registrado. Vence el " + prestamo.getFechaDevolucion()));

            return prestamo;

        } catch (SQLException e) {
            libroDAO.aumentarStock(idLibro);
            throw e;
        }
    }


    public static synchronized long devolver(int idPrestamo) throws SQLException {
        Prestamo prestamo = prestamoDAO.readById(idPrestamo);

        if (prestamo == null) {
            throw new IllegalStateException("El préstamo seleccionado ya no existe.");
        }
        if (!prestamoDAO.marcarDevuelto(idPrestamo)) {
            throw new IllegalStateException("Este préstamo ya fue devuelto.");
        }

        libroDAO.aumentarStock(prestamo.getIdLibro());

        long diasAtraso = prestamo.getDiasAtraso();
        String observacion = diasAtraso > 0
                ? "Devuelto con " + diasAtraso + " días de atraso (vencía el " + prestamo.getFechaDevolucion() + ")"
                : "Devuelto a tiempo";

        historialDAO.create(new Historial(idPrestamo, prestamo.getIdEstudiante(), prestamo.getIdLibro(),
                Historial.DEVOLUCION, (int) diasAtraso, observacion));

        return diasAtraso;
    }
}