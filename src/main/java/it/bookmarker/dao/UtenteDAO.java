package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import it.bookmarker.model.Utente;

public class UtenteDAO {

    // Dati connessione (come nel LibriDAO)
    private String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String user = "root";
    private String pass = "Bookmarker09!";

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
                        // Creiamo l'oggetto Utente con i dati dal DB
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

        return utente; // Restituisce l'oggetto Utente o null se non trovato
    }
}