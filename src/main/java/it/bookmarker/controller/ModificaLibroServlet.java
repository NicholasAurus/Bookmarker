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

    // FASE 1: Carica il libro e mostra il form
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if(idParam != null) {
            int id = Integer.parseInt(idParam);
            LibriDAO dao = new LibriDAO();
            Libro libro = dao.getLibroById(id);
            request.setAttribute("libroDaModificare", libro);
            request.getRequestDispatcher("modificaLibro.jsp").forward(request, response);
        } else {
            response.sendRedirect("BibliotecarioServlet");
        }
    }

    // FASE 2: Riceve i dati dal form e aggiorna il DB
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("idLibro"));
            int nuoveCopie = Integer.parseInt(request.getParameter("copie"));
            
            LibriDAO dao = new LibriDAO();
            dao.aggiornaDisponibilita(id, nuoveCopie);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("BibliotecarioServlet");
    }
}