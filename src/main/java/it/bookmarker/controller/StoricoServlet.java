package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;

@WebServlet("/StoricoServlet")
public class StoricoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
       
        int utenteId = 101; 
        
        PrestitiDAO dao = new PrestitiDAO();
        List<Prestito> storico = dao.getStoricoByUtente(utenteId);
        
        request.setAttribute("elencoStorico", storico);
        request.getRequestDispatcher("storico.jsp").forward(request, response);
    }
}