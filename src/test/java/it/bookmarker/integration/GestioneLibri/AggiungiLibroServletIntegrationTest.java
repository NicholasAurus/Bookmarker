package it.bookmarker.integration.GestioneLibri;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.AggiungiLibroServlet;
import it.bookmarker.dao.DBUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.lang.reflect.Method;

public class AggiungiLibroServletIntegrationTest {

    private AggiungiLibroServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_add;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;
    @Mock private Part part;
    @Mock private ServletContext servletContext;
    @Mock private ServletConfig servletConfig;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        DBUtil.setConnectionConfig(H2_URL, H2_USER, H2_PASS, H2_DRIVER);
        servlet = spy(new AggiungiLibroServlet());
        doReturn(servletContext).when(servlet).getServletContext();
        when(servletContext.getRealPath(anyString())).thenReturn(System.getProperty("java.io.tmpdir"));

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS libri");
            
            stmt.execute("CREATE TABLE libri (" +
                    "id_libro INT PRIMARY KEY AUTO_INCREMENT, " +
                    "titolo VARCHAR(255), " +
                    "autore VARCHAR(255), " +
                    "genere VARCHAR(100), " +
                    "disponibilita INT, " +
                    "data_pubblicazione DATE, " +
                    "descrizione TEXT, " +
                    "copertina VARCHAR(255), " +
                    "data_rientro DATE, " +
                    "attivo INT DEFAULT 1" +
                    ")");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS libri");
        }
    }


    @Test
    void testIntegration_TC_9_10_1_AggiungiLibro_Successo() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Il Nome della Rosa");
        when(request.getParameter("autore")).thenReturn("Umberto Eco");
        when(request.getParameter("genere")).thenReturn("Giallo Storico");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Romanzo storico.");

        // Mocking upload file valido
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("successMessage"), anyString());
        verify(response).sendRedirect("CatalogoBibliotecarioServlet");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM libri WHERE titolo = 'Il Nome della Rosa'")) {
            
            assertTrue(rs.next());
            assertEquals("Umberto Eco", rs.getString("autore"));
            assertEquals(5, rs.getInt("disponibilita"));
            assertEquals(1, rs.getInt("attivo"));
        }
    }


    @Test
    void testIntegration_TC_9_10_2_TitoloObbligatorio() throws Exception {
        when(request.getParameter("titolo")).thenReturn(""); 
        when(request.getParameter("autore")).thenReturn("Eco");
        when(request.getParameter("genere")).thenReturn("Giallo");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("titolo") || 
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_3_AutoreObbligatorio() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn(""); 
        when(request.getParameter("genere")).thenReturn("Giallo");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("autore") || 
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_4_GenereObbligatorio() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn(""); 
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("genere") || 
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_5_CopieObbligatorie() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn(""); 
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("copie") || 
            arg.toString().toLowerCase().contains("numero") ||
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_6_CopieNegative() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("-5"); 
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("negativo") || 
            arg.toString().toLowerCase().contains("valido")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_7_DataObbligatoria() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn(""); 
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("data") || 
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_8_DataFuturaNonValida() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("5");
        
        String futureDate = LocalDate.now().plusDays(400).toString();
        when(request.getParameter("dataPub")).thenReturn(futureDate);
        
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("data") || 
            arg.toString().toLowerCase().contains("valida") ||
            arg.toString().toLowerCase().contains("futuro")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_9_CopertinaObbligatoria() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");

        when(request.getPart("copertina")).thenReturn(null); 

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("copertina") || 
            arg.toString().toLowerCase().contains("immagine") ||
            arg.toString().toLowerCase().contains("obbligatoria")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_10_CopertinaFormatoErrato() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn("Desc");
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("documento.txt");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("immagine") ||
            arg.toString().toLowerCase().contains("copertina")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }

    @Test
    void testIntegration_TC_9_10_11_DescrizioneObbligatoria() throws Exception {
        when(request.getParameter("titolo")).thenReturn("Titolo");
        when(request.getParameter("autore")).thenReturn("Autore");
        when(request.getParameter("genere")).thenReturn("Genere");
        when(request.getParameter("copie")).thenReturn("5");
        when(request.getParameter("dataPub")).thenReturn("1980-01-01");
        when(request.getParameter("descrizione")).thenReturn(""); // Vuota
        
        when(request.getPart("copertina")).thenReturn(part);
        when(part.getSize()).thenReturn(1024L);
        when(part.getSubmittedFileName()).thenReturn("cover.jpg");

        Method doPost = AggiungiLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("descrizione") || 
            arg.toString().toLowerCase().contains("obbligatoria")
        )));
        verify(response).sendRedirect("aggiungiLibro.jsp");
    }
}