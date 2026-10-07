package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private static final String SELECT_BASE = """
            SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                   e.nombre AS estudiante, l.titulo AS libro
              FROM prestamos p
              LEFT JOIN estudiantes e ON e.id = p.id_estudiante
              LEFT JOIN libros l ON l.id = p.id_libro
            """;

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Prestamo> readAll() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY p.id DESC");
    }

    public List<Prestamo> readByEstudiante(int idEstudiante) throws SQLException {
        return consultar(SELECT_BASE + " WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC, p.id DESC",
                idEstudiante);
    }

    public List<Prestamo> readActivos() throws SQLException {
        return consultar(SELECT_BASE + " WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion");
    }

    public Prestamo readById(int id) throws SQLException {
        List<Prestamo> lista = consultar(SELECT_BASE + " WHERE p.id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public boolean existeActivo(int idEstudiante, int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_estudiante = ? AND id_libro = ? AND devuelto = FALSE";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean update(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE prestamos SET id_estudiante = ?, id_libro = ?, fecha_prestamo = ?, "
                + "fecha_devolucion = ?, devuelto = ? WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());
            return ps.executeUpdate() > 0;
        }
    }


    public boolean marcarDevuelto(int id) throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM prestamos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Prestamo> consultar(String sql, int... parametros) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setInt(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapear(rs));
                }
            }
        }
        return prestamos;
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Date prestamo = rs.getDate("fecha_prestamo");
        Date devolucion = rs.getDate("fecha_devolucion");

        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                prestamo != null ? prestamo.toLocalDate() : null,
                devolucion != null ? devolucion.toLocalDate() : null,
                rs.getBoolean("devuelto"),
                rs.getString("estudiante"),
                rs.getString("libro")
        );
    }
}