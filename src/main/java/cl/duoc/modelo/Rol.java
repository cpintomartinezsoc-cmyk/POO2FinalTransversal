package cl.duoc.modelo;

public enum Rol {

    BIBLIOTECARIO("bibliotecario"),
    ESTUDIANTE("estudiante");

    private final String valorBD;

    Rol(String valorBD) {
        this.valorBD = valorBD;
    }

    public String getValorBD() {
        return valorBD;
    }

    public static Rol desdeBD(String valor) {
        for (Rol rol : values()) {
            if (rol.valorBD.equalsIgnoreCase(valor)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + valor);
    }

    @Override
    public String toString() {
        return valorBD.substring(0, 1).toUpperCase() + valorBD.substring(1);
    }
}