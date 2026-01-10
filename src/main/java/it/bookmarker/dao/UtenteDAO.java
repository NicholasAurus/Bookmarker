package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.bookmarker.model.Utente;

public class UtenteDAO {

    private String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String user = "root";
    private String pass = "Bookmarker09!";

    
    public boolean registraUtente(Utente utente) throws SQLException {
        String sql = "INSERT INTO utenti (nome, cognome, n_tessera, email, password) VALUES (?, ?, ?, ?, ?)";
        
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
            pstmt.setString(3, utente.getNumeroTessera());
            pstmt.setString(4, utente.getEmail());
            pstmt.setString(5, utente.getPassword());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean esisteTessera(String numeroTessera) {
        boolean esiste = false;
        String sql = "SELECT 1 FROM utenti WHERE n_tessera = ?"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, numeroTessera);

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
                        
                        // Ora funziona perché abbiamo fatto SELECT *
                        // Controlla se nel DB la colonna si chiama "id" o "id_utente"
                        utente.setId(rs.getInt("id")); 
                        
                        utente.setNome(rs.getString("nome"));
                        // utente.setCognome(rs.getString("cognome")); // Opzionale se ti serve
                        utente.setEmail(rs.getString("email"));
                        utente.setRuolo(rs.getString("ruolo"));
                        utente.setPassword(rs.getString("password"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return utente; 
    }
}