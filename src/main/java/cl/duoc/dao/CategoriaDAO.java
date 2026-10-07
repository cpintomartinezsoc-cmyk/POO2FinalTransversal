package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public boolean create(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Categoria> readAll() throws SQLException {
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return categorias;
    }

    public boolean update(Categoria categoria) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("UPDATE categorias SET nombre = ? WHERE id = ?")) {
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM categorias WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}