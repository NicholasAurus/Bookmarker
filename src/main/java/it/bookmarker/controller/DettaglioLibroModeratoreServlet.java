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

@WebServlet("/DettaglioLibroModeratoreServlet")
public class DettaglioLibroModeratoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        

        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente");
        if (ruolo == null || (!ruolo.equalsIgnoreCase("MODERATORE") )) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                
                
                LibriDAO libriDao = new LibriDAO();
                Libro libro = libriDao.getLibroById(id);
                
              
                RecensioneDAO recensioneDao = new RecensioneDAO();
                List<Recensione> recensioni = recensioneDao.getRecensioniByLibro(id);
                
                
                request.setAttribute("libroDettaglio", libro);
                request.setAttribute("listaRecensioni", recensioni);
                
                
                request.getRequestDispatcher("dettaglioLibroModeratore.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                
                response.sendRedirect("CatalogoModeratoreServlet");
            }
        } else {
            
            response.sendRedirect("CatalogoModeratoreServlet");
        }
    }
}