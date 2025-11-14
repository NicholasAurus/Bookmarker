package it.bookmarker.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// crypt passw
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // db
    private String jdbcURL = "jdbc:mysql://localhost:3306/biblioteca";
    private String jdbcUsername = "root";
    private String jdbcPassword = "Bookmarker09!";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            
            String sql = "SELECT * FROM utenti WHERE email = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            
            // controlla se l'utente esiste
            if (rs.next()) {
                // poi password.
                String storedHashedPassword = rs.getString("password_hash");
                
                // password con jBCrypt
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    
                    //crea sessione utente
                    HttpSession session = request.getSession();
                    session.setAttribute("utenteLoggato", rs.getString("nome")); // Salviamo il nome
                    session.setAttribute("emailUtente", rs.getString("email")); // E l'email
                    
                    //timeout sessione 30 minuti
                    session.setMaxInactiveInterval(30 * 60); 
                    
                    
                    response.sendRedirect("index.jsp");
                    
                } else {
                    // password errata
                    sendError(request, response, "Email o password non validi.");
                }
            } else {
                // email non trovata
                sendError(request, response, "Email o password non validi.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(request, response, "Errore del database. Riprova più tardi.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ServletException("Driver jdbc non trovato", e);
        } finally {
            //chiusura risorse
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void sendError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        
        request.setAttribute("errorMessage", message);
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.forward(request, response);
    }
}