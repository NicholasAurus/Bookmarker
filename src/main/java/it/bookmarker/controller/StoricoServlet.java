package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;

@WebServlet("/StoricoServlet")
public class StoricoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        
        String emailUtente = (String) session.getAttribute("emailUtente");
        
        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return; 
        }
        
        PrestitiDAO dao = new PrestitiDAO();
        
        List<Prestito> storico = dao.getStoricoByUtente(emailUtente);
        
        request.setAttribute("elencoStorico", storico);
        request.getRequestDispatcher("storico.jsp").forward(request, response);
    }
}