package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Segnalazione;

public class SegnalazioniDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String jdbcUsername = "root";
    private String jdbcPassword = "Bookmarker09!";

    public List<Segnalazione> getAllSegnalazioni() {
        List<Segnalazione> lista = new ArrayList<>();
        
        
        String sql = "SELECT s.*, r.libro_id " + 
                     "FROM segnalazioni s " +
                     "JOIN recensioni r ON s.recensione_id = r.id " +
                     "ORDER BY FIELD(s.stato, 'aperta', 'chiusa'), s.data_segnalazione DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Segnalazione s = new Segnalazione();
                    s.setId(rs.getInt("id"));
                    s.setUtenteEmail(rs.getString("utente_email"));
                    s.setRecensioneId(rs.getInt("recensione_id"));
                    s.setMotivo(rs.getString("motivo")); 
                    s.setDataSegnalazione(rs.getDate("data_segnalazione"));
                    s.setStato(rs.getString("stato"));
                    s.setLibroId(rs.getInt("libro_id")); 
                    s.setNoteChiusura(rs.getString("note_chiusura"));

                    lista.add(s);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }


    public boolean chiudiSegnalazione(int idSegnalazione, String note) {

        String sql = "UPDATE segnalazioni SET stato = 'chiusa', note_chiusura = ? WHERE id = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, note);
                ps.setInt(2, idSegnalazione);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean aggiornaStato(int id, String nuovoStato) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean updated = false;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            String sql = "UPDATE segnalazioni SET stato = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuovoStato);
            pstmt.setInt(2, id);
            
            updated = pstmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { 
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close(); 
            } catch (SQLException e) {}
        }
        return updated;
    }
    
    public boolean inserisciSegnalazione(int recensioneId, String emailUtente, String motivo) {
        String sql = "INSERT INTO segnalazioni (recensione_id, utente_email, motivo, data_segnalazione, stato) VALUES (?, ?, ?, CURDATE(), 'aperta')";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean inserito = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recensioneId);
            pstmt.setString(2, emailUtente);
            pstmt.setString(3, motivo);
            
            inserito = pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { 
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close(); 
            } catch (SQLException e) {}
        }
        return inserito;
    }
}