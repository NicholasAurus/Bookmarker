package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.RecensioneDAO;

@WebServlet("/RimuoviRecensioneServlet")
public class RimuoviRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer idUtente = (Integer) session.getAttribute("idUtente");
        String idLibroParam = request.getParameter("idLibro");

        // Se l'utente non è loggato o manca l'ID del libro, torna allo storico
        if (idUtente == null || idLibroParam == null) {
            response.sendRedirect("StoricoServlet");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idLibroParam);
            
            // Chiamo il DAO per eliminare
            RecensioneDAO dao = new RecensioneDAO();
            boolean eliminato = dao.eliminaRecensione(idUtente, idLibro);
            
            if (eliminato) {
                System.out.println("Recensione eliminata con successo.");
            } else {
                System.out.println("Nessuna recensione trovata da eliminare.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Torno allo storico aggiornato
        response.sendRedirect("StoricoServlet");
    }
}