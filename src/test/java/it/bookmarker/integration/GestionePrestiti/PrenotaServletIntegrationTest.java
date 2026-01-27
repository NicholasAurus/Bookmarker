package it.bookmarker.integration.GestionePrestiti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.PrenotaServlet;
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
import java.time.LocalDate;
import java.lang.reflect.Method;

public class PrenotaServletIntegrationTest {

    private PrenotaServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_prenota;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new PrenotaServlet();
        
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS libri");
            
            stmt.execute("CREATE TABLE prestiti (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "utente_email VARCHAR(255), " +
                    "libro_id INT, " +
                    "data_prenotazione DATE, " +
                    "data_inizio DATE, " +
                    "data_fine_prevista DATE, " +
                    "data_restituzione_effettiva DATE, " +
                    "stato VARCHAR(50), " +
                    "motivazione VARCHAR(255)" +
                    ")");

            stmt.execute("CREATE TABLE libri (" +
                    "id_libro INT PRIMARY KEY AUTO_INCREMENT, " +
                    "titolo VARCHAR(255), " +
                    "disponibilita INT DEFAULT 5" +
                    ")");
            
            stmt.execute("INSERT INTO libri (id_libro, titolo) VALUES (1, 'Libro Test')");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS libri");
        }
    }

    @Test
    void testIntegration_TC_9_5_1_PrenotazioneEffettuata() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        
        when(request.getParameter("idLibro")).thenReturn("1");
        String dataDomani = LocalDate.now().plusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataDomani);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect(contains("msg=prenotazione_ok"));

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM prestiti WHERE utente_email = 'mario@test.it' AND libro_id = 1")) {
            
            assertTrue(rs.next());
            assertEquals("Richiesto", rs.getString("stato"));
        }
    }

    @Test
    void testIntegration_TC_9_5_2_EmailNull() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn(null);
        
        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);
        
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void testIntegration_TC_9_5_3_IdLibroNull() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn(null);
        String dataDomani = LocalDate.now().plusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataDomani);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("null") || 
            arg.toString().toLowerCase().contains("id") ||
            arg.toString().toLowerCase().contains("dati") ||
            arg.toString().toLowerCase().contains("mancanti")
        )));
    }

    @Test
    void testIntegration_TC_9_5_4_IdLibroFormatoErrato() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn("abc"); 
        String dataDomani = LocalDate.now().plusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataDomani);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        // Aggiornato: ora accetta anche "valido" oltre a "formato"
        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("valido")
        )));
    }

    @Test
    void testIntegration_TC_9_5_5_DataNull() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn("1");
        when(request.getParameter("dataRitiro")).thenReturn(null);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("data") || 
            arg.toString().toLowerCase().contains("null") ||
            arg.toString().toLowerCase().contains("dati") ||
            arg.toString().toLowerCase().contains("mancanti")
        )));
    }

    @Test
    void testIntegration_TC_9_5_6_DataFormatoErrato() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn("1");
        when(request.getParameter("dataRitiro")).thenReturn("non-una-data");

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("data") || 
            arg.toString().toLowerCase().contains("formato") ||
            arg.toString().toLowerCase().contains("valido")
        )));
    }

    @Test
    void testIntegration_TC_9_5_7_DataPassata() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn("1");
        
        String dataIeri = LocalDate.now().minusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataIeri);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (arg.toString().toLowerCase().contains("passata") || arg.toString().toLowerCase().contains("precedente"))));
    }

    @Test
    void testIntegration_TC_9_5_8_DataOltreLimite() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        when(request.getParameter("idLibro")).thenReturn("1");
        
        // Limite di 2 giorni, proviamo con 3
        String dataFutura = LocalDate.now().plusDays(3).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataFutura);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), argThat(arg -> arg != null && (arg.toString().toLowerCase().contains("limite") || arg.toString().toLowerCase().contains("giorni"))));
    }

    @Test
    void testIntegration_TC_9_5_9_LimitePrestitiRaggiunto() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (utente_email, libro_id, stato) VALUES ('mario@test.it', 10, 'Richiesto')");
            stmt.execute("INSERT INTO prestiti (utente_email, libro_id, stato) VALUES ('mario@test.it', 11, 'prenotato')");
            stmt.execute("INSERT INTO prestiti (utente_email, libro_id, stato) VALUES ('mario@test.it', 12, 'Richiesto')");
        }

        when(request.getParameter("idLibro")).thenReturn("1");
        String dataDomani = LocalDate.now().plusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataDomani);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), contains("limite massimo"));
        verify(response).sendRedirect(contains("DettaglioLibroServlet?id=1"));
    }

    @Test
    void testIntegration_TC_9_5_10_PrenotazioneGiaRichiesta() throws Exception {
        when(session.getAttribute("emailUtente")).thenReturn("mario@test.it");
        
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (utente_email, libro_id, stato) VALUES ('mario@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("idLibro")).thenReturn("1");
        String dataDomani = LocalDate.now().plusDays(1).toString();
        when(request.getParameter("dataRitiro")).thenReturn(dataDomani);

        Method doPost = PrenotaServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errorePrenotazione"), contains("già richiesto"));
        verify(response).sendRedirect(contains("DettaglioLibroServlet?id=1"));
    }
}