package it.bookmarker.dao;
//commit
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.bookmarker.model.Utente;

public class UtenteDAO {

<<<<<<< HEAD
   
=======
    // Dati connessione 
>>>>>>> branch 'master' of https://github.com/NicholasAurus/Bookmarker.git
    private String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String user = "root";
    private String pass = "Bookmarker09!";

    
    public boolean registraUtente(Utente utente) throws SQLException {
        String sql = "INSERT INTO utenti (nome, cognome, n_tessera, email, password_hash) VALUES (?, ?, ?, ?, ?)";
        
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
    
    
    
    public Utente getUtenteByEmail(String email) {
        Utente utente = null;
        String query = "SELECT nome, email, ruolo, password_hash FROM utenti WHERE email = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
<<<<<<< HEAD
                   
=======
                        // oggetto Utente con i dati dal DB
>>>>>>> branch 'master' of https://github.com/NicholasAurus/Bookmarker.git
                        utente = new Utente(
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("ruolo"),
                            rs.getString("password_hash")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
        return utente; 
=======
        return utente; // Restituisce Utente o null se non trovato
>>>>>>> branch 'master' of https://github.com/NicholasAurus/Bookmarker.git
    }
}