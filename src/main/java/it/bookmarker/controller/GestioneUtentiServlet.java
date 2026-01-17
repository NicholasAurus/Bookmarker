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

        String errore = (String) session.getAttribute("errore");
        if (errore != null) {
            request.setAttribute("errore", errore);
            session.removeAttribute("errore");
        }

        String tabRichiesta = request.getParameter("tab");
        String activeTab = "utenti";

        if ("prestiti".equals(tabRichiesta)) {
            activeTab = "prestiti";
        }

        request.setAttribute("activeTab", activeTab);

        UtenteDAO utenteDao = new UtenteDAO();
        PrestitiDAO prestitoDao = new PrestitiDAO();

        List<Utente> utentiInAttesa = utenteDao.getUtentiInAttesa();
        request.setAttribute("listaUtenti", utentiInAttesa);
        
        List<Prestito> prestitiInAttesa = prestitoDao.getPrestitiRichiesti();
        request.setAttribute("listaPrestiti", prestitiInAttesa);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("approvazioneUtenti.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String tipoOperazione = request.getParameter("tipoOperazione"); 
        String azione = request.getParameter("azione"); 
        String tabDaAprire = "utenti";

        if ("utente".equals(tipoOperazione)) {
            String emailUtente = request.getParameter("emailUtente");
            UtenteDAO dao = new UtenteDAO();

            if ("accetta".equals(azione)) {
                dao.updateStato(emailUtente, "attivo");
            } else if ("rifiuta".equals(azione)) {
                dao.updateStato(emailUtente, "rifiutato");
            }
            tabDaAprire = "utenti";
        } 
        else if ("prestito".equals(tipoOperazione)) {
            try {
                int idPrestito = Integer.parseInt(request.getParameter("idPrestito"));
                PrestitiDAO dao = new PrestitiDAO();
                
                if ("accetta".equals(azione)) {
                    
                    boolean esito = dao.gestisciPrestito(idPrestito, "prenotato", null);
                    if (!esito) {
                        request.getSession().setAttribute("errore", "Impossibile confermare: copie esaurite.");
                    }
                } else if ("rifiuta".equals(azione)) {
                    
                    String motivazione = request.getParameter("motivazione");
                    dao.gestisciPrestito(idPrestito, "rifiutato", motivazione);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            tabDaAprire = "prestiti";
        }

        response.sendRedirect("GestioneUtentiServlet?tab=" + tabDaAprire);
    }
}