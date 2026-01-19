package it.bookmarker.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.service.LibroService;

@WebServlet("/CatalogoModeratoreServlet")
public class CatalogoModeratoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String ruolo = (String) session.getAttribute("ruoloUtente"); 
        
        if (ruolo == null || (!ruolo.equalsIgnoreCase("MODERATORE"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        //Inizializzazione Service
        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);

        //Recupero Dati
        List<Libro> elencoLibri = service.getCatalogoCompleto(); 

        //Invio alla JSP
        request.setAttribute("elencoLibri", elencoLibri);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("catalogoModeratore.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}