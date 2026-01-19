package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;
import it.bookmarker.service.UtenteService;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        //Inizializza Service
        UtenteDAO dao = new UtenteDAO();
        UtenteService service = new UtenteService(dao);

        try {
            //Login
            Utente utenteTrovato = service.login(email, password);

            HttpSession session = request.getSession();
            
            session.setAttribute("utenteLoggato", utenteTrovato.getNome());
            session.setAttribute("emailUtente", utenteTrovato.getEmail());
            session.setAttribute("utenteObj", utenteTrovato);
            

            String ruoloDB = utenteTrovato.getRuolo();
            if (ruoloDB != null) {
                session.setAttribute("ruoloUtente", ruoloDB.toUpperCase());
            } else {
                session.setAttribute("ruoloUtente", "LETTORE");
            }
            
            session.setMaxInactiveInterval(30 * 60); // 30 minuti

            response.sendRedirect("index.jsp");

        } catch (Exception e) {
            sendError(request, response, e.getMessage());
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
        rd.forward(request, response);
    }
}