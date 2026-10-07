package cl.duoc.controlador;

import cl.duoc.dao.EstudianteDAO;
import cl.duoc.dao.LibroDAO;
import cl.duoc.dao.PrestamoDAO;
import cl.duoc.modelo.Estudiante;
import cl.duoc.modelo.Libro;
import cl.duoc.modelo.Prestamo;
import cl.duoc.servicio.PrestamoService;
import cl.duoc.util.Validador;

import javax.swing.SwingWorker;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class PrestamoController {

    private static final int TIEMPO_PROCESO_MS = 1500;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();


    public List<Prestamo> listar(Estudiante estudiante) throws SQLException {
        return estudiante == null ? prestamoDAO.readAll() : prestamoDAO.readByEstudiante(estudiante.getId());
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.readAll();
    }

    public void registrarPrestamoAsync(Estudiante estudiante, Libro libro,
                                       Consumer<Prestamo> alTerminar, Consumer<Exception> alFallar) {
        Validador.seleccion(estudiante, "un estudiante");
        Validador.seleccion(libro, "un libro");

        ejecutarEnSegundoPlano(() -> {
            Thread.sleep(TIEMPO_PROCESO_MS);
            return PrestamoService.prestar(estudiante.getId(), libro.getId());
        }, alTerminar, alFallar);
    }

    public void registrarDevolucionAsync(Prestamo prestamo, Consumer<Long> alTerminar, Consumer<Exception> alFallar) {
        Validador.seleccion(prestamo, "un préstamo de la tabla");
        if (prestamo.isDevuelto()) {
            throw new IllegalStateException("Este préstamo ya fue devuelto.");
        }

        ejecutarEnSegundoPlano(() -> {
            Thread.sleep(TIEMPO_PROCESO_MS);
            return PrestamoService.devolver(prestamo.getId());
        }, alTerminar, alFallar);
    }


    public void probarConcurrenciaAsync(Libro libro, int cantidadHilos,
                                        Consumer<String> alTerminar, Consumer<Exception> alFallar) {
        Validador.seleccion(libro, "un libro");

        ejecutarEnSegundoPlano(() -> {
            // Se eligen estudiantes que no tengan ya este libro prestado
            List<Estudiante> candidatos = new ArrayList<>();
            for (Estudiante e : estudianteDAO.readAll()) {
                if (!prestamoDAO.existeActivo(e.getId(), libro.getId())) {
                    candidatos.add(e);
                }
            }
            int hilos = Math.min(cantidadHilos, candidatos.size());
            if (hilos == 0) {
                throw new IllegalStateException("No hay estudiantes disponibles para la prueba.");
            }

            int stockInicial = libroDAO.readById(libro.getId()).getStock();
            CountDownLatch largada = new CountDownLatch(1);
            CountDownLatch termino = new CountDownLatch(hilos);
            AtomicInteger exitos = new AtomicInteger();
            List<String> detalle = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < hilos; i++) {
                Estudiante estudiante = candidatos.get(i);
                new Thread(() -> {
                    try {
                        largada.await(); // todos los hilos parten al mismo tiempo
                        PrestamoService.prestar(estudiante.getId(), libro.getId());
                        exitos.incrementAndGet();
                        detalle.add("✔ " + estudiante.getNombre() + ": préstamo registrado");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        detalle.add("✘ " + estudiante.getNombre() + ": " + e.getMessage());
                    } finally {
                        termino.countDown();
                    }
                }, "Prestamo-" + (i + 1)).start();
            }

            largada.countDown();
            termino.await();

            int stockFinal = libroDAO.readById(libro.getId()).getStock();

            StringBuilder resumen = new StringBuilder();
            resumen.append("Libro: ").append(libro.getTitulo()).append("\n");
            resumen.append("Hilos simultáneos: ").append(hilos).append("\n");
            resumen.append("Stock inicial: ").append(stockInicial).append("\n");
            resumen.append("Préstamos registrados: ").append(exitos.get()).append("\n");
            resumen.append("Préstamos rechazados: ").append(hilos - exitos.get()).append("\n");
            resumen.append("Stock final: ").append(stockFinal).append("\n\n");
            for (String linea : detalle) {
                resumen.append(linea).append("\n");
            }
            return resumen.toString();
        }, alTerminar, alFallar);
    }

    @FunctionalInterface
    private interface Tarea<T> {
        T ejecutar() throws Exception;
    }


    private <T> void ejecutarEnSegundoPlano(Tarea<T> tarea, Consumer<T> alTerminar, Consumer<Exception> alFallar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.ejecutar();
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    alFallar.accept(causa instanceof Exception ex ? ex : e);
                }
            }
        }.execute();
    }
}