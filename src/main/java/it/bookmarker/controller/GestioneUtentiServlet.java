package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.model.Utente;
import it.bookmarker.service.PrestitoService;
import it.bookmarker.service.UtenteService;

@WebServlet("/GestioneUtentiServlet")
public class GestioneUtentiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");

        if (ruolo == null || !ruolo.equalsIgnoreCase("BIBLIOTECARIO")) {
            response.sendRedirect("login.jsp"); 
            return;
        }

        //Gestione Messaggi di Errore 
        String errore = (String) session.getAttribute("errore");
        if (errore != null) {
            request.setAttribute("errore", errore);
            session.removeAttribute("errore");
        }

        //Gestione Tab UI
        String tabRichiesta = request.getParameter("tab");
        String activeTab = "utenti"; //Default
        if ("prestiti".equals(tabRichiesta)) {
            activeTab = "prestiti";
        }
        request.setAttribute("activeTab", activeTab);

        //Inizializzazione Service
        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();
        
        UtenteService utenteService = new UtenteService(utenteDao);
        PrestitoService prestitoService = new PrestitoService(prestitoDao);

        //Recupero Dati
        List<Utente> utentiInAttesa = utenteService.getUtentiDaApprovare();
        request.setAttribute("listaUtenti", utentiInAttesa);
        
        List<Prestito> prestitiInAttesa = prestitoService.getRichiesteInAttesa();
        request.setAttribute("listaPrestiti", prestitiInAttesa);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("approvazioneUtenti.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String tipoOperazione = request.getParameter("tipoOperazione"); 
        String azione = request.getParameter("azione"); 
        String tabDaAprire = "utenti";

        // Inizializzazione Service
        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();
        UtenteService utenteService = new UtenteService(utenteDao);
        PrestitoService prestitoService = new PrestitoService(prestitoDao);

        // LOGICA UTENTI
        if ("utente".equals(tipoOperazione)) {
            String emailUtente = request.getParameter("emailUtente");
            
            // Il service traduce "accetta" in "attivo"
            utenteService.gestisciApprovazioneUtente(emailUtente, azione);
            
            tabDaAprire = "utenti";
        } 
        // LOGICA PRESTITI
        else if ("prestito".equals(tipoOperazione)) {
            String idPrestitoStr = request.getParameter("idPrestito");
            
            if ("accetta".equals(azione)) {
                boolean esito = prestitoService.approvaRichiestaPrestito(idPrestitoStr);
                
                if (!esito) {
                    // Se il service torna false, mancano le copie
                    request.getSession().setAttribute("errore", "Impossibile confermare: copie esaurite");
                }
                
            } else if ("rifiuta".equals(azione)) {
                String motivazione = request.getParameter("motivazione");
                prestitoService.rifiutaRichiestaPrestito(idPrestitoStr, motivazione);
            }
            
            tabDaAprire = "prestiti";
        }

        response.sendRedirect("GestioneUtentiServlet?tab=" + tabDaAprire);
    }
}