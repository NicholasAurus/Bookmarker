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
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            
            String sql = "SELECT * FROM segnalazioni ORDER BY data_segnalazione DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Segnalazione s = new Segnalazione();
                s.setId(rs.getInt("id"));
                
                
                s.setUtenteEmail(rs.getString("utente_email")); 
                
                s.setRecensioneId(rs.getInt("recensione_id"));
                s.setMotivo(rs.getString("motivo"));
                s.setDataSegnalazione(rs.getDate("data_segnalazione"));
                s.setStato(rs.getString("stato")); 
                lista.add(s);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { 
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close(); 
            } catch (SQLException e) {}
        }
        return lista;
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
}