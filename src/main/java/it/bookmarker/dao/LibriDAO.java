package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Libro;

public class LibriDAO {

    
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";

   
    public Libro getLibroById(int id) {
        Libro libro = null;
        
        String sql = "SELECT * FROM libri WHERE id_libro = ?"; 
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, id);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        libro = new Libro();
                       
                        libro.setId(rs.getInt("id_libro"));
                        libro.setTitolo(rs.getString("titolo"));
                        libro.setAutore(rs.getString("autore"));
                        libro.setGenere(rs.getString("genere"));
                        libro.setDisponibilita(rs.getInt("disponibilita"));
                        libro.setDataRientro(rs.getDate("data_rientro"));
                        libro.setDataPubblicazione(rs.getDate("data_pubblicazione"));
                        libro.setDescrizione(rs.getString("descrizione"));
                        libro.setCopertina(rs.getString("copertina"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return libro;
    }
   
    // METODO PER OTTENERE TUTTI I LIBRI
    public List<Libro> getAllLibri() {
        List<Libro> listaLibri = new ArrayList<>();
        String query = "SELECT * FROM libri";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
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