package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Prestito;

public class PrestitiDAO {
    
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";

    public List<Prestito> getStoricoByUtente(String emailUtente) {
        List<Prestito> lista = new ArrayList<>();
        
        String sql = "SELECT p.*, l.titolo, l.autore, l.copertina, l.descrizione, " +
                     "(SELECT COUNT(*) FROM recensioni r WHERE r.libro_id = p.libro_id AND r.utente_email = p.utente_email) as recensioni_count " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " + 
                     "WHERE p.utente_email = ? " +
                     "ORDER BY p.data_inizio DESC";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, emailUtente);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestito p = new Prestito();
                        
                        p.setId(rs.getInt("id"));
                        p.setUtenteEmail(rs.getString("utente_email"));
                        p.setLibroId(rs.getInt("libro_id"));
                        p.setDataInizio(rs.getDate("data_inizio"));
                        p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                        p.setDataRestituzioneEffettiva(rs.getDate("data_restituzione_effettiva"));
                        
                        p.setTitoloLibro(rs.getString("titolo"));
                        p.setAutoreLibro(rs.getString("autore"));
                        p.setCopertinaLibro(rs.getString("copertina"));
                        p.setDescrizioneLibro(rs.getString("descrizione"));
                        
                        p.setRecensito(rs.getInt("recensioni_count") > 0);
                        
                        lista.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}