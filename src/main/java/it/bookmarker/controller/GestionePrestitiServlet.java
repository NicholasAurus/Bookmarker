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

@WebServlet("/GestionePrestitiServlet") public class GestionePrestitiServlet extends HttpServlet { private static final long serialVersionUID = 1L;

protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    HttpSession session = request.getSession();
    String ruolo = (String) session.getAttribute("ruoloUtente");
    
    if (ruolo == null || !ruolo.equalsIgnoreCase("BIBLIOTECARIO")) {
        response.sendRedirect("login.jsp");
        return;
    }

    String tabRichiesta = request.getParameter("tab");
    String activeTab = "prenotati"; 

    if ("attivi".equals(tabRichiesta)) {
        activeTab = "attivi";
    } else if ("restituiti".equals(tabRichiesta)) {
        activeTab = "restituiti";
    }
    request.setAttribute("activeTab", activeTab);

    PrestitiDAO prestitiDAO = new PrestitiDAO();
    LibriDAO libriDAO = new LibriDAO();
    PrestitoService service = new PrestitoService(prestitiDAO, libriDAO);
    
    List<Prestito> listaPrenotati = service.getPrenotati();
    List<Prestito> listaAttivi = service.getAttivi();
    List<Prestito> listaRestituiti = service.getRestituiti();
    
    request.setAttribute("listaPrenotati", listaPrenotati);
    request.setAttribute("listaAttivi", listaAttivi);
    request.setAttribute("listaRestituiti", listaRestituiti);
    
    request.getRequestDispatcher("gestionePrestiti.jsp").forward(request, response);
}

protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

    HttpSession session = request.getSession();
    String ruolo = (String) session.getAttribute("ruoloUtente");
    
    if (ruolo == null || !ruolo.equalsIgnoreCase("BIBLIOTECARIO")) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String azione = request.getParameter("azione");
    String idStr = request.getParameter("idPrestito");
    String tabDaAprire = "prenotati"; 

    PrestitiDAO prestitiDAO = new PrestitiDAO();
    LibriDAO libriDAO = new LibriDAO();
    PrestitoService service = new PrestitoService(prestitiDAO, libriDAO);

    if (idStr != null && azione != null) {
        
        if ("ritiro".equals(azione)) {
            try {
                service.confermaRitiro(idStr);
                session.setAttribute("successMessage", "Ritiro confermato! Il prestito è ora attivo.");
            } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | StatoPrestitoNonValidoException e) {
                session.setAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Errore di sistema durante la conferma del ritiro.");
            }
            tabDaAprire = "prenotati"; 
            
        } else if ("annulla".equals(azione)) {
            String motivazione = request.getParameter("motivazione");
            
            try {
                service.annullaPrestito(idStr, motivazione);
                session.setAttribute("successMessage", "Prenotazione annullata correttamente.");
            } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | 
                     StatoPrestitoNonValidoException | LibroNonTrovatoException e) {
                session.setAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Errore di sistema durante l'annullamento.");
            }
            
            tabDaAprire = "prenotati";
            
        } else if ("restituzione".equals(azione)) {
            try {
                service.registraRestituzione(idStr);
                session.setAttribute("successMessage", "Libro restituito con successo!");
            } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | 
                     StatoPrestitoNonValidoException | LibroNonTrovatoException e) {
                session.setAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Errore di sistema durante la registrazione della restituzione.");
            }
            
            tabDaAprire = "attivi"; 
        }
    }

    response.sendRedirect("GestionePrestitiServlet?tab=" + tabDaAprire);
}
}