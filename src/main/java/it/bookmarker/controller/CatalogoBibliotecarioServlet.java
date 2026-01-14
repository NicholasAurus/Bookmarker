package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;

@WebServlet("/CatalogoBibliotecarioServlet")
public class CatalogoBibliotecarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || !ruolo.equalsIgnoreCase("bibliotecario")) { 
            response.sendRedirect("index.jsp"); // Rimanda alla home se non è autorizzato
            return;
        }


      
        LibriDAO dao = new LibriDAO();
        List<Libro> elencoLibri = dao.getAllLibri();
        

        request.setAttribute("elencoLibri", elencoLibri);
        
  
        request.getRequestDispatcher("catalogoBibliotecario.jsp").forward(request, response);
    }
}