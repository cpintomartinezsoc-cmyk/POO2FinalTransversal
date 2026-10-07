package cl.duoc.vista;

import cl.duoc.controlador.UsuarioController;
import cl.duoc.modelo.Rol;
import cl.duoc.modelo.Usuario;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsuariosPanel extends JPanel implements Refrescable {

    private final UsuarioController controlador;
    private final Usuario usuarioActual;

    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtRut = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(18);
    private final JPasswordField txtContrasena = new JPasswordField(10);
    private final JComboBox<Rol> cmbRol = new JComboBox<>(Rol.values());

    private final DefaultTableModel modelo = Tablas.modeloSoloLectura("ID", "Nombre", "RUT", "Correo", "Rol");
    private final JTable tabla = Tablas.crear(modelo);

    private List<Usuario> usuarios = new ArrayList<>();

    public UsuariosPanel(UsuarioController controlador, Usuario usuarioActual) {
        this.controlador = controlador;
        this.usuarioActual = usuarioActual;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Gestión de usuarios"));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Nombre:"));
        fila1.add(txtNombre);
        fila1.add(new JLabel("RUT:"));
        fila1.add(txtRut);
        fila1.add(new JLabel("Rol:"));
        fila1.add(cmbRol);

        JLabel ayudaClave = new JLabel("(al actualizar, vacía = mantener)");
        ayudaClave.setForeground(Color.GRAY);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Correo:"));
        fila2.add(txtCorreo);
        fila2.add(new JLabel("Contraseña:"));
        fila2.add(txtContrasena);
        fila2.add(ayudaClave);

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(btnCrear);
        fila3.add(btnActualizar);
        fila3.add(btnEliminar);
        fila3.add(btnLimpiar);

        JPanel norte = new JPanel(new GridLayout(3, 1));
        norte.add(fila1);
        norte.add(fila2);
        norte.add(fila3);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFormulario();
            }
        });

        refrescar();
    }

    @Override
    public void refrescar() {
        modelo.setRowCount(0);
        try {
            usuarios = controlador.listar();
            for (Usuario u : usuarios) {
                modelo.addRow(new Object[]{u.getId(), u.getNombre(), u.getRut(), u.getCorreo(), u.getRol()});
            }
        } catch (Exception e) {
            usuarios = new ArrayList<>();
            Mensajes.error(this, "cargar los usuarios", e);
        }
    }

    private void crear() {
        try {
            Usuario usuario = controlador.crear(txtNombre.getText(), txtRut.getText(), txtCorreo.getText(),
                    new String(txtContrasena.getPassword()), (Rol) cmbRol.getSelectedItem());
            Mensajes.exito(this, "Usuario registrado con ID " + usuario.getId() + ".");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "registrar el usuario", e);
        }
    }

    private void actualizar() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            controlador.actualizar(seleccionado.getId(), txtNombre.getText(), txtRut.getText(), txtCorreo.getText(),
                    new String(txtContrasena.getPassword()), (Rol) cmbRol.getSelectedItem());
            Mensajes.exito(this, "Usuario actualizado correctamente.");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "actualizar el usuario", e);
        }
    }

    private void eliminar() {
        Usuario seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar al usuario \"" + seleccionado.getNombre() + "\"?")) {
            return;
        }
        try {
            controlador.eliminar(seleccionado, usuarioActual);
            Mensajes.exito(this, "Usuario eliminado correctamente.");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "eliminar el usuario", e);
        }
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Usuario u = usuarios.get(fila);
        txtNombre.setText(u.getNombre());
        txtRut.setText(u.getRut());
        txtCorreo.setText(u.getCorreo());
        txtContrasena.setText("");
        cmbRol.setSelectedItem(u.getRol());
    }

    private Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione un usuario en la tabla.");
            return null;
        }
        return usuarios.get(fila);
    }

    private void limpiar() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
        tabla.clearSelection();
    }
}