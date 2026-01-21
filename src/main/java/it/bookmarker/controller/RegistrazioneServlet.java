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

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        //parametri
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

        //chiama il Service
        String errore = service.registraUtente(nome, cognome, codiceFiscale, email, password, confirmPassword, domanda, risposta);

        
        if (errore == null) {
            //ok
            response.sendRedirect("login.jsp?reg=success");
        } else {
            //fail
            request.setAttribute("errorMessage", errore);
            RequestDispatcher rd = request.getRequestDispatcher("registrazione.jsp");
            rd.forward(request, response);
        }
    }
}