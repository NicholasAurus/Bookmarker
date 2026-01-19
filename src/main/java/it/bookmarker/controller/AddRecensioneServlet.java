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

@WebServlet("/AddRecensioneServlet")
public class AddRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        String emailUtente = (String) session.getAttribute("emailUtente");
        
        
        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero parametri
        String idLibroParam = request.getParameter("idLibro");
        String testo = request.getParameter("testo");
        String votoParam = request.getParameter("voto");

        // Chiamo il Service
        RecensioneDAO dao = new RecensioneDAO();
        RecensioneService service = new RecensioneService(dao);
        
        boolean successo = service.aggiungiRecensione(emailUtente, idLibroParam, testo, votoParam);
        
        response.sendRedirect("StoricoServlet");
    }
}