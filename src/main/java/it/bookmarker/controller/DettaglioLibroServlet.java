package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.model.Recensione;
import it.bookmarker.service.LibroService;
import it.bookmarker.service.RecensioneService;

@WebServlet("/DettaglioLibroServlet")
public class DettaglioLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                //Parsing dell'ID
                int id = Integer.parseInt(idParam);
      
                //Inizializzazione Service
                LibriDAO libriDao = new LibriDAO();
                RecensioneDAO recDao = new RecensioneDAO();
                
                LibroService libroService = new LibroService(libriDao);
                RecensioneService recService = new RecensioneService(recDao);

                //Recupero Dati
                Libro libro = libroService.getDettaglioLibro(id);
                List<Recensione> recensioni = recService.getRecensioniPubbliche(id);
                
                request.setAttribute("libroDettaglio", libro);
                request.setAttribute("listaRecensioni", recensioni);
                
                request.getRequestDispatcher("dettaglioLibro.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                // Se l'ID non è valido, rimandiamo alla lista libri
                response.sendRedirect("LibriServlet");
            }
        } else {
            // Se manca l'ID
            response.sendRedirect("LibriServlet");
        }
    }
}