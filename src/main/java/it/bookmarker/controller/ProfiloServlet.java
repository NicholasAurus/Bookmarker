package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;
import it.bookmarker.service.UtenteService;

@WebServlet("/ProfiloServlet")
public class ProfiloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String emailUtente = (String) session.getAttribute("emailUtente");

        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // inizializzazione Service
        UtenteDAO dao = new UtenteDAO();
        UtenteService service = new UtenteService(dao);
        
        // Recupero Dati
        Utente utenteCompleto = service.getDatiUtente(emailUtente);
        
        // Invio alla JSP
        request.setAttribute("datiUtente", utenteCompleto);
        request.getRequestDispatcher("profilo.jsp").forward(request, response);
    }
}