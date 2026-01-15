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

@WebServlet("/GestionePrestitiServlet")
public class GestionePrestitiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
      
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || !ruolo.equalsIgnoreCase("BIBLIOTECARIO")) {
            response.sendRedirect("login.jsp");
            return;
        }

        PrestitiDAO dao = new PrestitiDAO();
        
      
        List<Prestito> listaPrenotati = dao.getPrestitiPrenotati();
        request.setAttribute("listaPrenotati", listaPrenotati);

       
        List<Prestito> listaAttivi = dao.getPrestitiAttivi();
        request.setAttribute("listaAttivi", listaAttivi);
        
        List<Prestito> listaRestituiti = dao.getPrestitiRestituiti();
        request.setAttribute("listaRestituiti", listaRestituiti);
        
        request.getRequestDispatcher("gestionePrestiti.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione");
        
        try {
            
            String idStr = request.getParameter("idPrestito");
            if (idStr != null) {
                int idPrestito = Integer.parseInt(idStr);
                PrestitiDAO dao = new PrestitiDAO();

                if ("ritiro".equals(azione)) {
                    
                    dao.confermaRitiro(idPrestito);
                    
                } else if ("annulla".equals(azione)) {
                
                    dao.gestisciPrestito(idPrestito, "annullato");
                    
                } else if ("restituzione".equals(azione)) {
                    
                    dao.terminaPrestito(idPrestito);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); 
        }

        
        response.sendRedirect("GestionePrestitiServlet");
    }
}