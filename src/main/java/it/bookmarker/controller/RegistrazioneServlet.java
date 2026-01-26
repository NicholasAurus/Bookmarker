package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.service.UtenteService;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.UtenteServiceException.*;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String codiceFiscale = request.getParameter("codice_fiscale");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("conferma_password");
        String domanda = request.getParameter("domanda");
        String risposta = request.getParameter("risposta");
        
        UtenteDAO dao = new UtenteDAO(); 
        UtenteService service = new UtenteService(dao);

        try {
            service.registraUtente(nome, cognome, codiceFiscale, email, password, confirmPassword, domanda, risposta);

            response.sendRedirect("login.jsp?reg=success");

        } catch (FormatoDatiNonValidoException | FormatoPasswordNonValidoException | PasswordNonCorrispondentiException | EmailGiaRegistrataException | CodiceFiscaleGiaRegistratoException e) {
            
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("oldNome", nome);
            request.setAttribute("oldCognome", cognome);
            request.setAttribute("oldCodiceFiscale", codiceFiscale);
            request.setAttribute("oldEmail", email);
            
            RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore tecnico del sistema. Riprova più tardi.");
            
            request.setAttribute("oldNome", nome);
            request.setAttribute("oldCognome", cognome);
            request.setAttribute("oldCodiceFiscale", codiceFiscale);
            request.setAttribute("oldEmail", email);

            RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
            rd.forward(request, response);
        }
    }
}