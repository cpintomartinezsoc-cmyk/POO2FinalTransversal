package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Historial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class HistorialDAO {

    private static final String SELECT_BASE = """
            SELECT h.id, h.id_prestamo, h.id_estudiante, h.id_libro, h.accion, h.fecha,
                   h.dias_atraso, h.observacion, e.nombre AS estudiante, l.titulo AS libro
              FROM historial_prestamos h
              LEFT JOIN estudiantes e ON e.id = h.id_estudiante
              LEFT JOIN libros l ON l.id = h.id_libro
            """;

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void crearTablaSiNoExiste() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS historial_prestamos (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    id_prestamo INT,
                    id_estudiante INT,
                    id_libro INT,
                    accion VARCHAR(20) NOT NULL,
                    fecha DATETIME NOT NULL,
                    dias_atraso INT DEFAULT 0,
                    observacion VARCHAR(200)
                )
                """;
        try (Statement st = conexion().createStatement()) {
            st.execute(sql);
        }
    }

    public boolean create(Historial historial) throws SQLException {
        String sql = "INSERT INTO historial_prestamos "
                + "(id_prestamo, id_estudiante, id_libro, accion, fecha, dias_atraso, observacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, historial.getIdPrestamo());
            ps.setInt(2, historial.getIdEstudiante());
            ps.setInt(3, historial.getIdLibro());
            ps.setString(4, historial.getAccion());
            ps.setTimestamp(5, Timestamp.valueOf(historial.getFecha()));
            ps.setInt(6, historial.getDiasAtraso());
            ps.setString(7, historial.getObservacion());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Historial> readAll() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY h.fecha DESC, h.id DESC", null);
    }

    public List<Historial> readByEstudiante(int idEstudiante) throws SQLException {
        return consultar(SELECT_BASE + " WHERE h.id_estudiante = ? ORDER BY h.fecha DESC, h.id DESC", idEstudiante);
    }

    private List<Historial> consultar(String sql, Integer idEstudiante) throws SQLException {
        List<Historial> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            if (idEstudiante != null) {
                ps.setInt(1, idEstudiante);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp fecha = rs.getTimestamp("fecha");
                    lista.add(new Historial(
                            rs.getInt("id"),
                            rs.getInt("id_prestamo"),
                            rs.getInt("id_estudiante"),
                            rs.getInt("id_libro"),
                            rs.getString("accion"),
                            fecha != null ? fecha.toLocalDateTime() : null,
                            rs.getInt("dias_atraso"),
                            rs.getString("observacion"),
                            rs.getString("estudiante"),
                            rs.getString("libro")
                    ));
                }
            }
        }
        return lista;
    }
}