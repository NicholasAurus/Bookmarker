package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.service.PrestitoService;

@WebServlet("/GestionePrestitiServlet")
public class GestionePrestitiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        
        if (ruolo == null || !ruolo.equalsIgnoreCase("BIBLIOTECARIO")) {
            response.sendRedirect("login.jsp");
            return;
        }

        //Gestione Tab Attivo
        String tabRichiesta = request.getParameter("tab");
        String activeTab = "prenotati"; //default

        if ("attivi".equals(tabRichiesta)) {
            activeTab = "attivi";
        } else if ("restituiti".equals(tabRichiesta)) {
            activeTab = "restituiti";
        }
        request.setAttribute("activeTab", activeTab);

        //Inizializzazione Service
        PrestitiDAO dao = new PrestitiDAO();
        PrestitoService service = new PrestitoService(dao);
        
        //Recupero Dati tramite Service
        List<Prestito> listaPrenotati = service.getPrenotati();
        List<Prestito> listaAttivi = service.getAttivi();
        List<Prestito> listaRestituiti = service.getRestituiti();
        
        //Invio alla JSP
        request.setAttribute("listaPrenotati", listaPrenotati);
        request.setAttribute("listaAttivi", listaAttivi);
        request.setAttribute("listaRestituiti", listaRestituiti);
        
        request.getRequestDispatcher("gestionePrestiti.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        //Parametri per il dispatching
        String azione = request.getParameter("azione");
        String idStr = request.getParameter("idPrestito");
        String tabDaAprire = "prenotati"; // Default redirect

        //Service
        PrestitiDAO dao = new PrestitiDAO();
        PrestitoService service = new PrestitoService(dao);

        //Logica azioni
        if (idStr != null && azione != null) {
            
            if ("ritiro".equals(azione)) {
                service.confermaRitiro(idStr);
                tabDaAprire = "prenotati"; //Rimaniamo qui per vederne altri
                
            } else if ("annulla".equals(azione)) {
                String motivazione = request.getParameter("motivazione");
                service.annullaPrestito(idStr, motivazione);
                tabDaAprire = "prenotati";
                
            } else if ("restituzione".equals(azione)) {
                service.registraRestituzione(idStr);
                tabDaAprire = "attivi"; //per tornare agli attivi dopo una restituzione
            }
        }

        response.sendRedirect("GestionePrestitiServlet?tab=" + tabDaAprire);
    }
}