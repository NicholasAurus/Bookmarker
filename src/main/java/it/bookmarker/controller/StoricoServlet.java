package it.bookmarker.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.model.Recensione;

@WebServlet("/StoricoServlet")
public class StoricoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String emailUtente = (String) session.getAttribute("emailUtente");
        
        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return; 
        }
        
        PrestitiDAO dao = new PrestitiDAO();
        RecensioneDAO recDao = new RecensioneDAO();
        
        //Recupero lo storico
        List<Prestito> storico = dao.getStoricoByUtente(emailUtente);
        
        // mappa per associare ID Libro -> Oggetto Recensione
        Map<Integer, Recensione> mappaRecensioni = new HashMap<>();
        
        //Per ogni prestito che risulta recensito, carico i dettagli della recensione
        for (Prestito p : storico) {
            if (p.isRecensito()) {
                Recensione r = recDao.getRecensioneByUtenteAndLibro(emailUtente, p.getLibroId());
                if (r != null) {
                    mappaRecensioni.put(p.getLibroId(), r);
                }
            }
        }
        
        request.setAttribute("elencoStorico", storico);
        request.setAttribute("mappaRecensioni", mappaRecensioni); // Passo la mappa alla JSP
        
        request.getRequestDispatcher("storico.jsp").forward(request, response);
    }
}