package it.bookmarker.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/RegistrationServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    private String jdbcURL = "jdbc:mysql://localhost:3306/biblioteca";
    private String jdbcUsername = "root";
    private String jdbcPassword = "Bookmarker09!";
 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
       
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String tessera = request.getParameter("tessera");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        
    
        if (!password.equals(confirmPassword)) {
            //èassword non corrispondono: rimanda alla pagina di registrazione con un errore
            request.setAttribute("errorMessage", "Le password non corrispondono.");
            RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
            rd.forward(request, response);
            return; // Termina l'esecuzione
        }
     
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
         
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            //preparedStatement per evitare sql Injection)
            
            String sql = "INSERT INTO utenti (nome, cognome, n_tessera, email, password_hash) VALUES (?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, tessera);
            pstmt.setString(4, email);
            pstmt.setString(5, hashedPassword); //salva password hashata
            
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                
                response.sendRedirect("login.jsp?reg=success");
            } else {
                
                throw new SQLException("Creazione utente fallita, nessuna riga modificata.");
            }

        } catch (SQLException e) {
            //gestisce errori SQL, come un'email duplicata
            String errorMsg = "Errore del database: " + e.getMessage();
            
            
            if (e.getErrorCode() == 1062) { 
                errorMsg = "Email già registrata. Prova ad accedere.";
            }
            
            request.setAttribute("errorMessage", errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
            rd.forward(request, response);
            
        } catch (ClassNotFoundException e) {
            
            throw new ServletException("Driver jdbc non trovato", e);
        } finally {
            
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

