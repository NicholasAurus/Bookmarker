package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import it.bookmarker.model.Recensione;

public class RecensioneDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";

    public List<Recensione> getRecensioniByLibro(int idLibro) {
        List<Recensione> lista = new ArrayList<>();
        String sql = "SELECT r.*, u.nome, u.cognome " +
                     "FROM recensioni r " +
                     "JOIN utenti u ON r.utente_id = u.email " + 
                     "WHERE r.libro_id = ? " +
                     "ORDER BY r.data_inserimento DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idLibro);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Recensione r = new Recensione();
                        r.setId(rs.getInt("id"));
                        r.setUtenteEmail(rs.getString("utente_id"));
                        r.setLibroId(rs.getInt("libro_id"));
                        r.setTesto(rs.getString("testo"));
                        r.setDataInserimento(rs.getDate("data_inserimento"));
                        r.setVoto(rs.getInt("voto")); 
                        
                        String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                        r.setNomeUtenteDisplay(nomeCompleto);

                        lista.add(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminaRecensione(String emailUtente, int idLibro) {
        String sql = "DELETE FROM recensioni WHERE utente_id = ? AND libro_id = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, emailUtente);
                ps.setInt(2, idLibro);
                
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}