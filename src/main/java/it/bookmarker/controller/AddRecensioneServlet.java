package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.AddRecensioneDAO;

@WebServlet("/AddRecensioneServlet")
public class AddRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Imposto la codifica per leggere bene accenti ed emoji
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        
        try {
            // Recupero l'utente dalla sessione
            Integer idUtente = (Integer) session.getAttribute("idUtente");
            
            // Se la sessione è scaduta o l'utente non è loggato, lo mando al login
            if (idUtente == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Recupero i parametri dal form (quelli con name="...")
            String idLibroParam = request.getParameter("idLibro");
            String testo = request.getParameter("testo");
            String votoParam = request.getParameter("voto");

            // Controllo che i parametri non siano nulli
            if (idLibroParam != null && votoParam != null) {
                int idLibro = Integer.parseInt(idLibroParam);
                int voto = Integer.parseInt(votoParam);

                // Chiamo il DAO per salvare nel database
                AddRecensioneDAO dao = new AddRecensioneDAO();
                boolean successo = dao.salvaRecensione(idUtente, idLibro, testo, voto);
                
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