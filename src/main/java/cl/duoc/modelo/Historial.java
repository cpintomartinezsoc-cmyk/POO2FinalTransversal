package cl.duoc.modelo;

import java.time.LocalDateTime;

public class Historial {

    public static final String PRESTAMO = "PRESTAMO";
    public static final String DEVOLUCION = "DEVOLUCION";

    private int id;
    private int idPrestamo;
    private int idEstudiante;
    private int idLibro;
    private String accion;
    private LocalDateTime fecha;
    private int diasAtraso;
    private String observacion;

    private String nombreEstudiante;
    private String tituloLibro;

    public Historial(int idPrestamo, int idEstudiante, int idLibro, String accion,
                     int diasAtraso, String observacion) {
        this.idPrestamo = idPrestamo;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.accion = accion;
        this.fecha = LocalDateTime.now().withNano(0);
        this.diasAtraso = diasAtraso;
        this.observacion = observacion;
    }

    public Historial(int id, int idPrestamo, int idEstudiante, int idLibro, String accion, LocalDateTime fecha,
                     int diasAtraso, String observacion, String nombreEstudiante, String tituloLibro) {
        this(idPrestamo, idEstudiante, idLibro, accion, diasAtraso, observacion);
        this.id = id;
        this.fecha = fecha;
        this.nombreEstudiante = nombreEstudiante;
        this.tituloLibro = tituloLibro;
    }

    public int getId() {
        return id;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getAccion() {
        return accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }
}