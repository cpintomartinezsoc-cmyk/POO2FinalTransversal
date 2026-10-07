package cl.duoc.dao;

import cl.duoc.conexion.DatabaseConnection;
import cl.duoc.modelo.LibroRanking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<LibroRanking> librosMasPrestados(int limite) throws SQLException {
        String sql = """
                SELECT l.titulo, l.autor, COUNT(p.id) AS total
                  FROM prestamos p
                  JOIN libros l ON l.id = p.id_libro
                 GROUP BY l.id, l.titulo, l.autor
                 ORDER BY total DESC, l.titulo
                 LIMIT ?
                """;
        List<LibroRanking> ranking = new ArrayList<>();

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ranking.add(new LibroRanking(rs.getString("titulo"), rs.getString("autor"), rs.getInt("total")));
                }
            }
        }
        return ranking;
    }
}