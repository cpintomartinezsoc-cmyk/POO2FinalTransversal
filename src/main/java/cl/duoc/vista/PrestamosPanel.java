package cl.duoc.vista;

import cl.duoc.controlador.PrestamoController;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Libro;
import cl.duoc.modelo.Prestamo;
import cl.duoc.servicio.PrestamoService;
import cl.duoc.util.Combos;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PrestamosPanel extends JPanel implements Refrescable {

    private static final String TODOS = "Todos";
    private static final String EN_PRESTAMO = "En préstamo";
    private static final String ATRASADOS = "Atrasados";
    private static final String DEVUELTOS = "Devueltos";

    private final PrestamoController controlador;
    private final Estudiante estudianteFijo;

    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JComboBox<Libro> cmbLibro = new JComboBox<>();
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{TODOS, EN_PRESTAMO, ATRASADOS, DEVUELTOS});
    private final JSpinner spnHilos = new JSpinner(new SpinnerNumberModel(5, 2, 10, 1));

    private final JButton btnPrestar = new JButton("Registrar préstamo");
    private final JButton btnDevolver = new JButton("Registrar devolución");
    private final JButton btnConcurrencia = new JButton("Prueba de concurrencia");
    private final JProgressBar barraProgreso = new JProgressBar();
    private final JLabel lblEstado = new JLabel(" ");

    private final DefaultTableModel modelo = Tablas.modeloSoloLectura(
            "ID", "Estudiante", "Libro", "Fecha préstamo", "Vencimiento", "Estado");
    private final JTable tabla = Tablas.crear(modelo);

    private List<Prestamo> prestamos = new ArrayList<>();

    public PrestamosPanel(PrestamoController controlador, Estudiante estudianteFijo) {
        this.controlador = controlador;
        this.estudianteFijo = estudianteFijo;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(estudianteFijo == null ? "Préstamos y devoluciones" : "Mis préstamos"));

        JLabel lblVencimiento = new JLabel("Vence en " + PrestamoService.DIAS_PRESTAMO + " días");
        lblVencimiento.setForeground(Color.GRAY);
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Estudiante:"));
        fila1.add(cmbEstudiante);
        fila1.add(new JLabel("Libro:"));
        fila1.add(cmbLibro);
        fila1.add(btnPrestar);
        fila1.add(lblVencimiento);

        JButton btnActualizar = new JButton("Actualizar lista");
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(btnDevolver);
        fila2.add(new JLabel("Mostrar:"));
        fila2.add(cmbFiltro);
        fila2.add(btnActualizar);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (estudianteFijo == null) {
            fila3.add(new JLabel("Hilos:"));
            fila3.add(spnHilos);
            fila3.add(btnConcurrencia);
        }
        barraProgreso.setIndeterminate(true);
        barraProgreso.setVisible(false);
        barraProgreso.setPreferredSize(new Dimension(150, 18));
        fila3.add(barraProgreso);
        fila3.add(lblEstado);

        JPanel norte = new JPanel(new GridLayout(3, 1));
        norte.add(fila1);
        norte.add(fila2);
        norte.add(fila3);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                           boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(t, valor, seleccionada, foco, fila, columna);
                if (!seleccionada) {
                    c.setForeground(String.valueOf(valor).startsWith("ATRASADO") ? Color.RED : Color.BLACK);
                }
                return c;
            }
        });

        cmbEstudiante.setPreferredSize(new Dimension(260, 26));
        cmbLibro.setPreferredSize(new Dimension(320, 26));

        if (estudianteFijo != null) {
            cmbEstudiante.setEnabled(false); // el estudiante solo puede pedir para sí mismo
        }

        btnPrestar.addActionListener(e -> prestar());
        btnDevolver.addActionListener(e -> devolver());
        btnConcurrencia.addActionListener(e -> probarConcurrencia());
        btnActualizar.addActionListener(e -> refrescar());
        cmbFiltro.addActionListener(e -> cargarTabla());

        refrescar();
    }

    @Override
    public void refrescar() {
        cargarCombos();
        cargarTabla();
    }

    private void cargarCombos() {
        try {
            if (estudianteFijo != null) {
                cmbEstudiante.removeAllItems();
                cmbEstudiante.addItem(estudianteFijo);
            } else {
                Combos.recargar(cmbEstudiante, controlador.listarEstudiantes());
            }
            Combos.recargar(cmbLibro, controlador.listarLibros());
        } catch (Exception e) {
            Mensajes.error(this, "cargar estudiantes y libros", e);
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            String filtro = (String) cmbFiltro.getSelectedItem();
            prestamos = new ArrayList<>();

            for (Prestamo p : controlador.listar(estudianteFijo)) {
                boolean mostrar = switch (filtro) {
                    case EN_PRESTAMO -> !p.isDevuelto();
                    case ATRASADOS -> p.estaAtrasado();
                    case DEVUELTOS -> p.isDevuelto();
                    default -> true;
                };
                if (mostrar) {
                    prestamos.add(p);
                    modelo.addRow(new Object[]{p.getId(), p.getNombreEstudiante(), p.getTituloLibro(),
                            p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()});
                }
            }
        } catch (Exception e) {
            prestamos = new ArrayList<>();
            Mensajes.error(this, "cargar los préstamos", e);
        }
    }

    private void prestar() {
        try {
            Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
            Libro libro = (Libro) cmbLibro.getSelectedItem();

            iniciarProceso("Registrando préstamo en segundo plano...");
            controlador.registrarPrestamoAsync(estudiante, libro,
                    prestamo -> {
                        terminarProceso();
                        Mensajes.exito(this, "Préstamo N° " + prestamo.getId() + " registrado.\n"
                                + "Fecha de vencimiento: " + prestamo.getFechaDevolucion());
                        refrescar();
                    },
                    error -> {
                        terminarProceso();
                        Mensajes.error(this, "registrar el préstamo", error);
                        refrescar();
                    });
        } catch (Exception e) {
            terminarProceso();
            Mensajes.error(this, "registrar el préstamo", e);
        }
    }

    private void devolver() {
        try {
            int fila = tabla.getSelectedRow();
            Prestamo prestamo = fila >= 0 ? prestamos.get(fila) : null;

            controlador.registrarDevolucionAsync(prestamo,
                    diasAtraso -> {
                        terminarProceso();
                        if (diasAtraso > 0) {
                            Mensajes.advertencia(this, "Devolución registrada CON ATRASO de " + diasAtraso
                                    + " días.\nQuedó constancia en el historial del estudiante.");
                        } else {
                            Mensajes.exito(this, "Devolución registrada a tiempo.");
                        }
                        refrescar();
                    },
                    error -> {
                        terminarProceso();
                        Mensajes.error(this, "registrar la devolución", error);
                        refrescar();
                    });
            iniciarProceso("Registrando devolución en segundo plano...");
        } catch (Exception e) {
            terminarProceso();
            Mensajes.error(this, "registrar la devolución", e);
        }
    }

    private void probarConcurrencia() {
        Libro libro = (Libro) cmbLibro.getSelectedItem();
        int hilos = (Integer) spnHilos.getValue();

        if (libro == null) {
            Mensajes.advertencia(this, "Seleccione un libro.");
            return;
        }
        if (!Mensajes.confirmar(this, hilos + " hilos intentarán prestar \"" + libro.getTitulo()
                + "\" al mismo tiempo.\nSe registrarán préstamos reales. ¿Continuar?")) {
            return;
        }
        try {
            iniciarProceso("Ejecutando prueba de concurrencia...");
            controlador.probarConcurrenciaAsync(libro, hilos,
                    resumen -> {
                        terminarProceso();
                        JTextArea texto = new JTextArea(resumen, 15, 45);
                        texto.setEditable(false);
                        JOptionPane.showMessageDialog(this, new JScrollPane(texto),
                                "Resultado de la prueba de concurrencia", JOptionPane.INFORMATION_MESSAGE);
                        refrescar();
                    },
                    error -> {
                        terminarProceso();
                        Mensajes.error(this, "ejecutar la prueba", error);
                    });
        } catch (Exception e) {
            terminarProceso();
            Mensajes.error(this, "ejecutar la prueba", e);
        }
    }

    private void iniciarProceso(String mensaje) {
        btnPrestar.setEnabled(false);
        btnDevolver.setEnabled(false);
        btnConcurrencia.setEnabled(false);
        barraProgreso.setVisible(true);
        lblEstado.setText(mensaje);
    }

    private void terminarProceso() {
        btnPrestar.setEnabled(true);
        btnDevolver.setEnabled(true);
        btnConcurrencia.setEnabled(true);
        barraProgreso.setVisible(false);
        lblEstado.setText(" ");
    }
}