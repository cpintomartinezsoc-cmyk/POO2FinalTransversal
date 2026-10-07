package org.example;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.dao.HistorialDAO;
import cl.duoc.util.Mensajes;
import cl.duoc.vista.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                DatabaseConnection.getInstance().getConnection();
                new HistorialDAO().crearTablaSiNoExiste();
                System.out.println("Conexión exitosa a la base de datos biblioteca");
            } catch (Exception e) {
                Mensajes.error(null, "conectar con la base de datos", e);
                System.exit(1);
                return;
            }

            new LoginFrame().setVisible(true);
        });
    }
}