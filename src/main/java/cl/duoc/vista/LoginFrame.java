package cl.duoc.vista;

import cl.duoc.controlador.LoginController;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Usuario;
import cl.duoc.util.Mensajes;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final LoginController controlador = new LoginController();

    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);

    public LoginFrame() {
        super("Biblioteca Escolar - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("📚 Sistema de Gestión de Biblioteca");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(titulo, c);

        c.gridwidth = 1;
        c.gridy = 1;
        panel.add(new JLabel("Correo o RUT:"), c);
        c.gridx = 1;
        panel.add(txtUsuario, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        panel.add(txtContrasena, c);

        JButton btnIngresar = new JButton("Ingresar");
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        panel.add(btnIngresar, c);

        JLabel ayuda = new JLabel("Prueba: antonia@correo.cl / clave123 (bibliotecario)");
        ayuda.setForeground(Color.GRAY);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(ayuda, c);

        add(panel);
        pack();
        setLocationRelativeTo(null);

        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar); // Enter = Ingresar
    }

    private void ingresar() {
        try {
            Usuario usuario = controlador.autenticar(txtUsuario.getText(), new String(txtContrasena.getPassword()));

            // Si es estudiante, se busca su información personal (vinculada por RUT)
            Estudiante estudiante = usuario.esBibliotecario() ? null : controlador.buscarEstudianteVinculado(usuario);

            new MainFrame(usuario, estudiante).setVisible(true);
            dispose();

        } catch (Exception e) {
            txtContrasena.setText("");
            Mensajes.error(this, "iniciar sesión", e);
        }
    }
}