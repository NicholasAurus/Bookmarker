package it.bookmarker.dao;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import it.bookmarker.model.Libro;

public class LibriDAO {

    public List<Libro> getLibriDisponibili() {
        List<Libro> listaLibri = new ArrayList<>();
        
        // I tuoi dati di connessione
        String url = "jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC";
        String user = "root";
        String pass = "Bookmarker09!";

        String query = "SELECT titolo, autore, genere FROM libri WHERE disponibilita = TRUE";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    // Crea oggetto Libro con i dati presi dal DB
                    Libro libro = new Libro(
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("genere")
                    );
                    // Lo aggiungiamo alla lista
                    listaLibri.add(libro);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaLibri; // Restituiamo la lista alla Servlet
    }
}
