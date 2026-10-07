package cl.duoc.modelo;

public class Usuario implements Identificable {

    private int id;
    private String nombre;
    private String rut;
    private String correo;
    private String contrasena;
    private Rol rol;

    public Usuario(String nombre, String rut, String correo, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Usuario(int id, String nombre, String rut, String correo, String contrasena, Rol rol) {
        this(nombre, rut, correo, contrasena, rol);
        this.id = id;
    }

    public boolean esBibliotecario() {
        return rol == Rol.BIBLIOTECARIO;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombre + " (" + rol + ")";
    }
}