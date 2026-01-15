package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Prestito;

public class PrestitiDAO {
    
    private String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String USER = "root";
    private String PASS = "Bookmarker09!";

  
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
                        p.setStato(rs.getString("stato")); 
                        
                     
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
    
   
    public List<Prestito> getPrestitiRichiesti() {
        List<Prestito> lista = new ArrayList<>();
        
     
        String sql = "SELECT p.*, l.titolo " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " +
                     "WHERE p.stato = 'richiesto'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Prestito p = new Prestito();
                    
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                    p.setStato(rs.getString("stato"));
                    
                   
                    p.setTitoloLibro(rs.getString("titolo"));
                    
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    
    public void gestisciPrestito(int idPrestito, String nuovoStato) {
        String sql = "UPDATE prestiti SET stato = ? WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, nuovoStato);
                pstmt.setInt(2, idPrestito);
                
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    public List<Prestito> getPrestitiPrenotati() {
        List<Prestito> lista = new ArrayList<>();
        
     
        String sql = "SELECT p.*, l.titolo " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " +
                     "WHERE p.stato = 'prenotato'"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                    p.setStato(rs.getString("stato"));
                    
                   
                    p.setTitoloLibro(rs.getString("titolo"));
                    
                    lista.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

   
    public void confermaRitiro(int idPrestito) {
        String sql = "UPDATE prestiti SET stato = 'attivo', data_inizio = CURDATE(), data_fine_prevista = DATE_ADD(CURDATE(), INTERVAL 30 DAY) WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idPrestito);
                pstmt.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    
    
    public List<Prestito> getPrestitiAttivi() {
        List<Prestito> lista = new ArrayList<>();
        
    
        String sql = "SELECT p.*, l.titolo " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " +
                     "WHERE p.stato = 'attivo'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                    p.setStato(rs.getString("stato"));
                    
                    
                    p.setTitoloLibro(rs.getString("titolo"));
                    
                    lista.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

   
    public void terminaPrestito(int idPrestito) {
        String sql = "UPDATE prestiti SET stato = 'restituito', data_restituzione_effettiva = CURDATE() WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idPrestito);
                pstmt.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    
    public List<Prestito> getPrestitiRestituiti() {
        List<Prestito> lista = new ArrayList<>();
        
        
        String sql = "SELECT p.*, l.titolo " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " +
                     "WHERE p.stato = 'restituito' " +
                     "ORDER BY data_restituzione_effettiva DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataRestituzioneEffettiva(rs.getDate("data_restituzione_effettiva"));
                    p.setStato(rs.getString("stato"));
                    
                    
                    p.setTitoloLibro(rs.getString("titolo"));
                    
                    lista.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}