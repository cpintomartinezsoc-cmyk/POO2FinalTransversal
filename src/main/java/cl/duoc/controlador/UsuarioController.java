package cl.duoc.controlador;

import cl.duoc.dao.UsuarioDAO;
import cl.duoc.modelo.Rol;
import cl.duoc.modelo.Usuario;
import cl.duoc.util.Validador;

import java.sql.SQLException;
import java.util.List;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.readAll();
    }

    public Usuario crear(String nombre, String rut, String correo, String contrasena, Rol rol) throws SQLException {
        Usuario usuario = construir(0, nombre, rut, correo, rol);
        usuario.setContrasena(Validador.contrasena(contrasena));
        usuarioDAO.create(usuario);
        return usuario;
    }

    public void actualizar(int id, String nombre, String rut, String correo, String contrasena, Rol rol)
            throws SQLException {
        Usuario usuario = construir(id, nombre, rut, correo, rol);
        boolean cambiarClave = contrasena != null && !contrasena.isBlank();
        if (cambiarClave) {
            usuario.setContrasena(Validador.contrasena(contrasena));
        }
        usuarioDAO.update(usuario, cambiarClave);
    }

    public void eliminar(Usuario usuario, Usuario usuarioActual) throws SQLException {
        if (usuario.getId() == usuarioActual.getId()) {
            throw new IllegalStateException("No puede eliminar el usuario con el que inició sesión.");
        }
        usuarioDAO.delete(usuario.getId());
    }

    private Usuario construir(int id, String nombre, String rut, String correo, Rol rol) {
        return new Usuario(
                id,
                Validador.nombre(nombre, "Nombre", 100),
                Validador.rut(rut),
                Validador.correo(correo),
                null,
                Validador.seleccion(rol, "un rol")
        );
    }
}