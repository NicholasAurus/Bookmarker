package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRecensioneDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String jdbcUsername = "root";
    private String jdbcPassword = "Bookmarker09!";

    public boolean salvaRecensione(String emailUtente, int idLibro, String testo, int voto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean rowInserted = false;

        System.out.println("[DEBUG DAO] Inizio salvataggio recensione...");
        System.out.println("[DEBUG DAO] Dati ricevuti -> Utente: " + emailUtente + ", Libro: " + idLibro + ", Voto: " + voto);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            if (conn != null) {
                System.out.println("[DEBUG DAO] Connessione al DB riuscita!");
            }

            String sql = "INSERT INTO recensioni (utente_email, libro_id, testo, data_inserimento, voto) VALUES (?, ?, ?, NOW(), ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emailUtente);
            pstmt.setInt(2, idLibro);
            pstmt.setString(3, testo);
            pstmt.setInt(4, voto);

            int rows = pstmt.executeUpdate();
            rowInserted = (rows > 0);
            
            if (rowInserted) {
                System.out.println("[DEBUG DAO] INSERT eseguito con successo! Righe inserite: " + rows);
            } else {
                System.out.println("[DEBUG DAO] INSERT fallito, nessuna riga aggiunta.");
            }

        } catch (SQLException e) {
            System.out.println("[DEBUG DAO] ERRORE SQL GRAVE:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("[DEBUG DAO] ERRORE DRIVER:");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rowInserted;
    }
}