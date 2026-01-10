package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.bookmarker.dao.*;


@WebServlet("/AddRecensioneServlet")
public class AddRecensioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUtente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int idUtente = (Integer) session.getAttribute("idUtente");
        int idLibro = Integer.parseInt(request.getParameter("idLibro"));
        String titolo = request.getParameter("titolo");
        String testo = request.getParameter("testo");

        AddRecensioneDAO dao = new AddRecensioneDAO();
        dao.salvaRecensione(idUtente, idLibro, titolo, testo);

        response.sendRedirect("StoricoPrestitiServlet");
    }
}