package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;

@WebServlet("/ProfiloServlet")
public class ProfiloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer idUtente = (Integer) session.getAttribute("idUtente");
        
       
        if (idUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
      
        UtenteDAO dao = new UtenteDAO();
        Utente utenteCompleto = dao.getUtenteById(idUtente);
        
      
        request.setAttribute("datiUtente", utenteCompleto);
        request.getRequestDispatcher("profilo.jsp").forward(request, response);
    }
}