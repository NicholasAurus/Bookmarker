package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Libro;

public class LibriDAO {

   
    public List<Libro> getAllLibri() {
        List<Libro> listaLibri = new ArrayList<>();
        
        String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
        String user = "root";
        String pass = "Bookmarker09!";

        // anche quelli non disponibili
        String query = "SELECT * FROM libri";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    
                    Libro libro = new Libro();
                    
                    
                    libro.setId(rs.getInt("id_libro")); 
                    libro.setTitolo(rs.getString("titolo"));
                    libro.setAutore(rs.getString("autore"));
                    libro.setGenere(rs.getString("genere"));
                    
                    
                    libro.setDisponibilita(rs.getInt("disponibilita")); 
                    libro.setDataRientro(rs.getDate("data_rientro"));   
                    libro.setDataPubblicazione(rs.getDate("data_pubblicazione"));
                    libro.setDescrizione(rs.getString("descrizione"));
                    libro.setCopertina(rs.getString("copertina"));

                    
                    listaLibri.add(libro);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaLibri;
    }
}