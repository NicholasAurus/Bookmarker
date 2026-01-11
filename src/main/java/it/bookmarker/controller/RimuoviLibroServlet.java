package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.bookmarker.dao.LibriDAO;

@WebServlet("/RimuoviLibroServlet")
public class RimuoviLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                LibriDAO dao = new LibriDAO();
                dao.rimuoviLibro(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Torna al catalogo aggiornato
        response.sendRedirect("GestoreServlet");
    }
}