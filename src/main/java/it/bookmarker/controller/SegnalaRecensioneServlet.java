package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.bookmarker.dao.SegnalazioniDAO;


@WebServlet("/SegnalaRecensioneServlet")
public class SegnalaRecensioneServlet extends HttpServlet { 
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
    
        String emailUtente = (String) session.getAttribute("emailUtente");
        
        String idLibroStr = request.getParameter("idLibro");

        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idRecensioneStr = request.getParameter("idRecensione");
        String motivo = request.getParameter("motivo");

        if (idRecensioneStr != null && motivo != null && idLibroStr != null) {
            
            if (motivo.trim().length() < 20) {
                response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr + "&error=motivoBreve");
                return;
            }

            try {
                int idRecensione = Integer.parseInt(idRecensioneStr);
                
                SegnalazioniDAO dao = new SegnalazioniDAO();
                boolean successo = dao.inserisciSegnalazione(idRecensione, emailUtente, motivo);
                
                if(successo) {
                    response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr + "&msg=segnalazioneOk");
                } else {
                    response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr + "&error=db_error");
                }
                return;

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        if (idLibroStr != null) {
            response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr);
        } else {
            response.sendRedirect("LibriServlet");
        }
    }
}