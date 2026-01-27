package it.bookmarker.integration.GestioneLibri;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.ModificaLibroServlet;
import it.bookmarker.dao.DBUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.lang.reflect.Method;

public class ModificaLibroServletIntegrationTest {

    private ModificaLibroServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_mod;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        DBUtil.setConnectionConfig(H2_URL, H2_USER, H2_PASS, H2_DRIVER);

        servlet = new ModificaLibroServlet();
        
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
    void testIntegration_TC_9_11_1_ModificaDisponibilita_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (titolo, autore, genere, disponibilita, data_pubblicazione, descrizione, copertina, attivo) " +
                         "VALUES ('Libro Test', 'Autore Test', 'Genere', 5, '2020-01-01', 'Desc', 'img.jpg', 1)");
        }

        when(request.getParameter("azione")).thenReturn("aggiornaQuantita");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantita")).thenReturn("50");

        Method doPost = ModificaLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("successMessage"), anyString());
        verify(response).sendRedirect("CatalogoBibliotecarioServlet");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT disponibilita FROM libri WHERE id_libro = 1")) {
            
            assertTrue(rs.next());
            assertEquals(50, rs.getInt("disponibilita"));
        }
    }

    @Test
    void testIntegration_TC_9_11_2_ModificaDisponibilita_IdNull() throws Exception {
        when(request.getParameter("azione")).thenReturn("aggiornaQuantita");
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("quantita")).thenReturn("50");

        Method doPost = ModificaLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("id") || 
            arg.toString().toLowerCase().contains("null") ||
            arg.toString().toLowerCase().contains("trovato") ||
            arg.toString().toLowerCase().contains("valido")
        )));
        verify(response).sendRedirect("CatalogoBibliotecarioServlet");
    }

    @Test
    void testIntegration_TC_9_11_3_ModificaDisponibilita_CopieNull() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (titolo, autore, genere, disponibilita, data_pubblicazione, descrizione, copertina, attivo) " +
                         "VALUES ('Libro Test', 'Autore Test', 'Genere', 5, '2020-01-01', 'Desc', 'img.jpg', 1)");
        }

        when(request.getParameter("azione")).thenReturn("aggiornaQuantita");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantita")).thenReturn(null); // o ""

        Method doPost = ModificaLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("copie") || 
            arg.toString().toLowerCase().contains("quantità") ||
            arg.toString().toLowerCase().contains("obbligatorio") ||
            arg.toString().toLowerCase().contains("valido")
        )));
        verify(response).sendRedirect("CatalogoBibliotecarioServlet");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT disponibilita FROM libri WHERE id_libro = 1")) {
            assertTrue(rs.next());
            assertEquals(5, rs.getInt("disponibilita"));
        }
    }

    @Test
    void testIntegration_TC_9_11_4_ModificaDisponibilita_QuantitaNegativa() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (titolo, autore, genere, disponibilita, data_pubblicazione, descrizione, copertina, attivo) " +
                         "VALUES ('Libro Stabile', 'Autore Test', 'Genere', 10, '2020-01-01', 'Desc', 'img.jpg', 1)");
        }

        when(request.getParameter("azione")).thenReturn("aggiornaQuantita");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("quantita")).thenReturn("-5");

        Method doPost = ModificaLibroServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorMessage"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("negativo") ||
            arg.toString().toLowerCase().contains("valido")
        )));
        verify(response).sendRedirect("CatalogoBibliotecarioServlet");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT disponibilita FROM libri WHERE id_libro = 1")) {
            
            assertTrue(rs.next());
            assertEquals(10, rs.getInt("disponibilita"));
        }
    }
}