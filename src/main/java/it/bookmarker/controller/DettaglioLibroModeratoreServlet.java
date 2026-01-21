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
import it.bookmarker.dao.RecensioneDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.model.Recensione;
import it.bookmarker.service.LibroService;
import it.bookmarker.service.RecensioneService;

@WebServlet("/DettaglioLibroModeratoreServlet")
public class DettaglioLibroModeratoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        
        if (ruolo == null || (!ruolo.equalsIgnoreCase("MODERATORE"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                //Parsing (fatto qui perchè ci sono 2 service)
                int id = Integer.parseInt(idParam);
                
                //Inizializzazione dei Service
                LibriDAO libriDao = new LibriDAO();
                RecensioneDAO recDao = new RecensioneDAO();
                
                LibroService libroService = new LibroService(libriDao);
                RecensioneService recService = new RecensioneService(recDao);
                
                //Recupero Dati dai Service
                Libro libro = libroService.getDettaglioLibro(id);
                List<Recensione> recensioni = recService.getRecensioniPerModeratore(id);
                

                request.setAttribute("libroDettaglio", libro);
                request.setAttribute("listaRecensioni", recensioni);
                
                request.getRequestDispatcher("dettaglioLibroModeratore.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                // Se l'ID non è un numero (es. ciao), torniamo al catalogo
                response.sendRedirect("CatalogoModeratoreServlet");
            }
        } else {
            // Se manca l'ID, torniamo al catalogo
            response.sendRedirect("CatalogoModeratoreServlet");
        }
    }
}