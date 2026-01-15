package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import it.bookmarker.dao.RecensioneDAO;

@WebServlet("/AddRecensioneServlet")
public class AddRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        
        try {
            String emailUtente = (String) session.getAttribute("emailUtente");
            
            if (emailUtente == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String idLibroParam = request.getParameter("idLibro");
            String testo = request.getParameter("testo");
            String votoParam = request.getParameter("voto");

            if (idLibroParam != null && votoParam != null) {
                int idLibro = Integer.parseInt(idLibroParam);
                int voto = Integer.parseInt(votoParam);

                RecensioneDAO dao = new RecensioneDAO();
                boolean successo = dao.salvaRecensione(emailUtente, idLibro, testo, voto);
                
                if (successo) {
                    System.out.println("Recensione salvata correttamente!");
                } else {
                    System.out.println("Errore nel salvataggio della recensione.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect("StoricoServlet");
    }
}