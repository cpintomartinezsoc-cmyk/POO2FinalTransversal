package cl.duoc.vista;

import cl.duoc.controlador.EstudianteController;
import cl.duoc.modelo.Estudiante;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class EstudiantesPanel extends JPanel implements Refrescable {

    private final EstudianteController controlador;

    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtRut = new JTextField(10);
    private final JTextField txtCurso = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(18);
    private final JPasswordField txtContrasena = new JPasswordField(10);

    private final DefaultTableModel modelo = Tablas.modeloSoloLectura("ID", "Nombre", "RUT", "Curso", "Correo");
    private final JTable tabla = Tablas.crear(modelo);

    private List<Estudiante> estudiantes = new ArrayList<>();

    public EstudiantesPanel(EstudianteController controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Gestión de estudiantes"));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Nombre:"));
        fila1.add(txtNombre);
        fila1.add(new JLabel("RUT:"));
        fila1.add(txtRut);
        fila1.add(new JLabel("Curso:"));
        fila1.add(txtCurso);

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
            estudiantes = controlador.listar();
            for (Estudiante e : estudiantes) {
                modelo.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
            }
        } catch (Exception e) {
            estudiantes = new ArrayList<>();
            Mensajes.error(this, "cargar los estudiantes", e);
        }
    }

    private void crear() {
        try {
            Estudiante estudiante = controlador.crear(txtNombre.getText(), txtRut.getText(), txtCurso.getText(),
                    txtCorreo.getText(), new String(txtContrasena.getPassword()));
            Mensajes.exito(this, "Estudiante registrado con ID " + estudiante.getId()
                    + ".\nYa puede iniciar sesión con su correo o RUT.");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "registrar el estudiante", e);
        }
    }

    private void actualizar() {
        Estudiante seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            controlador.actualizar(seleccionado, txtNombre.getText(), txtRut.getText(), txtCurso.getText(),
                    txtCorreo.getText(), new String(txtContrasena.getPassword()));
            Mensajes.exito(this, "Estudiante actualizado correctamente.");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "actualizar el estudiante", e);
        }
    }

    private void eliminar() {
        Estudiante seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar al estudiante \"" + seleccionado.getNombre()
                + "\"?\nTambién se eliminará su usuario.")) {
            return;
        }
        try {
            controlador.eliminar(seleccionado);
            Mensajes.exito(this, "Estudiante eliminado correctamente.");
            limpiar();
            refrescar();
        } catch (Exception e) {
            Mensajes.error(this, "eliminar el estudiante", e);
        }
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Estudiante e = estudiantes.get(fila);
        txtNombre.setText(e.getNombre());
        txtRut.setText(e.getRut());
        txtCurso.setText(e.getCurso());
        txtCorreo.setText(e.getCorreo());
        txtContrasena.setText("");
    }

    private Estudiante seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione un estudiante en la tabla.");
            return null;
        }
        return estudiantes.get(fila);
    }

    private void limpiar() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        tabla.clearSelection();
    }
}