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

        // Chiamo il DAO per cercare l'utente tramite email
        UtenteDAO dao = new UtenteDAO();
        Utente utenteTrovato = dao.getUtenteByEmail(email);
        System.out.println("Email cercata: " + email);
        if (utenteTrovato == null) {
            System.out.println("ERRORE: Utente non trovato nel DB (l'oggetto è null)");
        } else {
            System.out.println("Utente trovato: " + utenteTrovato.getEmail());
            System.out.println("Hash nel DB: " + utenteTrovato.getPassword());
            System.out.println("Password inserita nel form: " + password);
            boolean check = BCrypt.checkpw(password, utenteTrovato.getPassword());
            System.out.println("Risultato BCrypt.checkpw: " + check);
        }
        // --- FINE DEBUG ---

        
        if (utenteTrovato != null) {
            // controllo password
            if (BCrypt.checkpw(password, utenteTrovato.getPassword())) {
                
                // login riuscito, sessione
                HttpSession session = request.getSession();
                session.setAttribute("utenteLoggato", utenteTrovato.getNome());
                session.setAttribute("emailUtente", utenteTrovato.getEmail());
                session.setAttribute("ruoloUtente", utenteTrovato.getRuolo());
                
                // Timeout sessione 30 minuti
                session.setMaxInactiveInterval(30 * 60); 
                
                response.sendRedirect("index.jsp");
                return; 
            }
        }

        // l'utente è null o la password è sbagliata
        sendError(request, response, "Email o password non validi.");
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.forward(request, response);
    }
}