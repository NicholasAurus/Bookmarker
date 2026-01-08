package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;

@WebServlet("/LibriServlet")
public class LibriServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        LibriDAO dao = new LibriDAO();
        List<Libro> libriTrovati = dao.getAllLibri();
        
        
        request.setAttribute("elencoLibri", libriTrovati);
        
        
        request.getRequestDispatcher("catalogo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
