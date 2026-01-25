package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
    public List<Libro> getAllLibri() {
        List<Libro> listaLibri = new ArrayList<>();
        
        String query = "SELECT l.*, COALESCE(AVG(r.voto), 0) as media_voti " +
                       "FROM libri l " +
                       "LEFT JOIN recensioni r ON l.id_libro = r.libro_id " +
                       "WHERE l.attivo = 1 " +
                       "GROUP BY l.id_libro";

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
                    
                    libro.setMediaVoti(rs.getDouble("media_voti"));

                    listaLibri.add(libro);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaLibri;
    }
    
    public boolean rimuoviLibro(int idLibro) {
        String sql = "UPDATE libri SET attivo = 0 WHERE id_libro = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, idLibro);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inserisciLibro(Libro l) {
        String sql = "INSERT INTO libri (titolo, autore, genere, disponibilita, data_pubblicazione, descrizione, copertina) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, l.getTitolo());
                ps.setString(2, l.getAutore());
                ps.setString(3, l.getGenere());
                ps.setInt(4, l.getDisponibilita());
                ps.setDate(5, l.getDataPubblicazione());
                ps.setString(6, l.getDescrizione());
                ps.setString(7, l.getCopertina());
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean aggiornaDisponibilita(int id_libro, int nuoveCopie) {
        String sql = "UPDATE libri SET disponibilita = ? WHERE id_libro = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, nuoveCopie);
                ps.setInt(2, id_libro);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getCopieDisponibili(int idLibro) {
        String sql = "SELECT disponibilita FROM libri WHERE id_libro = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, idLibro);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("disponibilita");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }
}