package cl.duoc.controlador;

import cl.duoc.dao.CategoriaDAO;
import cl.duoc.dao.LibroDAO;
import cl.duoc.modelo.Categoria;
import cl.duoc.modelo.Libro;
import cl.duoc.util.Validador;

import java.sql.SQLException;
import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Libro> listar() throws SQLException {
        return libroDAO.readAll();
    }

    public List<Libro> buscar(String texto) throws SQLException {
        return libroDAO.buscar(texto);
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.readAll();
    }

    public Categoria crearCategoria(String nombre) throws SQLException {
        Categoria categoria = new Categoria(Validador.texto(nombre, "Categoría", 100));
        categoriaDAO.create(categoria);
        return categoria;
    }

    public Libro crear(String titulo, String autor, String isbn, String editorial,
                       String stock, Categoria categoria) throws SQLException {
        Libro libro = construir(0, titulo, autor, isbn, editorial, stock, categoria);
        libroDAO.create(libro);
        return libro;
    }

    public void actualizar(int id, String titulo, String autor, String isbn, String editorial,
                           String stock, Categoria categoria) throws SQLException {
        libroDAO.update(construir(id, titulo, autor, isbn, editorial, stock, categoria));
    }

    public void eliminar(int id) throws SQLException {
        libroDAO.delete(id);
    }

    private Libro construir(int id, String titulo, String autor, String isbn, String editorial,
                            String stock, Categoria categoria) {
        Libro libro = new Libro(
                Validador.texto(titulo, "Título", 200),
                Validador.texto(autor, "Autor", 100),
                Validador.isbn(isbn),
                Validador.texto(editorial, "Editorial", 100),
                Validador.enteroNoNegativo(stock, "Stock"),
                Validador.seleccion(categoria, "una categoría").getId()
        );
        libro.setId(id);
        return libro;
    }
}