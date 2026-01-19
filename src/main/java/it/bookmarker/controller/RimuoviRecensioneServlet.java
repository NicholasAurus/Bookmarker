package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.service.RecensioneService;

@WebServlet("/RimuoviRecensioneServlet")
public class RimuoviRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String emailUtente = (String) session.getAttribute("emailUtente");
        String idLibroParam = request.getParameter("idLibro");

        // Controllo Parametri base 
        if (emailUtente == null || idLibroParam == null) {
            response.sendRedirect("StoricoServlet");
            return;
        }

        // Inizializzazione Service
        RecensioneDAO dao = new RecensioneDAO();
        RecensioneService service = new RecensioneService(dao);

        boolean eliminato = service.deleteRecensioneUtente(emailUtente, idLibroParam);
        
        response.sendRedirect("StoricoServlet");
    }
}