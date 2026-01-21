package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.service.UtenteService;

@WebServlet("/RecuperoPasswordServlet")
public class RecuperoPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        UtenteDAO dao = new UtenteDAO();
        UtenteService service = new UtenteService(dao);
        HttpSession session = request.getSession();
        
       
        if ("cercaEmail".equals(action)) {
            String email = request.getParameter("email");
            String domanda = service.recuperaDomanda(email);
            
            if (domanda != null) {
                // Trovato! Salviamo l'email in sessione per sicurezza e andiamo allo step 2
                session.setAttribute("emailRecupero", email);
                request.setAttribute("domanda", domanda);
                request.getRequestDispatcher("recupero_domanda.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Email non trovata.");
                request.getRequestDispatcher("recupero_email.jsp").forward(request, response);
            }
        }
        
      
        else if ("verificaRisposta".equals(action)) {
            String email = (String) session.getAttribute("emailRecupero");
            String risposta = request.getParameter("risposta");
            
            if (email != null && service.verificaRispostaSicurezza(email, risposta)) {
                // Risposta corretta! Andiamo allo step 3
                request.getRequestDispatcher("recupero_reset.jsp").forward(request, response);
            } else {
                // Errore: ricarichiamo la domanda per riprovare
                String domanda = service.recuperaDomanda(email);
                request.setAttribute("domanda", domanda);
                request.setAttribute("error", "Risposta errata. Riprova.");
                request.getRequestDispatcher("recupero_domanda.jsp").forward(request, response);
            }
        }
        
  
        else if ("resetFinale".equals(action)) {
            String email = (String) session.getAttribute("emailRecupero");
            String pwd = request.getParameter("password");
            String confPwd = request.getParameter("conferma_password");
            
            if (email != null && service.resetPassword(email, pwd, confPwd)) {
                // Successo: puliamo la sessione e mandiamo al login
                session.removeAttribute("emailRecupero");
                response.sendRedirect("login.jsp?msg=resetSuccess");
            } else {
                request.setAttribute("error", "Errore: password non valida o non coincidenti.");
                request.getRequestDispatcher("recupero_reset.jsp").forward(request, response);
            }
        }
    }
    
    // Gestione GET per caricare la pagina iniziale
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("recupero_email.jsp").forward(request, response);
    }
}