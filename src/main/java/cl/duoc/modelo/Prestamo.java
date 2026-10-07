package cl.duoc.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo implements Identificable {

    private int id;
    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    private String nombreEstudiante;
    private String tituloLibro;

    public Prestamo(int idEstudiante, int idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = false;
    }

    public Prestamo(int id, int idEstudiante, int idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                    boolean devuelto, String nombreEstudiante, String tituloLibro) {
        this(idEstudiante, idLibro, fechaPrestamo, fechaDevolucion);
        this.id = id;
        this.devuelto = devuelto;
        this.nombreEstudiante = nombreEstudiante;
        this.tituloLibro = tituloLibro;
    }

    public long getDiasAtraso() {
        if (devuelto || fechaDevolucion == null) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
        return Math.max(0, dias);
    }

    public boolean estaAtrasado() {
        return getDiasAtraso() > 0;
    }

    public String getEstado() {
        if (devuelto) {
            return "DEVUELTO";
        }
        if (estaAtrasado()) {
            return "ATRASADO (" + getDiasAtraso() + " días)";
        }
        return "EN PRÉSTAMO";
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }
}