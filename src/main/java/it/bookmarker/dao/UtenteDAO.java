package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.bookmarker.model.Utente;

public class UtenteDAO {

    private String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String user = "root";
    private String pass = "Bookmarker09!";

    public boolean registraUtente(Utente utente) throws SQLException {
        String sql = "INSERT INTO utenti (nome, cognome, codice_fiscale, email, password, ruolo, stato, data_registrazione, domanda_sicurezza, risposta_sicurezza) VALUES (?, ?, ?, ?, ?, ?, 'in_attesa', CURDATE(), ?, ?)";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getCodiceFiscale());
            pstmt.setString(4, utente.getEmail());
            pstmt.setString(5, utente.getPassword());
            pstmt.setString(6, utente.getRuolo()); 
            pstmt.setString(7, utente.getDomandaSicurezza());
            pstmt.setString(8, utente.getRispostaSicurezza());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean esisteEmail(String email) {
        boolean esiste = false;
        String sql = "SELECT 1 FROM utenti WHERE email = ?"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        esiste = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return esiste;
    }
    
    public boolean esisteCodiceFiscale(String codiceFiscale) {
        boolean esiste = false;
        String sql = "SELECT 1 FROM utenti WHERE codice_fiscale = ?"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, codiceFiscale);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        esiste = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return esiste;
    }
    
    public Utente getUtenteByEmail(String email) {
        Utente utente = null;
        String query = "SELECT * FROM utenti WHERE email = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        utente = new Utente(); 
                        
                        utente.setNome(rs.getString("nome"));
                        utente.setCognome(rs.getString("cognome")); 
                        utente.setEmail(rs.getString("email"));
                        utente.setRuolo(rs.getString("ruolo"));
                        utente.setPassword(rs.getString("password"));
                        utente.setCodiceFiscale(rs.getString("codice_fiscale"));
                        utente.setDataRegistrazione(rs.getDate("data_registrazione"));
                        utente.setStato(rs.getString("stato"));
                        utente.setDomandaSicurezza(rs.getString("domanda_sicurezza"));
                        utente.setRispostaSicurezza(rs.getString("risposta_sicurezza"));
                        
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return utente; 
    }

    public boolean updateStato(String email, String nuovoStato) {
        String sql = "UPDATE utenti SET stato = ? WHERE email = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nuovoStato); 
                pstmt.setString(2, email);

                int rows = pstmt.executeUpdate();
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Utente> getUtentiInAttesa() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM utenti WHERE stato = 'in_attesa'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Utente u = new Utente();
                    u.setEmail(rs.getString("email"));
                    u.setNome(rs.getString("nome"));
                    u.setCognome(rs.getString("cognome"));
                    u.setCodiceFiscale(rs.getString("codice_fiscale"));
                    u.setDataRegistrazione(rs.getDate("data_registrazione"));
                    u.setStato(rs.getString("stato"));
                    lista.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public String getDomandaSicurezza(String email) {
        String sql = "SELECT domanda_sicurezza FROM utenti WHERE email = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, email);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("domanda_sicurezza");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRispostaSicurezza(String email) {
        String sql = "SELECT risposta_sicurezza FROM utenti WHERE email = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, email);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("risposta_sicurezza");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String email, String nuovaPasswordHash) {
        String sql = "UPDATE utenti SET password = ? WHERE email = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, nuovaPasswordHash);
                ps.setString(2, email);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUtente(String email) {
        String sql = "DELETE FROM utenti WHERE email = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, email);
                
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}