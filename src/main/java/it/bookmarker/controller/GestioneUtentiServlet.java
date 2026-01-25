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

import it.bookmarker.dao.LibriDAO;
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
        
       
        if (!isBibliotecario(request)) {
            response.sendRedirect("login.jsp"); 
            return;
        }

        HttpSession session = request.getSession();

       
        String errore = (String) session.getAttribute("errore");
        if (errore != null) {
            request.setAttribute("errore", errore);
            session.removeAttribute("errore");
        }

      
        String tabRichiesta = request.getParameter("tab");
        String activeTab = "prestiti".equals(tabRichiesta) ? "prestiti" : "utenti";
        request.setAttribute("activeTab", activeTab);

       
        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        
        UtenteService utenteService = new UtenteService(utenteDao);
        PrestitoService prestitoService = new PrestitoService(prestitoDao, libriDAO);

        List<Utente> utentiInAttesa = utenteService.getUtentiDaApprovare();
        request.setAttribute("listaUtenti", utentiInAttesa);
        
        List<Prestito> prestitiInAttesa = prestitoService.getRichiesteInAttesa();
        request.setAttribute("listaPrestiti", prestitiInAttesa);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("approvazioneUtenti.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // controllo ruolo
        if (!isBibliotecario(request)) {
            response.sendRedirect("login.jsp"); 
            return;
        }
        
        String tipoOperazione = request.getParameter("tipoOperazione"); 
        String azione = request.getParameter("azione"); 
        String tabDaAprire = "utenti"; // Default fallback

        // Inizializzazione Service
        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        UtenteService utenteService = new UtenteService(utenteDao);
        PrestitoService prestitoService = new PrestitoService(prestitoDao, libriDAO);

       
        if ("utente".equals(tipoOperazione)) {
            String emailUtente = request.getParameter("emailUtente");
            
            
            if ("accetta".equals(azione)) {
                utenteService.accettaUtente(emailUtente);
            } else if ("rifiuta".equals(azione)) {
                utenteService.rifiutaUtente(emailUtente);
            }
            
            
            tabDaAprire = "utenti";
        } 
       
        else if ("prestito".equals(tipoOperazione)) {
            String idPrestitoStr = request.getParameter("idPrestito");
            
            if ("accetta".equals(azione)) {
                String esito = prestitoService.approvaRichiestaPrestito(idPrestitoStr);
                

                    request.getSession().setAttribute("errore", esito);

                
            } else if ("rifiuta".equals(azione)) {
                String motivazione = request.getParameter("motivazione");
                prestitoService.rifiutaRichiestaPrestito(idPrestitoStr, motivazione);
            }
            
            tabDaAprire = "prestiti";
        }

        // Redirect per evitare il "Resubmit form" al refresh
        response.sendRedirect("GestioneUtentiServlet?tab=" + tabDaAprire);
    }
    
    
    private boolean isBibliotecario(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false: non creare sessione se non esiste
        if (session == null) return false;
        
        String ruolo = (String) session.getAttribute("ruoloUtente");
        return ruolo != null && ruolo.equalsIgnoreCase("BIBLIOTECARIO");
    }
}