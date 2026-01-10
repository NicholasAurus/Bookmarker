package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRecensioneDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/biblioteca";
    private String jdbcUsername = "root";
    private String jdbcPassword = "Bookmarker09!";

    public boolean salvaRecensione(int idUtente, int idLibro, String titolo, String testo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean rowInserted = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            String sql = "INSERT INTO recensioni (utente_id, libro_id, titolo, testo, data_creazione) VALUES (?, ?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idLibro);
            pstmt.setString(3, titolo);
            pstmt.setString(4, testo);

            rowInserted = pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
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
