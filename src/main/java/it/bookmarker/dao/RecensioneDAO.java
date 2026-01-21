package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.bookmarker.model.Recensione;

public class RecensioneDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Bookmarker09!";
    
    
    public boolean salvaRecensione(String emailUtente, int idLibro, String testo, int voto) {
        boolean esito = false;
        
        
        if (esisteRecensione(emailUtente, idLibro)) {
            
            esito = aggiornaRecensioneEsistente(emailUtente, idLibro, testo, voto);
        } else {
            
            esito = inserisciNuovaRecensione(emailUtente, idLibro, testo, voto);
        }
        
        return esito;
    }

    
    private boolean esisteRecensione(String email, int idLibro) {
        String sql = "SELECT COUNT(*) FROM recensioni WHERE utente_email = ? AND libro_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setInt(2, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

   
    private boolean aggiornaRecensioneEsistente(String email, int idLibro, String testo, int voto) {
        String sql = "UPDATE recensioni SET testo = ?, voto = ?, data_inserimento = NOW() WHERE utente_email = ? AND libro_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, testo);
                ps.setInt(2, voto);
                ps.setString(3, email);
                ps.setInt(4, idLibro);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    
    private boolean inserisciNuovaRecensione(String email, int idLibro, String testo, int voto) {
        String sql = "INSERT INTO recensioni (utente_email, libro_id, testo, data_inserimento, voto) VALUES (?, ?, ?, NOW(), ?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setInt(2, idLibro);
                ps.setString(3, testo);
                ps.setInt(4, voto);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Recensione> getRecensioniByLibro(int idLibro) {
        List<Recensione> lista = new ArrayList<>();
        String sql = "SELECT r.*, u.nome, u.cognome " +
                     "FROM recensioni r " +
                     "JOIN utenti u ON r.utente_email = u.email " +
                     "WHERE r.libro_id = ? AND r.eliminata = 0 " +
                     "ORDER BY r.data_inserimento DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idLibro);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Recensione r = new Recensione();
                        r.setId(rs.getInt("id"));
                        r.setUtenteEmail(rs.getString("utente_email"));
                        r.setLibroId(rs.getInt("libro_id"));
                        r.setTesto(rs.getString("testo"));
                        r.setDataInserimento(rs.getDate("data_inserimento"));
                        r.setVoto(rs.getInt("voto")); 
                        r.setVisibile(rs.getBoolean("visibile"));
                        String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                        r.setNomeUtenteDisplay(nomeCompleto);
                        
                        r.setEliminata(rs.getBoolean("eliminata"));
                        
                        lista.add(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //FUNZIONE PER L'UTENTE
    public boolean deleteRecensioneUtente(String emailUtente, int idLibro) {
        String sql = "DELETE FROM recensioni WHERE utente_email = ? AND libro_id = ?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, emailUtente);
                ps.setInt(2, idLibro);
                
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //FUNZIONE PER IL MODERATORE
    public boolean deleteRecensioneModeratore(int idRecensione) {
        String sql = "UPDATE recensioni SET eliminata = 1 WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, idRecensione);
                int rows = ps.executeUpdate();
                return rows > 0; // Ritorna true se ha cancellato
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cambiaVisibilita(int idRecensione, boolean visibile) {
        String sql = "UPDATE recensioni SET visibile = ? WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setBoolean(1, visibile);
                ps.setInt(2, idRecensione);
                
                int rows = ps.executeUpdate();
                
                // Se rows > 0 ha trovato la recensione e aggiornato lo stato
                return rows > 0;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false;
        }
    }
    
    public List<Recensione> getRecensioniPubbliche(int idLibro) {
        List<Recensione> lista = new ArrayList<>();
        
        String sql = "SELECT r.*, u.nome, u.cognome " +
                     "FROM recensioni r " +
                     "JOIN utenti u ON r.utente_email = u.email " +
                     "WHERE r.libro_id = ? AND r.visibile = true AND r.eliminata = 0 " +  
                     "ORDER BY r.data_inserimento DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idLibro);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Recensione r = new Recensione();
                        r.setId(rs.getInt("id"));
                        r.setUtenteEmail(rs.getString("utente_email"));
                        r.setLibroId(rs.getInt("libro_id"));
                        r.setTesto(rs.getString("testo"));
                        r.setDataInserimento(rs.getDate("data_inserimento"));
                        r.setVoto(rs.getInt("voto"));
                        r.setVisibile(rs.getBoolean("visibile")); 
                        
                        String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                        r.setNomeUtenteDisplay(nomeCompleto);

                        lista.add(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    
    public Recensione getRecensioneByUtenteAndLibro(String email, int idLibro) {
        Recensione r = null;
        String sql = "SELECT * FROM recensioni WHERE utente_email = ? AND libro_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, email);
                ps.setInt(2, idLibro);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        r = new Recensione();
                        r.setId(rs.getInt("id"));
                        r.setUtenteEmail(rs.getString("utente_email"));
                        r.setLibroId(rs.getInt("libro_id"));
                        r.setTesto(rs.getString("testo"));
                        r.setVoto(rs.getInt("voto"));
                        r.setDataInserimento(rs.getDate("data_inserimento"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
    



 public List<Recensione> getAllRecensioniPerModeratore(int idLibro) {
     List<Recensione> lista = new ArrayList<>();
     
     
     String sql = "SELECT r.*, u.nome, u.cognome " +
                  "FROM recensioni r " +
                  "JOIN utenti u ON r.utente_email = u.email " +
                  "WHERE r.libro_id = ? " +  
                  "ORDER BY r.data_inserimento DESC";

     try {
         Class.forName("com.mysql.cj.jdbc.Driver");
         try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
              PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setInt(1, idLibro);

             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     Recensione r = new Recensione();
                     r.setId(rs.getInt("id"));
                     r.setUtenteEmail(rs.getString("utente_email"));
                     r.setLibroId(rs.getInt("libro_id"));
                     r.setTesto(rs.getString("testo"));
                     r.setDataInserimento(rs.getDate("data_inserimento"));
                     r.setVoto(rs.getInt("voto")); 
                     r.setVisibile(rs.getBoolean("visibile"));
                     
                     String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");
                     r.setNomeUtenteDisplay(nomeCompleto);
                     
                     r.setEliminata(rs.getBoolean("eliminata"));
                     
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