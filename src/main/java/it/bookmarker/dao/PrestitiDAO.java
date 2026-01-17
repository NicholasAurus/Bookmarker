package it.bookmarker.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Prestito;

public class PrestitiDAO {
    
    private String URL = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private String USER = "root";
    private String PASS = "Bookmarker09!";

    public boolean prenotaLibro(String emailUtente, int idLibro, Date dataSceltaDallUtente) {
        String sql = "INSERT INTO prestiti (utente_email, libro_id, data_prenotazione, stato) VALUES (?, ?, ?, 'Richiesto')";
        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, emailUtente);
            pstmt.setInt(2, idLibro);
            pstmt.setDate(3, dataSceltaDallUtente);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace(); 
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public List<Prestito> getStoricoByUtente(String emailUtente) {
        List<Prestito> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.titolo, l.autore, l.copertina, l.descrizione, " +
                     "(SELECT COUNT(*) FROM recensioni r WHERE r.libro_id = p.libro_id AND r.utente_email = p.utente_email) as recensioni_count " +
                     "FROM prestiti p " +
                     "JOIN libri l ON p.libro_id = l.id_libro " + 
                     "WHERE p.utente_email = ? " +
                     "ORDER BY p.id DESC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, emailUtente);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prestito p = new Prestito();
                        p.setId(rs.getInt("id"));
                        p.setUtenteEmail(rs.getString("utente_email"));
                        p.setLibroId(rs.getInt("libro_id"));
                        p.setDataPrenotazione(rs.getDate("data_prenotazione")); 
                        p.setDataInizio(rs.getDate("data_inizio"));
                        p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                        p.setDataRestituzioneEffettiva(rs.getDate("data_restituzione_effettiva"));
                        p.setStato(rs.getString("stato"));
                        p.setMotivazione(rs.getString("motivazione")); 
                        p.setTitoloLibro(rs.getString("titolo"));
                        p.setAutoreLibro(rs.getString("autore"));
                        p.setCopertinaLibro(rs.getString("copertina"));
                        p.setDescrizioneLibro(rs.getString("descrizione"));
                        
                        p.setRecensito(rs.getInt("recensioni_count") > 0);
                        lista.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Prestito> getPrestitiRichiesti() {
        List<Prestito> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.titolo FROM prestiti p JOIN libri l ON p.libro_id = l.id_libro WHERE p.stato = 'Richiesto'";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataPrenotazione(rs.getDate("data_prenotazione")); 
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                    p.setStato(rs.getString("stato"));
                    p.setTitoloLibro(rs.getString("titolo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean gestisciPrestito(int idPrestito, String nuovoStato, String motivazione) {
        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psUpdateLibro = null;
        PreparedStatement psUpdatePrestito = null;
        ResultSet rs = null;
        boolean successo = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            if ("prenotato".equals(nuovoStato)) {
                conn.setAutoCommit(false); 

                String sqlCheck = "SELECT p.libro_id, l.disponibilita FROM prestiti p JOIN libri l ON p.libro_id = l.id_libro WHERE p.id = ?";
                psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setInt(1, idPrestito);
                rs = psCheck.executeQuery();

                if (rs.next()) {
                    int idLibro = rs.getInt("libro_id");
                    int disponibilita = rs.getInt("disponibilita");

                    if (disponibilita > 0) {
                        String sqlUpdateLibro = "UPDATE libri SET disponibilita = disponibilita - 1 WHERE id_libro = ?";
                        psUpdateLibro = conn.prepareStatement(sqlUpdateLibro);
                        psUpdateLibro.setInt(1, idLibro);
                        psUpdateLibro.executeUpdate();

                        String sqlUpdatePrestito = "UPDATE prestiti SET stato = ?, motivazione = ? WHERE id = ?";
                        psUpdatePrestito = conn.prepareStatement(sqlUpdatePrestito);
                        psUpdatePrestito.setString(1, nuovoStato);
                        psUpdatePrestito.setString(2, motivazione);
                        psUpdatePrestito.setInt(3, idPrestito);
                        psUpdatePrestito.executeUpdate();

                        conn.commit();
                        successo = true;
                    } else {
                        conn.rollback();
                        successo = false;
                    }
                }
            } else {
                String sql = "UPDATE prestiti SET stato = ?, motivazione = ? WHERE id = ?";
                psUpdatePrestito = conn.prepareStatement(sql);
                psUpdatePrestito.setString(1, nuovoStato);
                psUpdatePrestito.setString(2, motivazione);
                psUpdatePrestito.setInt(3, idPrestito);
                psUpdatePrestito.executeUpdate();
                successo = true;
            }

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psCheck != null) psCheck.close(); } catch (Exception e) {}
            try { if (psUpdateLibro != null) psUpdateLibro.close(); } catch (Exception e) {}
            try { if (psUpdatePrestito != null) psUpdatePrestito.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return successo;
    }

    public List<Prestito> getPrestitiPrenotati() {
        List<Prestito> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.titolo FROM prestiti p JOIN libri l ON p.libro_id = l.id_libro WHERE p.stato = 'prenotato'"; 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    
                    p.setDataPrenotazione(rs.getDate("data_prenotazione"));
                    
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataFinePrevista(rs.getDate("data_fine_prevista"));
                    p.setStato(rs.getString("stato"));
                    p.setTitoloLibro(rs.getString("titolo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return lista;
    }

    public void confermaRitiro(int idPrestito) {
        String sql = "UPDATE prestiti SET stato = 'In Corso', data_inizio = CURDATE(), data_fine_prevista = DATE_ADD(CURDATE(), INTERVAL 30 DAY) WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idPrestito);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Prestito> getPrestitiAttivi() {
        List<Prestito> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.titolo FROM prestiti p JOIN libri l ON p.libro_id = l.id_libro WHERE p.stato = 'In Corso'";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
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
                    p.setTitoloLibro(rs.getString("titolo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void terminaPrestito(int idPrestito) {
        String sql = "UPDATE prestiti SET stato = 'Restituito', data_restituzione_effettiva = CURDATE() WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idPrestito);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Prestito> getPrestitiRestituiti() {
        List<Prestito> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.titolo FROM prestiti p JOIN libri l ON p.libro_id = l.id_libro WHERE p.stato = 'Restituito' ORDER BY data_restituzione_effettiva DESC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Prestito p = new Prestito();
                    p.setId(rs.getInt("id"));
                    p.setUtenteEmail(rs.getString("utente_email"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setDataInizio(rs.getDate("data_inizio"));
                    p.setDataRestituzioneEffettiva(rs.getDate("data_restituzione_effettiva"));
                    p.setStato(rs.getString("stato"));
                    p.setTitoloLibro(rs.getString("titolo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}