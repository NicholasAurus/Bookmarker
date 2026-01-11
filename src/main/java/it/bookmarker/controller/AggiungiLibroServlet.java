package it.bookmarker.controller;

import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;

@WebServlet("/AggiungiLibroServlet")
public class AggiungiLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        try {
            String titolo = request.getParameter("titolo");
            String autore = request.getParameter("autore");
            String genere = request.getParameter("genere");
            int copie = Integer.parseInt(request.getParameter("copie"));
            Date dataPub = Date.valueOf(request.getParameter("dataPub"));
            String copertina = request.getParameter("copertina");
            String descrizione = request.getParameter("descrizione");
            
            Libro nuovo = new Libro();
            nuovo.setTitolo(titolo);
            nuovo.setAutore(autore);
            nuovo.setGenere(genere);
            nuovo.setDisponibilita(copie);
            nuovo.setDataPubblicazione(dataPub);
            nuovo.setCopertina(copertina);
            nuovo.setDescrizione(descrizione);
            
            LibriDAO dao = new LibriDAO();
            dao.inserisciLibro(nuovo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect("GestoreServlet");
    }
}