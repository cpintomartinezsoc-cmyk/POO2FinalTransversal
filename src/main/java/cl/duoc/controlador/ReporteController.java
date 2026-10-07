package cl.duoc.controlador;

import cl.duoc.dao.EstudianteDAO;
import cl.duoc.dao.HistorialDAO;
import cl.duoc.dao.PrestamoDAO;
import cl.duoc.dao.ReporteDAO;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Historial;
import cl.duoc.modelo.LibroRanking;
import cl.duoc.modelo.Prestamo;
import cl.duoc.util.Validador;

import java.sql.SQLException;
import java.util.List;

public class ReporteController {

    private final ReporteDAO reporteDAO = new ReporteDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final HistorialDAO historialDAO = new HistorialDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    public List<LibroRanking> librosMasPrestados() throws SQLException {
        return reporteDAO.librosMasPrestados(10);
    }

    public List<Prestamo> historialPorEstudiante(Estudiante estudiante) throws SQLException {
        Validador.seleccion(estudiante, "un estudiante");
        return prestamoDAO.readByEstudiante(estudiante.getId());
    }

    public List<Prestamo> librosEnPrestamo() throws SQLException {
        return prestamoDAO.readActivos();
    }

    public List<Historial> movimientos() throws SQLException {
        return historialDAO.readAll();
    }
}