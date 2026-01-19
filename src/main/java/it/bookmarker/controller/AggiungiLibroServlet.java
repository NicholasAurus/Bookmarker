package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.service.LibroService;

@WebServlet("/AggiungiLibroServlet")
public class AggiungiLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Recupero Parametri (tutti come String)
        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String genere = request.getParameter("genere");
        String copieStr = request.getParameter("copie");
        String dataPubStr = request.getParameter("dataPub");
        String copertina = request.getParameter("copertina");
        String descrizione = request.getParameter("descrizione");
        
        //Inizializzazione Service
        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);
        
        //Chiamata al Service
        boolean successo = service.aggiungiLibro(titolo, autore, genere, copieStr, dataPubStr, copertina, descrizione);

        response.sendRedirect("BibliotecarioServlet");
    }
}