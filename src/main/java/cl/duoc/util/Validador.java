package cl.duoc.util;

public final class Validador {

    private Validador() {
        // Clase utilitaria: no se instancia
    }

    public static String texto(String valor, String campo, int largoMaximo) {
        String limpio = valor == null ? "" : valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" es obligatorio.");
        }
        if (limpio.length() > largoMaximo) {
            throw new IllegalArgumentException(
                    "El campo \"" + campo + "\" admite como máximo " + largoMaximo + " caracteres.");
        }
        return limpio;
    }

    public static String nombre(String valor, String campo, int largoMaximo) {
        String limpio = texto(valor, campo, largoMaximo);
        if (!limpio.matches("[\\p{L} .'-]+")) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" solo puede contener letras y espacios.");
        }
        return limpio;
    }

    public static String rut(String valor) {
        String limpio = texto(valor, "RUT", 12).replace(".", "").toUpperCase();
        if (!limpio.matches("\\d{7,8}-[\\dK]")) {
            throw new IllegalArgumentException("El RUT debe tener el formato 12345678-9 (sin puntos y con guion).");
        }
        return limpio;
    }

    public static String correo(String valor) {
        String limpio = texto(valor, "Correo", 100).toLowerCase();
        if (!limpio.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido (ejemplo: nombre@correo.cl).");
        }
        return limpio;
    }

    public static String contrasena(String valor) {
        String limpio = texto(valor, "Contraseña", 100);
        if (limpio.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        return limpio;
    }

    public static String isbn(String valor) {
        String limpio = texto(valor, "ISBN", 20).replace("-", "").toUpperCase();
        if (!limpio.matches("\\d{13}|\\d{9}[\\dX]")) {
            throw new IllegalArgumentException("El ISBN debe tener 10 o 13 dígitos.");
        }
        return limpio;
    }

    public static int enteroNoNegativo(String valor, String campo) {
        String limpio = texto(valor, campo, 9);
        try {
            int numero = Integer.parseInt(limpio);
            if (numero < 0) {
                throw new IllegalArgumentException("El campo \"" + campo + "\" no puede ser negativo.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" debe ser un número entero.");
        }
    }

    public static <T> T seleccion(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("Debe seleccionar " + campo + ".");
        }
        return valor;
    }
}