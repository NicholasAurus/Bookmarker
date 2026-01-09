package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;

@WebServlet("/DettaglioLibroServlet")
public class DettaglioLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
       
        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                
               
                LibriDAO dao = new LibriDAO();
                Libro libro = dao.getLibroById(id);
                
               
                request.setAttribute("libroDettaglio", libro);
                request.getRequestDispatcher("dettaglioLibro.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                response.sendRedirect("LibriServlet");
            }
        } else {
            response.sendRedirect("LibriServlet"); 
        }
    }
}