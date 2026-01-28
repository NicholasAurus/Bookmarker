package it.bookmarker.system.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseTestHelper {
    
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";

    public static void createUtente(String email, String ruolo, String stato) {
        String randomCF = ("CF" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 16).toUpperCase();
        createUtente(email, ruolo, stato, randomCF);
    }

    public static void createUtente(String email, String ruolo, String stato, String cf) {
        deleteUtente(email); 

        String passwordPura = "Password123!";
        String passwordHash = BCrypt.hashpw(passwordPura, BCrypt.gensalt());

        String sql = "INSERT INTO utenti (email, password, nome, cognome, codice_fiscale, " +
                     "ruolo, stato, data_registrazione, domanda_sicurezza, risposta_sicurezza, recapito) " +
                     "VALUES (?, ?, 'Test', 'User', ?, " +
                     "?, ?, NOW(), 'DomandaTest?', 'RispostaTest', '3330000000')";
        
        executeUpdate(sql, email, passwordHash, cf, ruolo, stato);
    }

    public static void deleteUtente(String email) {
        executeUpdate("DELETE FROM segnalazioni WHERE utente_email = ?", email);
        executeUpdate("DELETE FROM recensioni WHERE utente_email = ?", email);
        executeUpdate("DELETE FROM prestiti WHERE utente_email = ?", email);
        executeUpdate("DELETE FROM utenti WHERE email = ?", email);
    }

    public static void createLibro(int idLibro, String titolo, int disponibilita) {
        deleteLibro(idLibro); 

        String sql = "INSERT INTO libri (id_libro, titolo, autore, genere, disponibilita, " +
                     "data_pubblicazione, descrizione, copertina, attivo) " +
                     "VALUES (?, ?, 'Autore Test', 'Genere Test', ?, " +
                     "CURDATE(), 'Descrizione Test', 'default.jpg', 1)";
        
        executeUpdate(sql, idLibro, titolo, disponibilita);
    }
    

    public static void deleteLibro(int idLibro) {
        executeUpdate("DELETE FROM segnalazioni WHERE recensione_id IN (SELECT id FROM recensioni WHERE libro_id = ?)", idLibro);
        executeUpdate("DELETE FROM recensioni WHERE libro_id = ?", idLibro);
        executeUpdate("DELETE FROM prestiti WHERE libro_id = ?", idLibro);
        executeUpdate("DELETE FROM libri WHERE id_libro = ?", idLibro);
    }

    public static void createPrestito(String emailUtente, int idLibro, String stato) {
        boolean isAttivo = "in_corso".equalsIgnoreCase(stato) || "ATTIVO".equalsIgnoreCase(stato);
        
        String sql = "INSERT INTO prestiti (utente_email, libro_id, stato, data_prenotazione, data_inizio) " +
                     "VALUES (?, ?, ?, CURDATE(), " + 
                     (isAttivo ? "CURDATE()" : "NULL") + ")";
        
        executeUpdate(sql, emailUtente, idLibro, stato);
    }

    private static void executeUpdate(String sql, Object... params) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore DatabaseTestHelper: " + e.getMessage());
        }
    }
}