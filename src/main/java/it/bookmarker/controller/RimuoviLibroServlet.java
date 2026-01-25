package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.service.LibroService;

@WebServlet("/RimuoviLibroServlet")
public class RimuoviLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);
        
        String esito = service.rimuoviLibro(idParam);
        
        HttpSession session = request.getSession();

        if (esito == null) {
            session.setAttribute("successMessage", "Libro eliminato dal catalogo");
        } else {
            session.setAttribute("errorMessage", esito);
        }
        
        response.sendRedirect("CatalogoBibliotecarioServlet");
    }
}