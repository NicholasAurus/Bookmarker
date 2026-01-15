package it.bookmarker.controller;

import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.bookmarker.dao.PrestitiDAO;

@WebServlet("/PrenotaServlet")
public class PrenotaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        String emailUtente = (String) session.getAttribute("emailUtente");
        
        if (emailUtente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idLibroStr = request.getParameter("idLibro");
        String dataRitiroStr = request.getParameter("dataRitiro");
        
        if (idLibroStr != null && dataRitiroStr != null && !dataRitiroStr.isEmpty()) {
            try {
                int idLibro = Integer.parseInt(idLibroStr);
                Date dataRitiro = Date.valueOf(dataRitiroStr);
                
                PrestitiDAO dao = new PrestitiDAO();
                boolean successo = dao.prenotaLibro(emailUtente, idLibro, dataRitiro);
                
                if (successo) {
                    response.sendRedirect("DettaglioLibroServlet?id=" + idLibro + "&msg=prenotazione_ok");
                } else {
                    response.sendRedirect("DettaglioLibroServlet?id=" + idLibro + "&error=db_error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("LibriServlet");
            }
        } else {
            response.sendRedirect("LibriServlet");
        }
    }
}