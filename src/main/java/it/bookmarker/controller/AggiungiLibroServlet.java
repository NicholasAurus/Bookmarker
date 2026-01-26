package it.bookmarker.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.service.LibroService;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.LibroServiceException.*;


@WebServlet("/AggiungiLibroServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class AggiungiLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String titolo = request.getParameter("titolo");
        String autore = request.getParameter("autore");
        String genere = request.getParameter("genere");
        String copieStr = request.getParameter("copie");
        String dataPubStr = request.getParameter("dataPub");
        String descrizione = request.getParameter("descrizione");
        
        //GESTIONE UPLOAD FILE
        Part filePart = request.getPart("copertina");
        String nomeFileImmagine = "";

        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            
            String cleanFileName = System.currentTimeMillis() + "_" + fileName.replaceAll("\\s+", "_");
            
            String uploadPath = getServletContext().getRealPath("") + File.separator + "img";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            try {
                filePart.write(uploadPath + File.separator + cleanFileName);
                nomeFileImmagine = "img/" + cleanFileName; 
            } catch (IOException e) {
                e.printStackTrace();
                nomeFileImmagine = ""; 
            }
        }

        LibriDAO dao = new LibriDAO();
        LibroService service = new LibroService(dao);
        HttpSession session = request.getSession();
        
        try {
            service.aggiungiLibro(titolo, autore, genere, copieStr, dataPubStr, nomeFileImmagine, descrizione);
            
            session.setAttribute("successMessage", "Nuovo libro aggiunto con successo al catalogo.");
            response.sendRedirect("CatalogoBibliotecarioServlet");

        } catch (FormatoDatiNonValidoException | DataNonValidaException | CopieNegativeException e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect("aggiungiLibro.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Errore tecnico durante il salvataggio del libro.");
            response.sendRedirect("aggiungiLibro.jsp");
        }
    }
}