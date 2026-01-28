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
import it.bookmarker.service.exception.GenericException.FormatoDatiNonValidoException;
import it.bookmarker.service.exception.PrestitoServiceException.CopieNonDisponibiliException;
import it.bookmarker.service.exception.PrestitoServiceException.PrestitoNonTrovatoException;
import it.bookmarker.service.exception.PrestitoServiceException.StatoPrestitoNonValidoException;
import it.bookmarker.service.exception.UtenteServiceException.StatoUtenteNonValidoException;
import it.bookmarker.service.exception.UtenteServiceException.UtenteNonTrovatoException;

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
        
        if (!isBibliotecario(request)) {
            response.sendRedirect("login.jsp"); 
            return;
        }
        
        String tipoOperazione = request.getParameter("tipoOperazione"); 
        String azione = request.getParameter("azione"); 
        String tabDaAprire = "utenti"; 
        String msgParam = ""; // Variabile per il messaggio Toast

        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        UtenteService utenteService = new UtenteService(utenteDao);
        PrestitoService prestitoService = new PrestitoService(prestitoDao, libriDAO);

        
        if ("utente".equals(tipoOperazione)) {
            String emailUtente = request.getParameter("emailUtente");
            tabDaAprire = "utenti";
            
            try {
                if ("accetta".equals(azione)) {
                    utenteService.accettaUtente(emailUtente);
                    msgParam = "&msg=user_ok"; // Successo attivazione
                } else if ("rifiuta".equals(azione)) {
                    utenteService.rifiutaUtente(emailUtente);
                    msgParam = "&msg=user_ko"; // Successo rifiuto
                }
            } catch (FormatoDatiNonValidoException | UtenteNonTrovatoException | StatoUtenteNonValidoException e) {
                request.getSession().setAttribute("errore", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errore", "Errore imprevisto durante l'operazione.");
            }
        } 
        
        else if ("prestito".equals(tipoOperazione)) {
            String idPrestitoStr = request.getParameter("idPrestito");
            tabDaAprire = "prestiti";
            
            try {
                if ("accetta".equals(azione)) {
                    prestitoService.approvaRichiestaPrestito(idPrestitoStr);
                    msgParam = "&msg=loan_ok"; // Successo approvazione prestito
                } else if ("rifiuta".equals(azione)) {
                    String motivazione = request.getParameter("motivazione");
                    prestitoService.rifiutaRichiestaPrestito(idPrestitoStr, motivazione);
                    msgParam = "&msg=loan_ko"; // Successo rifiuto prestito
                }
            } catch (FormatoDatiNonValidoException | PrestitoNonTrovatoException | 
                     StatoPrestitoNonValidoException | CopieNonDisponibiliException e) {
                request.getSession().setAttribute("errore", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errore", "Errore tecnico durante l'operazione.");
            }
        }

      
        response.sendRedirect("GestioneUtentiServlet?tab=" + tabDaAprire + msgParam);
    }
    
    private boolean isBibliotecario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String ruolo = (String) session.getAttribute("ruoloUtente");
        return ruolo != null && ruolo.equalsIgnoreCase("BIBLIOTECARIO");
    }
}