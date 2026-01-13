package it.bookmarker.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
       
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String codiceFiscale = request.getParameter("codice_fiscale");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("conferma_password");
        
       
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Le password non corrispondono.");
            forwardToRegistration(request, response);
            return;
        }
        
        
        UtenteDAO dao = new UtenteDAO();
        
        
        if (dao.esisteCodiceFiscale(codiceFiscale)) {
            request.setAttribute("errorMessage", "Il codice fiscale inserito è già registrato.");
            forwardToRegistration(request, response);
            return; 
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        
        Utente nuovoUtente = new Utente(nome, cognome, codiceFiscale, email, hashedPassword);

        
        

        try {
            boolean isRegistered = dao.registraUtente(nuovoUtente);
            
            if (isRegistered) {
                response.sendRedirect("login.jsp?reg=success");
            } else {
                throw new SQLException("Creazione utente fallita, nessuna riga modificata.");
            }

        } catch (SQLException e) {
            String errorMsg = "Errore del database: " + e.getMessage();
            
           
            if (e.getErrorCode() == 1062) { 
                errorMsg = "Email già registrata. Prova ad accedere.";
            }
            
            request.setAttribute("errorMessage", errorMsg);
            forwardToRegistration(request, response);
        }
    }

    
    private void forwardToRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
        rd.forward(request, response);
    }
}
