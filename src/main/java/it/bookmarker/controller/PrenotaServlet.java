package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.service.PrestitoService;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.PrestitoServiceException.*;

@WebServlet("/PrenotaServlet")
public class PrenotaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String emailUtente = (String) session.getAttribute("emailUtente");

        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idLibroStr = request.getParameter("idLibro");
        String dataRitiroStr = request.getParameter("dataRitiro");
        
        PrestitiDAO prestitiDAO = new PrestitiDAO();
        LibriDAO libriDAO = new LibriDAO();
        PrestitoService service = new PrestitoService(prestitiDAO, libriDAO);
        
        try {
            service.prenotaLibro(emailUtente, idLibroStr, dataRitiroStr);

            response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr + "&msg=prenotazione_ok");

        } catch (FormatoDatiNonValidoException | DataNonValidaException | 
                 LimitePrestitiSuperatoException | PrestitoGiaEsistenteException e) {
            
            session.setAttribute("errorePrenotazione", e.getMessage());
            handleErrorRedirect(response, idLibroStr);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorePrenotazione", "Errore tecnico durante la prenotazione.");
            handleErrorRedirect(response, idLibroStr);
        }
    }

    private void handleErrorRedirect(HttpServletResponse response, String idLibroStr) throws IOException {
        if (idLibroStr != null && idLibroStr.matches("\\d+")) {
            response.sendRedirect("DettaglioLibroServlet?id=" + idLibroStr);
        } else {
            response.sendRedirect("LibriServlet");
        }
    }
}