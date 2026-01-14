package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Prestito;

public class PrestitiDAO {
    
    
    private String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private String user = "root";
    private String pass = "Bookmarker09!";

    public List<Prestito> getPrestitiRichiesti() {
        List<Prestito> lista = new ArrayList<>();
        

        String sql = "SELECT * FROM prestiti WHERE stato = 'richiesto'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
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
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, nuovoStato);
                pstmt.setInt(2, idPrestito);
                
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}