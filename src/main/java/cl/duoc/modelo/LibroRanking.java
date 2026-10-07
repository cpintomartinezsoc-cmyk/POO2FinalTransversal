package cl.duoc.modelo;

public class LibroRanking {

    private final String titulo;
    private final String autor;
    private final int totalPrestamos;

    public LibroRanking(String titulo, String autor, int totalPrestamos) {
        this.titulo = titulo;
        this.autor = autor;
        this.totalPrestamos = totalPrestamos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getTotalPrestamos() {
        return totalPrestamos;
    }
}