package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.SegnalazioniDAO;
import it.bookmarker.service.SegnalazioneService;

@WebServlet("/SegnalaRecensioneServlet")
public class SegnalaRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        String emailUtente = (String) session.getAttribute("emailUtente");
        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Recupero Parametri
        String idLibroStr = request.getParameter("idLibro");
        String idRecensioneStr = request.getParameter("idRecensione");
        String motivo = request.getParameter("motivo");

        // Se manca l'ID libro andiamo al catalogo
        if (idLibroStr == null) {
            response.sendRedirect("LibriServlet");
            return;
        }

        // Inizializzazione Service
        SegnalazioniDAO dao = new SegnalazioniDAO();
        SegnalazioneService service = new SegnalazioneService(dao);

        String risultato = service.segnalaRecensione(idRecensioneStr, emailUtente, motivo);


        String redirectURL = "DettaglioLibroServlet?id=" + idLibroStr;

        if ("successo".equals(risultato)) {
            redirectURL += "&msg=segnalazioneOk";
            
        } else if ("motivo_breve".equals(risultato)) {
            redirectURL += "&error=motivoBreve";
            
        } else {
            // "errore_generico"
            redirectURL += "&error=db_error";
        }

        response.sendRedirect(redirectURL);
    }
}