package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private static final String SELECT_BASE = """
            SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, c.nombre AS categoria
              FROM libros l
              LEFT JOIN categorias c ON c.id = l.id_categoria
            """;

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, libro);
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Libro> readAll() throws SQLException {
        return buscar("");
    }

    public List<Libro> buscar(String texto) throws SQLException {
        String sql = SELECT_BASE + " WHERE l.titulo LIKE ? OR l.autor LIKE ? OR l.isbn LIKE ? ORDER BY l.titulo";
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        List<Libro> libros = new ArrayList<>();

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapear(rs));
                }
            }
        }
        return libros;
    }

    public Libro readById(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement(SELECT_BASE + " WHERE l.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean update(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            cargarParametros(ps, libro);
            ps.setInt(7, libro.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM libros WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }


    public boolean descontarStock(int idLibro) throws SQLException {
        String sql = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean aumentarStock(int idLibro) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("UPDATE libros SET stock = stock + 1 WHERE id = ?")) {
            ps.setInt(1, idLibro);
            return ps.executeUpdate() == 1;
        }
    }

    private void cargarParametros(PreparedStatement ps, Libro libro) throws SQLException {
        ps.setString(1, libro.getTitulo());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getIsbn());
        ps.setString(4, libro.getEditorial());
        ps.setInt(5, libro.getStock());
        ps.setInt(6, libro.getIdCategoria());
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getInt("id_categoria"),
                rs.getString("categoria")
        );
    }
}