package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.service.LibroService;

@WebServlet("/ModificaLibroServlet")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                //Parsing ID
                int id = Integer.parseInt(idParam);
                
                //Service
                LibriDAO dao = new LibriDAO();
                LibroService service = new LibroService(dao);
                
                //Recupero Dati, metodo per il dettaglio
                Libro libro = service.getDettaglioLibro(id);
                
                request.setAttribute("libroDaModificare", libro);
                request.getRequestDispatcher("modificaLibro.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                // ID non valido
                response.sendRedirect("CatalogoBibliotecarioServlet");
            }
        } else {
            // ID mancante
            response.sendRedirect("CatalogoBibliotecarioServlet");
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione");
        
        // Inizializzazione Service
        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);

        // Gestione Azioni
        if ("aggiornaQuantita".equals(azione)) {
            String idStr = request.getParameter("id");
            String quantitaStr = request.getParameter("quantita");

            boolean aggiornato = service.aggiornaDisponibilita(idStr, quantitaStr);
            
        } 
        
        response.sendRedirect("CatalogoBibliotecarioServlet");
    }
}