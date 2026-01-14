package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.bookmarker.dao.SegnalazioniDAO;
import it.bookmarker.model.Segnalazione;

@WebServlet("/SegnalazioniServlet")
public class SegnalazioniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        
        if (session == null || session.getAttribute("emailUtente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || !ruolo.equalsIgnoreCase("MODERATORE")) {
             response.sendRedirect("index.jsp"); 
             return;
        }

        SegnalazioniDAO dao = new SegnalazioniDAO();
        List<Segnalazione> lista = dao.getAllSegnalazioni();
        
        request.setAttribute("elencoSegnalazioni", lista);
        request.getRequestDispatcher("segnalazioni.jsp").forward(request, response);
    }
}