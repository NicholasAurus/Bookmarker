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
    
 // 1. RIMUOVI LIBRO (Con pulizia prestiti/recensioni per evitare errori FK)
    public boolean rimuoviLibro(int idLibro) {
        String deleteRec = "DELETE FROM recensioni WHERE libro_id = ?";
        String deletePres = "DELETE FROM prestiti WHERE libro_id = ?";
        String deleteLib = "DELETE FROM libri WHERE id_libro = ?"; // O 'id' se si chiama così
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                conn.setAutoCommit(false); // Transazione manuale

                try (PreparedStatement ps1 = conn.prepareStatement(deleteRec);
                     PreparedStatement ps2 = conn.prepareStatement(deletePres);
                     PreparedStatement ps3 = conn.prepareStatement(deleteLib)) {
                    
                    // Cancella dipendenze
                    ps1.setInt(1, idLibro);
                    ps1.executeUpdate();
                    
                    ps2.setInt(1, idLibro);
                    ps2.executeUpdate();
                    
                    // Cancella libro
                    ps3.setInt(1, idLibro);
                    int rows = ps3.executeUpdate();
                    
                    conn.commit(); // Conferma tutto
                    return rows > 0;
                } catch (SQLException e) {
                    conn.rollback(); // Se errore, annulla tutto
                    throw e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. AGGIUNGI NUOVO LIBRO
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

    // 3. MODIFICA DISPONIBILITÀ
    public boolean aggiornaDisponibilita(int idLibro, int nuoveCopie) {
        String sql = "UPDATE libri SET disponibilita = ? WHERE id_libro = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, nuoveCopie);
                ps.setInt(2, idLibro);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}