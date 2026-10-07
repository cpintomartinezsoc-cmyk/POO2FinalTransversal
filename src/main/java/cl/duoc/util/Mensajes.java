package cl.duoc.util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

public final class Mensajes {

    private Mensajes() {
    }

    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(
                padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }


    public static void error(Component padre, String accion, Exception e) {
        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            advertencia(padre, e.getMessage());
            return;
        }
        if (e instanceof SQLException sql) {
            System.err.println("Error SQL (" + sql.getErrorCode() + "): " + sql.getMessage());
            JOptionPane.showMessageDialog(padre, traducir(accion, sql),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.err.println("Error inesperado: " + e);
        JOptionPane.showMessageDialog(padre, "Ocurrió un error al " + accion + ":\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static String traducir(String accion, SQLException e) {
        switch (e.getErrorCode()) {
            case 1062:
                return "No se puede " + accion + ": ya existe un registro con ese RUT o ISBN.";
            case 1451:
                return "No se puede " + accion + " porque tiene registros asociados\n"
                        + "(por ejemplo, préstamos o libros de esa categoría).";
            case 1452:
                return "El registro relacionado (estudiante, libro o categoría) ya no existe.";
            case 1045:
                return "Usuario o contraseña de MySQL incorrectos.\nRevise los datos en la clase DatabaseConnection.";
            case 1049:
                return "La base de datos \"biblioteca\" no existe.\nEjecute los scripts SQL en MySQL Workbench.";
            case 1146:
                return "Faltan tablas en la base de datos.\nEjecute los scripts SQL en MySQL Workbench.";
            case 1406:
                return "Uno de los datos es demasiado largo para guardarse.";
            default:
                break;
        }

        String mensaje = e.getMessage() != null ? e.getMessage() : "";

        if (mensaje.contains("No suitable driver")) {
            return "Falta el conector JDBC de MySQL en el proyecto (mysql-connector-j).";
        }
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return "No se pudo conectar con MySQL.\nVerifique que el servidor esté iniciado.";
        }
        return "Ocurrió un error al " + accion + ":\n" + mensaje;
    }
}