package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Recensione;

public class RecensioneDAO {
    
    
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";

    public List<Recensione> getRecensioniByLibro(int idLibro) {
        List<Recensione> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM recensioni WHERE libro_id = ? ORDER BY data_inserimento DESC";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, idLibro);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Recensione r = new Recensione();
                        r.setId(rs.getInt("id")); 
                        r.setUtenteId(rs.getInt("utente_id"));
                        r.setLibroId(rs.getInt("libro_id"));
                        r.setTesto(rs.getString("testo"));
                        r.setDataInserimento(rs.getDate("data_inserimento"));
                        
                        
                        r.setNomeUtenteDisplay("Utente #" + r.getUtenteId());
                        
                        lista.add(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}