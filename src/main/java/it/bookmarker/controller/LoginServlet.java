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

        if (utenteTrovato == null || !BCrypt.checkpw(password, utenteTrovato.getPassword())) {
            sendError(request, response, "Email o password non validi.");
            return; 
        }

        HttpSession session = request.getSession();
        
        session.setAttribute("utenteLoggato", utenteTrovato.getNome());
        session.setAttribute("emailUtente", utenteTrovato.getEmail());
        session.setAttribute("idUtente", utenteTrovato.getId());

        String ruoloDB = utenteTrovato.getRuolo();
        if (ruoloDB != null) {
            session.setAttribute("ruoloUtente", ruoloDB.toUpperCase());
        } else {
            session.setAttribute("ruoloUtente", "UTENTE"); // Valore di default se nullo
        }
        
        session.setMaxInactiveInterval(30 * 60); // 30 minuti

        response.sendRedirect("index.jsp");
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.forward(request, response);
    }
}