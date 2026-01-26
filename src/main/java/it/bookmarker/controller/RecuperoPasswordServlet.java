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
import it.bookmarker.service.exception.UtenteServiceException.*;

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
            try {
                String domanda = service.recuperaDomanda(email);
                session.setAttribute("emailRecupero", email);
                request.setAttribute("domanda", domanda);
                request.getRequestDispatcher("recupero_domanda.jsp").forward(request, response);

            } catch (UtenteNonTrovatoException e) {
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("recupero_email.jsp").forward(request, response);
            }
        }
        
        else if ("verificaRisposta".equals(action)) {
            String email = (String) session.getAttribute("emailRecupero");
            String risposta = request.getParameter("risposta");
            
            try {
                service.verificaRispostaSicurezza(email, risposta);
                
                session.setAttribute("rispostaRecupero", risposta);
                request.getRequestDispatcher("recupero_reset.jsp").forward(request, response);

            } catch (UtenteNonTrovatoException | RispostaSicurezzaErrataException e) {
                try {
                    String domanda = service.recuperaDomanda(email);
                    request.setAttribute("domanda", domanda);
                } catch (UtenteNonTrovatoException ex) {
                    
                }
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("recupero_domanda.jsp").forward(request, response);
            }
        }
        
        else if ("resetFinale".equals(action)) {
            String email = (String) session.getAttribute("emailRecupero");
            String risposta = (String) session.getAttribute("rispostaRecupero");
            String pwd = request.getParameter("password");
            String confPwd = request.getParameter("conferma_password");
            
            try {
                service.resetPassword(email, risposta, pwd, confPwd);
                
                session.removeAttribute("emailRecupero");
                session.removeAttribute("rispostaRecupero");
                response.sendRedirect("login.jsp?msg=resetSuccess");

            } catch (UtenteNonTrovatoException | RispostaSicurezzaErrataException | 
                     FormatoPasswordNonValidoException | PasswordNonCorrispondentiException e) {
                
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("recupero_reset.jsp").forward(request, response);
                
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Errore di sistema.");
                request.getRequestDispatcher("recupero_reset.jsp").forward(request, response);
            }
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("recupero_email.jsp").forward(request, response);
    }
}