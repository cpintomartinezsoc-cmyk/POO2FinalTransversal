package cl.duoc.vista;

import cl.duoc.controlador.EstudianteController;
import cl.duoc.controlador.LibroController;
import cl.duoc.controlador.PrestamoController;
import cl.duoc.controlador.ReporteController;
import cl.duoc.controlador.UsuarioController;
import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Usuario;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JPanel navegacion = new JPanel(new GridLayout(0, 1, 0, 8));
    private final JMenu menuModulos = new JMenu("Módulos");

    public MainFrame(Usuario usuario, Estudiante estudiante) {
        super("Biblioteca Escolar - " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1150, 650);
        setLocationRelativeTo(null);

        JLabel cabecera = new JLabel("  Bienvenido/a, " + usuario.getNombre() + "  |  Rol: " + usuario.getRol());
        cabecera.setFont(cabecera.getFont().deriveFont(Font.BOLD, 14f));
        cabecera.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Panel de navegación (lado izquierdo)
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lateral.add(navegacion, BorderLayout.NORTH);

        if (usuario.esBibliotecario()) {
            agregarModulo("Libros", new LibrosPanel(new LibroController(), false));
            agregarModulo("Estudiantes", new EstudiantesPanel(new EstudianteController()));
            agregarModulo("Usuarios", new UsuariosPanel(new UsuarioController(), usuario));
            agregarModulo("Préstamos", new PrestamosPanel(new PrestamoController(), null));
            agregarModulo("Reportes", new ReportesPanel(new ReporteController()));
        } else {
            agregarModulo("Catálogo de libros", new LibrosPanel(new LibroController(), true));
            agregarModulo("Mis préstamos", new PrestamosPanel(new PrestamoController(), estudiante));
        }

        JMenuBar barra = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        JMenuItem itemSalir = new JMenuItem("Salir");
        menuSesion.add(itemCerrarSesion);
        menuSesion.add(itemSalir);
        barra.add(menuSesion);
        barra.add(menuModulos);
        setJMenuBar(barra);

        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        itemSalir.addActionListener(e -> salir());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                salir();
            }
        });

        add(cabecera, BorderLayout.NORTH);
        add(lateral, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
    }

    private <T extends JPanel & Refrescable> void agregarModulo(String nombre, T panel) {
        contenido.add(panel, nombre);

        JButton boton = new JButton(nombre);
        boton.setPreferredSize(new Dimension(170, 36));
        boton.addActionListener(e -> mostrar(nombre, panel));
        navegacion.add(boton);

        JMenuItem item = new JMenuItem(nombre);
        item.addActionListener(e -> mostrar(nombre, panel));
        menuModulos.add(item);
    }

    private void mostrar(String nombre, Refrescable panel) {
        panel.refrescar();
        tarjetas.show(contenido, nombre);
    }

    private void cerrarSesion() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?", "Salir",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            DatabaseConnection.getInstance().cerrar();
            System.exit(0);
        }
    }
}