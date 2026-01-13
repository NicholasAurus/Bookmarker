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

import it.bookmarker.dao.UtenteDAO;
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

        UtenteDAO dao = new UtenteDAO();
        List<Utente> utentiInAttesa = dao.getUtentiInAttesa();

        request.setAttribute("listaUtenti", utentiInAttesa);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("approvazione_utenti.jsp");
        dispatcher.forward(request, response);
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String emailUtente = request.getParameter("emailUtente");
        String azione = request.getParameter("azione");
        
        UtenteDAO dao = new UtenteDAO();

        if ("accetta".equals(azione)) {
            dao.updateStato(emailUtente, "attivo");
        } else if ("rifiuta".equals(azione)) {
            dao.updateStato(emailUtente, "rifiutato");
        }

        
        response.sendRedirect("GestioneUtentiServlet");
    }
}