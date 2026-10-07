package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Rol;
import cl.duoc.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {

    private static final String COLUMNAS = "id, nombre, rut, correo, `contraseña`, rol";

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Usuario autenticar(String correoORut, String contrasena) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE (correo = ? OR rut = ?) AND `contraseña` = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, correoORut);
            ps.setString(2, correoORut);
            ps.setString(3, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, `contraseña`, rol) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().getValorBD());
            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
            return filas > 0;
        }
    }

    public List<Usuario> readAll() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public Usuario readByRut(String rut) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE rut = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean update(Usuario usuario, boolean cambiarContrasena) throws SQLException {
        String sql = cambiarContrasena
                ? "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, rol = ?, `contraseña` = ? WHERE id = ?"
                : "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, rol = ? WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getRol().getValorBD());
            if (cambiarContrasena) {
                ps.setString(5, usuario.getContrasena());
                ps.setInt(6, usuario.getId());
            } else {
                ps.setInt(5, usuario.getId());
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteByRut(String rut) throws SQLException {
        try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM usuarios WHERE rut = ?")) {
            ps.setString(1, rut);
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString(5), // columna `contraseña`
                Rol.desdeBD(rs.getString("rol"))
        );
    }
}