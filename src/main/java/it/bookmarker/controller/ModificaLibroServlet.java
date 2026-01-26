package it.bookmarker.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.service.LibroService;
import it.bookmarker.service.exception.GenericException.FormatoDatiNonValidoException;
import it.bookmarker.service.exception.LibroServiceException.CopieNegativeException;
import it.bookmarker.service.exception.LibroServiceException.LibroNonTrovatoException;

@WebServlet("/ModificaLibroServlet")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                
                LibriDAO dao = new LibriDAO();
                LibroService service = new LibroService(dao);

                Libro libro = service.getDettaglioLibro(id);
                
                request.setAttribute("libroDaModificare", libro);
                request.getRequestDispatcher("modificaLibro.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                response.sendRedirect("CatalogoBibliotecarioServlet");
            }
        } else {
            response.sendRedirect("CatalogoBibliotecarioServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String azione = request.getParameter("azione");
        
        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);
        HttpSession session = request.getSession();

        if ("aggiornaQuantita".equals(azione)) {
            String idStr = request.getParameter("id");
            String quantitaStr = request.getParameter("quantita");

            try {
                service.aggiornaDisponibilita(idStr, quantitaStr);
                session.setAttribute("successMessage", "Quantità aggiornata con successo.");
                
            } catch (FormatoDatiNonValidoException | CopieNegativeException | LibroNonTrovatoException e) {
                session.setAttribute("errorMessage", e.getMessage());
                
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Errore tecnico durante l'aggiornamento.");
            }
        } 
        
        response.sendRedirect("CatalogoBibliotecarioServlet");
    }
}