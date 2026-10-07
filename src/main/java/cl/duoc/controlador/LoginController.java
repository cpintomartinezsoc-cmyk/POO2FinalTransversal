package cl.duoc.controlador;

import cl.duoc.dao.EstudianteDAO;
import cl.duoc.dao.UsuarioDAO;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Usuario;
import cl.duoc.util.Validador;

import java.sql.SQLException;

public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();


    public Usuario autenticar(String correoORut, String contrasena) throws SQLException {
        String login = Validador.texto(correoORut, "Correo o RUT", 100);
        String clave = Validador.texto(contrasena, "Contraseña", 100);

        Usuario usuario = usuarioDAO.autenticar(login, clave);
        if (usuario == null) {
            throw new IllegalArgumentException("Correo/RUT o contraseña incorrectos.");
        }
        return usuario;
    }


    public Estudiante buscarEstudianteVinculado(Usuario usuario) throws SQLException {
        Estudiante estudiante = estudianteDAO.readByRut(usuario.getRut());
        if (estudiante == null) {
            throw new IllegalStateException("El usuario " + usuario.getNombre()
                    + " no tiene una ficha de estudiante asociada.\nContacte al bibliotecario.");
        }
        return estudiante;
    }
}