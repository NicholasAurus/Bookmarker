package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.model.Recensione;
import it.bookmarker.service.PrestitoService;
import it.bookmarker.service.RecensioneService;

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
        
        // Inizializzazione Services
        PrestitiDAO prestitoDAO = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        RecensioneDAO recensioneDao = new RecensioneDAO();
        
        PrestitoService prestitoService = new PrestitoService(prestitoDAO, libriDAO);
        RecensioneService recensioneService = new RecensioneService(recensioneDao);
        
        //Recupero Dati
        
        // Recupero lista dei prestiti
        List<Prestito> storico = prestitoService.getStoricoUtente(emailUtente);
        
        // Costruzione mappa delle recensioni usando la lista appena recuperata
        Map<Integer, Recensione> mappaRecensioni = recensioneService.getMappaRecensioniPerStorico(emailUtente, storico);
        
        // Invio alla JSP
        request.setAttribute("elencoStorico", storico);
        request.setAttribute("mappaRecensioni", mappaRecensioni); 
        
        request.getRequestDispatcher("storico.jsp").forward(request, response);
    }
}