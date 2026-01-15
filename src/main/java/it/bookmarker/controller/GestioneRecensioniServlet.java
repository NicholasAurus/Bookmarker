package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.bookmarker.dao.RecensioneDAO;

@WebServlet("/GestioneRecensioniServlet")
public class GestioneRecensioniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
  
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || (!ruolo.equalsIgnoreCase("MODERATORE") && !ruolo.equalsIgnoreCase("BIBLIOTECARIO"))) {
            response.sendRedirect("login.jsp");
            return;
        }

       
        String azione = request.getParameter("azione");
        String idRecensioneStr = request.getParameter("idRecensione");
        String idLibroStr = request.getParameter("idLibro");

       
        if (idRecensioneStr != null) {
            try {
                int idRecensione = Integer.parseInt(idRecensioneStr);
                RecensioneDAO dao = new RecensioneDAO();

                if ("rimuovi".equals(azione)) {
                  
                    dao.deleteRecensione(idRecensione);
                } 
                else if ("nascondi".equals(azione)) {
                    
                    dao.cambiaVisibilita(idRecensione, false);
                } 
                else if ("mostra".equals(azione)) {
                  
                    dao.cambiaVisibilita(idRecensione, true);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    
        if (idLibroStr != null) {
            response.sendRedirect("DettaglioLibroModeratoreServlet?id=" + idLibroStr);
        } else {
            response.sendRedirect("CatalogoModeratoreServlet");
        }
    }
}