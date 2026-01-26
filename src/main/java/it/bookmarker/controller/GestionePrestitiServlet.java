package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.service.PrestitoService;
import it.bookmarker.service.exception.GenericException.FormatoDatiNonValidoException;
import it.bookmarker.service.exception.LibroServiceException.LibroNonTrovatoException;
import it.bookmarker.service.exception.PrestitoServiceException.PrestitoNonTrovatoException;
import it.bookmarker.service.exception.PrestitoServiceException.StatoPrestitoNonValidoException;

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

        // Gestione Messaggi di Errore da Redirect (doPost)
        String errore = (String) session.getAttribute("errore");
        if (errore != null) {
            request.setAttribute("errore", errore);
            session.removeAttribute("errore");
        }

        // Gestione Tab Attivo
        String tabRichiesta = request.getParameter("tab");
        String activeTab = "prenotati"; // default

        if ("attivi".equals(tabRichiesta)) {
            activeTab = "attivi";
        } else if ("restituiti".equals(tabRichiesta)) {
            activeTab = "restituiti";
        }
        request.setAttribute("activeTab", activeTab);

        // Inizializzazione Service
        PrestitiDAO prestitiDAO = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        PrestitoService service = new PrestitoService(prestitiDAO, libriDAO);
        
        // Recupero Dati tramite Service
        List<Prestito> listaPrenotati = service.getPrenotati();
        List<Prestito> listaAttivi = service.getAttivi();
        List<Prestito> listaRestituiti = service.getRestituiti();
        
        // Invio alla JSP
        request.setAttribute("listaPrenotati", listaPrenotati);
        request.setAttribute("listaAttivi", listaAttivi);
        request.setAttribute("listaRestituiti", listaRestituiti);
        
        request.getRequestDispatcher("gestionePrestiti.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione");
        String idStr = request.getParameter("idPrestito");
        String tabDaAprire = "prenotati"; 

        PrestitiDAO prestitiDAO = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        PrestitoService service = new PrestitoService(prestitiDAO, libriDAO);
        HttpSession session = request.getSession();

        if (idStr != null && azione != null) {
            
            if ("ritiro".equals(azione)) {
                try {
                    service.confermaRitiro(idStr);
                } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | StatoPrestitoNonValidoException e) {
                    session.setAttribute("errore", e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errore", "Errore di sistema durante la conferma del ritiro.");
                }
                tabDaAprire = "prenotati"; 
                
            } else if ("annulla".equals(azione)) {
                String motivazione = request.getParameter("motivazione");
                
                try {
                    service.annullaPrestito(idStr, motivazione);
                } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | 
                         StatoPrestitoNonValidoException | LibroNonTrovatoException e) {
                    session.setAttribute("errore", e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errore", "Errore di sistema durante l'annullamento.");
                }
                
                tabDaAprire = "prenotati";
                
            } else if ("restituzione".equals(azione)) {
                try {
                    service.registraRestituzione(idStr);
                } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | 
                         StatoPrestitoNonValidoException | LibroNonTrovatoException e) {
                    session.setAttribute("errore", e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("errore", "Errore di sistema durante la registrazione della restituzione.");
                }
                
                tabDaAprire = "attivi"; 
            }
        }

        response.sendRedirect("GestionePrestitiServlet?tab=" + tabDaAprire);
    }
}