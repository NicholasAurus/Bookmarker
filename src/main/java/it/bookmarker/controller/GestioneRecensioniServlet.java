package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.service.RecensioneService;

@WebServlet("/GestioneRecensioniServlet")
public class GestioneRecensioniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        
        if (ruolo == null || (!ruolo.equalsIgnoreCase("MODERATORE"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        //Recupero Parametri
        String azione = request.getParameter("azione");
        String idRecensioneStr = request.getParameter("idRecensione");
        String idLibroStr = request.getParameter("idLibro");

        //Inizializzazione Service
        RecensioneDAO dao = new RecensioneDAO();
        RecensioneService service = new RecensioneService(dao);

        //Esecuzione Azione
        if (idRecensioneStr != null && azione != null) {
            
            if ("rimuovi".equals(azione)) {
                service.deleteRecensioneModeratore(idRecensioneStr);
                
            } else if ("nascondi".equals(azione)) {
                service.impostaVisibilita(idRecensioneStr, false); // false = nascondi
                
            } else if ("mostra".equals(azione)) {
                service.impostaVisibilita(idRecensioneStr, true);  // true = mostra
            }
        }

        // Se eravamo dentro la pagina di un libro specifico, torniamo lì
        // altrimenti torniamo al catalogo
        if (idLibroStr != null && !idLibroStr.isEmpty()) {
            response.sendRedirect("DettaglioLibroModeratoreServlet?id=" + idLibroStr);
        } else {
            response.sendRedirect("CatalogoModeratoreServlet");
        }
    }
}