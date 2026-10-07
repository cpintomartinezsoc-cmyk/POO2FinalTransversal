package cl.duoc.modelo;

public class Libro implements Identificable {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private int idCategoria;

    private String nombreCategoria;

    public Libro(String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public Libro(int id, String titulo, String autor, String isbn, String editorial,
                 int stock, int idCategoria, String nombreCategoria) {
        this(titulo, autor, isbn, editorial, stock, idCategoria);
        this.id = id;
        this.nombreCategoria = nombreCategoria;
    }

    public boolean estaDisponible() {
        return stock > 0;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getStock() {
        return stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    @Override
    public String toString() {
        return id + " - " + titulo + " (stock: " + stock + ")";
    }
}