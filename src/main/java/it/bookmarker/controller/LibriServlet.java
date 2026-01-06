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
        
        // 1. Chiamo il DAO per ottenere i dati
        LibriDAO dao = new LibriDAO();
        List<Libro> libriTrovati = dao.getLibriDisponibili();
        
        // 2. Metto la lista nella 'request' così la pagina JSP può leggerla
        request.setAttribute("elencoLibri", libriTrovati);
        
        // 3. Spedisco tutto alla pagina di visualizzazione (VIEW)
        request.getRequestDispatcher("catalogo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
