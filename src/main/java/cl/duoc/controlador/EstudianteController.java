package cl.duoc.controlador;

import cl.duoc.dao.EstudianteDAO;
import cl.duoc.dao.UsuarioDAO;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Rol;
import cl.duoc.modelo.Usuario;
import cl.duoc.util.Validador;

import java.sql.SQLException;
import java.util.List;


public class EstudianteController {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Estudiante> listar() throws SQLException {
        return estudianteDAO.readAll();
    }

    public Estudiante crear(String nombre, String rut, String curso, String correo, String contrasena)
            throws SQLException {
        Estudiante estudiante = construir(0, nombre, rut, curso, correo);
        String clave = Validador.contrasena(contrasena);

        if (usuarioDAO.readByRut(estudiante.getRut()) != null) {
            throw new IllegalStateException("Ya existe un usuario con el RUT " + estudiante.getRut() + ".");
        }

        estudianteDAO.create(estudiante);
        try {
            usuarioDAO.create(new Usuario(estudiante.getNombre(), estudiante.getRut(), estudiante.getCorreo(),
                    clave, Rol.ESTUDIANTE));
        } catch (SQLException e) {
            estudianteDAO.delete(estudiante.getId());
            throw e;
        }
        return estudiante;
    }

    public void actualizar(Estudiante original, String nombre, String rut, String curso, String correo,
                           String contrasena) throws SQLException {
        Estudiante editado = construir(original.getId(), nombre, rut, curso, correo);
        boolean cambiarClave = contrasena != null && !contrasena.isBlank();
        String clave = cambiarClave ? Validador.contrasena(contrasena) : null;

        estudianteDAO.update(editado);

        Usuario usuario = usuarioDAO.readByRut(original.getRut());
        if (usuario != null) {
            usuario.setNombre(editado.getNombre());
            usuario.setRut(editado.getRut());
            usuario.setCorreo(editado.getCorreo());
            usuario.setContrasena(clave);
            usuarioDAO.update(usuario, cambiarClave);
        } else if (cambiarClave) {
            usuarioDAO.create(new Usuario(editado.getNombre(), editado.getRut(), editado.getCorreo(),
                    clave, Rol.ESTUDIANTE));
        }
    }

    public void eliminar(Estudiante estudiante) throws SQLException {
        estudianteDAO.delete(estudiante.getId());
        usuarioDAO.deleteByRut(estudiante.getRut());
    }

    private Estudiante construir(int id, String nombre, String rut, String curso, String correo) {
        Estudiante estudiante = new Estudiante(
                Validador.nombre(nombre, "Nombre", 100),
                Validador.rut(rut),
                Validador.texto(curso, "Curso", 20),
                Validador.correo(correo)
        );
        estudiante.setId(id);
        return estudiante;
    }
}