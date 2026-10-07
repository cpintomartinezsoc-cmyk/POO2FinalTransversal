package cl.duoc.vista;

import cl.duoc.controlador.ReporteController;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Historial;
import cl.duoc.modelo.LibroRanking;
import cl.duoc.modelo.Prestamo;
import cl.duoc.util.Combos;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Tablas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportesPanel extends JPanel implements Refrescable {

    private static final String MAS_PRESTADOS = "Libros más prestados";
    private static final String HISTORIAL = "Historial por estudiante";
    private static final String EN_PRESTAMO = "Libros actualmente en préstamo";
    private static final String MOVIMIENTOS = "Movimientos y atrasos (historial)";

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReporteController controlador;

    private final JComboBox<String> cmbReporte =
            new JComboBox<>(new String[]{MAS_PRESTADOS, HISTORIAL, EN_PRESTAMO, MOVIMIENTOS});
    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JLabel lblResumen = new JLabel(" ");

    private final DefaultTableModel modelo = Tablas.modeloSoloLectura();
    private final JTable tabla = Tablas.crear(modelo);

    public ReportesPanel(ReporteController controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Reportes"));

        cmbEstudiante.setPreferredSize(new Dimension(260, 26));

        JButton btnGenerar = new JButton("Generar reporte");
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Reporte:"));
        fila1.add(cmbReporte);
        fila1.add(new JLabel("Estudiante:"));
        fila1.add(cmbEstudiante);
        fila1.add(btnGenerar);

        lblResumen.setFont(lblResumen.getFont().deriveFont(Font.BOLD));
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(lblResumen);

        JPanel norte = new JPanel(new GridLayout(2, 1));
        norte.add(fila1);
        norte.add(fila2);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> generar());
        cmbReporte.addActionListener(e -> {
            cmbEstudiante.setEnabled(HISTORIAL.equals(cmbReporte.getSelectedItem()));
            generar();
        });

        refrescar();
    }

    @Override
    public void refrescar() {
        try {
            Combos.recargar(cmbEstudiante, controlador.listarEstudiantes());
        } catch (Exception e) {
            Mensajes.error(this, "cargar los estudiantes", e);
        }
        cmbEstudiante.setEnabled(HISTORIAL.equals(cmbReporte.getSelectedItem()));
        generar();
    }

    private void generar() {
        modelo.setRowCount(0);
        try {
            switch ((String) cmbReporte.getSelectedItem()) {
                case MAS_PRESTADOS -> librosMasPrestados();
                case HISTORIAL -> historialPorEstudiante();
                case EN_PRESTAMO -> librosEnPrestamo();
                default -> movimientos();
            }
        } catch (Exception e) {
            lblResumen.setText(" ");
            Mensajes.error(this, "generar el reporte", e);
        }
    }

    private void librosMasPrestados() throws Exception {
        modelo.setColumnIdentifiers(new String[]{"#", "Título", "Autor", "Cantidad de préstamos"});
        List<LibroRanking> ranking = controlador.librosMasPrestados();
        int posicion = 1;
        for (LibroRanking r : ranking) {
            modelo.addRow(new Object[]{posicion++, r.getTitulo(), r.getAutor(), r.getTotalPrestamos()});
        }
        lblResumen.setText("Top " + ranking.size() + " libros con más préstamos registrados.");
    }

    private void historialPorEstudiante() throws Exception {
        modelo.setColumnIdentifiers(new String[]{"ID", "Libro", "Fecha préstamo", "Vencimiento", "Estado"});
        Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
        List<Prestamo> historial = controlador.historialPorEstudiante(estudiante);
        long atrasados = 0;
        for (Prestamo p : historial) {
            modelo.addRow(new Object[]{p.getId(), p.getTituloLibro(), p.getFechaPrestamo(),
                    p.getFechaDevolucion(), p.getEstado()});
            if (p.estaAtrasado()) {
                atrasados++;
            }
        }
        lblResumen.setText(estudiante.getNombre() + ": " + historial.size() + " préstamos, "
                + atrasados + " atrasados actualmente.");
    }

    private void librosEnPrestamo() throws Exception {
        modelo.setColumnIdentifiers(new String[]{"ID", "Libro", "Estudiante", "Fecha préstamo", "Vencimiento", "Estado"});
        List<Prestamo> activos = controlador.librosEnPrestamo();
        long atrasados = 0;
        for (Prestamo p : activos) {
            modelo.addRow(new Object[]{p.getId(), p.getTituloLibro(), p.getNombreEstudiante(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(), p.getEstado()});
            if (p.estaAtrasado()) {
                atrasados++;
            }
        }
        lblResumen.setText(activos.size() + " libros en préstamo, " + atrasados + " con atraso.");
    }

    private void movimientos() throws Exception {
        modelo.setColumnIdentifiers(new String[]{"Fecha", "Acción", "Estudiante", "Libro", "Días de atraso", "Observación"});
        List<Historial> lista = controlador.movimientos();
        for (Historial h : lista) {
            modelo.addRow(new Object[]{
                    h.getFecha() != null ? h.getFecha().format(FORMATO_FECHA_HORA) : "",
                    h.getAccion(), h.getNombreEstudiante(), h.getTituloLibro(),
                    h.getDiasAtraso(), h.getObservacion()});
        }
        lblResumen.setText(lista.size() + " movimientos registrados por la aplicación.");
    }
}