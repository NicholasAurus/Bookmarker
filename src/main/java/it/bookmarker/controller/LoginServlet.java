package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

      
        UtenteDAO dao = new UtenteDAO();
        Utente utenteTrovato = dao.getUtenteByEmail(email);
        
        
        System.out.println("Email cercata: " + email);
        if (utenteTrovato == null) {
            System.out.println("ERRORE: Utente non trovato nel DB (l'oggetto è null)");
        } else {
            System.out.println("Utente trovato ID: " + utenteTrovato.getId());
            
        }
    
        
        if (utenteTrovato != null) {
            
            if (BCrypt.checkpw(password, utenteTrovato.getPassword())) {
                
                
                HttpSession session = request.getSession();
                
              
                session.setAttribute("utenteLoggato", utenteTrovato.getNome());
                session.setAttribute("emailUtente", utenteTrovato.getEmail());
                session.setAttribute("ruoloUtente", utenteTrovato.getRuolo());
                
              
                session.setAttribute("idUtente", utenteTrovato.getId());
                
              
                session.setMaxInactiveInterval(30 * 60); 
                
                response.sendRedirect("index.jsp");
                return; 
            }
        }

       
        sendError(request, response, "Email o password non validi.");
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.forward(request, response);
    }
}