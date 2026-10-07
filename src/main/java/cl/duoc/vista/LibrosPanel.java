package cl.duoc.vista;

import cl.duoc.controlador.LibroController;
import cl.duoc.modelo.Categoria;
import cl.duoc.modelo.Libro;
import cl.duoc.util.Combos;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibrosPanel extends JPanel implements Refrescable {

    private final LibroController controlador;
    private final boolean soloLectura;

    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtAutor = new JTextField(15);
    private final JTextField txtIsbn = new JTextField(13);
    private final JTextField txtEditorial = new JTextField(15);
    private final JTextField txtStock = new JTextField(5);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JTextField txtBuscar = new JTextField(20);

    private final DefaultTableModel modelo =
            Tablas.modeloSoloLectura("ID", "Título", "Autor", "ISBN", "Editorial", "Categoría", "Stock");
    private final JTable tabla = Tablas.crear(modelo);

    private List<Libro> libros = new ArrayList<>();

    public LibrosPanel(LibroController controlador, boolean soloLectura) {
        this.controlador = controlador;
        this.soloLectura = soloLectura;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(soloLectura ? "Catálogo de libros" : "Gestión de libros"));

        JPanel norte = new JPanel(new GridLayout(0, 1));

        if (!soloLectura) {
            JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila1.add(new JLabel("Título:"));
            fila1.add(txtTitulo);
            fila1.add(new JLabel("Autor:"));
            fila1.add(txtAutor);
            fila1.add(new JLabel("ISBN:"));
            fila1.add(txtIsbn);

            JButton btnNuevaCategoria = new JButton("+ Categoría");
            JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila2.add(new JLabel("Editorial:"));
            fila2.add(txtEditorial);
            fila2.add(new JLabel("Stock:"));
            fila2.add(txtStock);
            fila2.add(new JLabel("Categoría:"));
            fila2.add(cmbCategoria);
            fila2.add(btnNuevaCategoria);

            JButton btnCrear = new JButton("Crear");
            JButton btnActualizar = new JButton("Actualizar");
            JButton btnEliminar = new JButton("Eliminar");
            JButton btnLimpiar = new JButton("Limpiar");
            JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila3.add(btnCrear);
            fila3.add(btnActualizar);
            fila3.add(btnEliminar);
            fila3.add(btnLimpiar);

            norte.add(fila1);
            norte.add(fila2);
            norte.add(fila3);

            btnCrear.addActionListener(e -> crear());
            btnActualizar.addActionListener(e -> actualizar());
            btnEliminar.addActionListener(e -> eliminar());
            btnLimpiar.addActionListener(e -> limpiar());
            btnNuevaCategoria.addActionListener(e -> nuevaCategoria());

            tabla.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    cargarFormulario();
                }
            });
        }

        JButton btnBuscar = new JButton("Buscar");
        JButton btnVerTodos = new JButton("Ver todos");
        JPanel filaBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaBuscar.add(new JLabel("Buscar por título, autor o ISBN:"));
        filaBuscar.add(txtBuscar);
        filaBuscar.add(btnBuscar);
        filaBuscar.add(btnVerTodos);
        norte.add(filaBuscar);

        btnBuscar.addActionListener(e -> cargarTabla());
        txtBuscar.addActionListener(e -> cargarTabla()); // Enter en el campo
        btnVerTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTabla();
        });

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        refrescar();
    }

    @Override
    public void refrescar() {
        if (!soloLectura) {
            cargarCategorias();
        }
        cargarTabla();
    }

    private void cargarCategorias() {
        try {
            Combos.recargar(cmbCategoria, controlador.listarCategorias());
        } catch (Exception e) {
            Mensajes.error(this, "cargar las categorías", e);
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            libros = controlador.buscar(txtBuscar.getText());
            for (Libro l : libros) {
                modelo.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                        l.getEditorial(), l.getNombreCategoria(), l.getStock()});
            }
        } catch (Exception e) {
            libros = new ArrayList<>();
            Mensajes.error(this, "cargar los libros", e);
        }
    }

    private void crear() {
        try {
            Libro libro = controlador.crear(txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(),
                    txtEditorial.getText(), txtStock.getText(), (Categoria) cmbCategoria.getSelectedItem());
            Mensajes.exito(this, "Libro registrado con ID " + libro.getId() + ".");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            Mensajes.error(this, "registrar el libro", e);
        }
    }

    private void actualizar() {
        Libro seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            controlador.actualizar(seleccionado.getId(), txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(),
                    txtEditorial.getText(), txtStock.getText(), (Categoria) cmbCategoria.getSelectedItem());
            Mensajes.exito(this, "Libro actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            Mensajes.error(this, "actualizar el libro", e);
        }
    }

    private void eliminar() {
        Libro seleccionado = seleccionado();
        if (seleccionado == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el libro \"" + seleccionado.getTitulo() + "\"?")) {
            return;
        }
        try {
            controlador.eliminar(seleccionado.getId());
            Mensajes.exito(this, "Libro eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            Mensajes.error(this, "eliminar el libro", e);
        }
    }

    private void nuevaCategoria() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva categoría:");
        if (nombre == null) {
            return;
        }
        try {
            Categoria categoria = controlador.crearCategoria(nombre);
            cargarCategorias();
            Combos.seleccionarPorId(cmbCategoria, categoria.getId());
        } catch (Exception e) {
            Mensajes.error(this, "registrar la categoría", e);
        }
    }

    private void cargarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        Libro libro = libros.get(fila);
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        txtStock.setText(String.valueOf(libro.getStock()));
        Combos.seleccionarPorId(cmbCategoria, libro.getIdCategoria());
    }

    private Libro seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione un libro en la tabla.");
            return null;
        }
        return libros.get(fila);
    }

    private void limpiar() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        tabla.clearSelection();
    }
}