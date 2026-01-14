package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;

@WebServlet("/ModificaLibroServlet")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if(idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                LibriDAO dao = new LibriDAO();
                Libro libro = dao.getLibroById(id);
                request.setAttribute("libroDaModificare", libro);
                request.getRequestDispatcher("modificaLibro.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect("CatalogoBibliotecarioServlet");
            }
        } else {
            response.sendRedirect("CatalogoBibliotecarioServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione");
        LibriDAO dao = new LibriDAO();

        
        if ("aggiornaQuantita".equals(azione)) {
            try {
                
                String idStr = request.getParameter("id");
                String quantitaStr = request.getParameter("quantita");

                if (idStr != null && quantitaStr != null) {
                    int id = Integer.parseInt(idStr);
                    int nuoveCopie = Integer.parseInt(quantitaStr);
                    dao.aggiornaDisponibilita(id, nuoveCopie);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        

        
        response.sendRedirect("CatalogoBibliotecarioServlet");
    }
}