package it.bookmarker.integration.GestionePrestiti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.GestionePrestitiServlet;
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

public class GestionePrestitiServletIntegrationTest {

    private GestionePrestitiServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_prestiti_final;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new GestionePrestitiServlet();
        
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Simuliamo ruolo Bibliotecario per avere i permessi
        when(session.getAttribute("ruoloUtente")).thenReturn("BIBLIOTECARIO");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS libri");
            
            stmt.execute("CREATE TABLE libri (" +
                    "id_libro INT PRIMARY KEY AUTO_INCREMENT, " +
                    "titolo VARCHAR(255), " +
                    "autore VARCHAR(255), " +
                    "copertina VARCHAR(255), " +
                    "descrizione VARCHAR(255), " +
                    "disponibilita INT" +
                    ")");

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
                    
            stmt.execute("INSERT INTO libri (id_libro, titolo, autore, copertina, descrizione, disponibilita) " +
                         "VALUES (1, 'Libro Test', 'Autore', 'img.jpg', 'Desc', 5)");
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

    // SEZIONE 9.7: SEGNARE UN PRESTITO COME RITIRATO

    @Test
    void testIntegration_TC_9_7_1_ConfermaRitiro_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (10, 'user@test.it', 1, 'prenotato')");
        }

        when(request.getParameter("azione")).thenReturn("ritiro");
        when(request.getParameter("idPrestito")).thenReturn("10");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestionePrestitiServlet?tab=prenotati");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato, data_inizio FROM prestiti WHERE id = 10")) {
            assertTrue(rs.next());
            String stato = rs.getString("stato");
            assertTrue(stato.equalsIgnoreCase("In Corso") || stato.equalsIgnoreCase("Ritirato"));
            assertNotNull(rs.getDate("data_inizio"));
        }
    }

    @Test
    void testIntegration_TC_9_7_2_ConfermaRitiro_IdNull() throws Exception {

        when(request.getParameter("azione")).thenReturn("ritiro");
        when(request.getParameter("idPrestito")).thenReturn(null);

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void testIntegration_TC_9_7_3_ConfermaRitiro_IdFormatoErrato() throws Exception {
        when(request.getParameter("azione")).thenReturn("ritiro");
        when(request.getParameter("idPrestito")).thenReturn("abc");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("valido") ||
            arg.toString().toLowerCase().contains("numero")
        )));
    }

    @Test
    void testIntegration_TC_9_7_4_ConfermaRitiro_NonEsiste() throws Exception {
        when(request.getParameter("azione")).thenReturn("ritiro");
        when(request.getParameter("idPrestito")).thenReturn("999");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("non esiste") || 
            arg.toString().toLowerCase().contains("trovato")
        )));
    }

    @Test
    void testIntegration_TC_9_7_5_ConfermaRitiro_StatoErrato() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (20, 'user@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("azione")).thenReturn("ritiro");
        when(request.getParameter("idPrestito")).thenReturn("20");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("stato") || 
            arg.toString().toLowerCase().contains("prenotato")
        )));
        
        verify(response).sendRedirect("GestionePrestitiServlet?tab=prenotati");
    }

    //SEZIONE 9.8: ANNULLARE UN PRESTITO

    @Test
    void testIntegration_TC_9_8_1_AnnullaPrestito_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (11, 'user@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("11");
        when(request.getParameter("motivazione")).thenReturn("Utente non si è presentato"); 

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestionePrestitiServlet?tab=prenotati");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato, motivazione FROM prestiti WHERE id = 11")) {
            assertTrue(rs.next());
            String stato = rs.getString("stato");
            assertTrue(stato.equalsIgnoreCase("Annullato") || stato.equalsIgnoreCase("Rifiutato"));
            assertEquals("Utente non si è presentato", rs.getString("motivazione"));
        }
    }

    @Test
    void testIntegration_TC_9_8_2_AnnullaPrestito_IdNull() throws Exception {
        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn(null);
        when(request.getParameter("motivazione")).thenReturn("Motivazione valida");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void testIntegration_TC_9_8_3_AnnullaPrestito_IdFormatoErrato() throws Exception {
        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("xyz");
        when(request.getParameter("motivazione")).thenReturn("Motivazione valida");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("valido")
        )));
    }

    @Test
    void testIntegration_TC_9_8_4_AnnullaPrestito_MotivazioneNull() throws Exception {
        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("10"); 
        when(request.getParameter("motivazione")).thenReturn(null);

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("motivazione") || 
            arg.toString().toLowerCase().contains("null") ||
            arg.toString().toLowerCase().contains("obbligatori")
        )));
    }

    @Test
    void testIntegration_TC_9_8_5_AnnullaPrestito_MotivazioneCorta() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (13, 'user@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("13");
        when(request.getParameter("motivazione")).thenReturn("Corta"); // < 10 caratteri

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("caratteri") || 
            arg.toString().toLowerCase().contains("lunghezza") ||
            arg.toString().toLowerCase().contains("breve")
        )));
    }

    @Test
    void testIntegration_TC_9_8_6_AnnullaPrestito_NonEsiste() throws Exception {
        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("999");
        when(request.getParameter("motivazione")).thenReturn("Motivazione valida");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("non esiste") || 
            arg.toString().toLowerCase().contains("trovato")
        )));
    }

    @Test
    void testIntegration_TC_9_8_7_AnnullaPrestito_StatoErrato() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (35, 'user@test.it', 1, 'Restituito')");
        }

        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("35");
        when(request.getParameter("motivazione")).thenReturn("Motivazione valida");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("stato") || 
            arg.toString().toLowerCase().contains("non può")
        )));
    }

    @Test
    void testIntegration_TC_9_8_8_AnnullaPrestito_LibroNonEsiste() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (36, 'user@test.it', 9999, 'Richiesto')");
        }

        when(request.getParameter("azione")).thenReturn("annulla");
        when(request.getParameter("idPrestito")).thenReturn("36");
        when(request.getParameter("motivazione")).thenReturn("Motivazione valida");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("libro") || 
            arg.toString().toLowerCase().contains("non esiste") ||
            arg.toString().toLowerCase().contains("trovato") ||
            arg.toString().toLowerCase().contains("prestito")
        )));
    }

    //SEZIONE 9.9: REGISTRA RESTITUZIONE

    @Test
    void testIntegration_TC_9_9_1_RegistraRestituzione_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato, data_inizio) VALUES (15, 'user@test.it', 1, 'In Corso', CURDATE())");
        }

        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn("15");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestionePrestitiServlet?tab=attivi");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato, data_restituzione_effettiva FROM prestiti WHERE id = 15")) {
            assertTrue(rs.next());
            assertEquals("Restituito", rs.getString("stato"));
            assertNotNull(rs.getDate("data_restituzione_effettiva"));
        }
        
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT disponibilita FROM libri WHERE id_libro = 1")) {
            assertTrue(rs.next());
            assertEquals(6, rs.getInt("disponibilita"));
        }
    }

    @Test
    void testIntegration_TC_9_9_2_RegistraRestituzione_IdNull() throws Exception {
        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn(null);

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void testIntegration_TC_9_9_3_RegistraRestituzione_IdFormatoErrato() throws Exception {
        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn("bad_id");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("valido")
        )));
    }

    @Test
    void testIntegration_TC_9_9_4_RegistraRestituzione_NonEsiste() throws Exception {
        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn("9999");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("non esiste") || 
            arg.toString().toLowerCase().contains("trovato")
        )));
    }

    @Test
    void testIntegration_TC_9_9_5_RegistraRestituzione_StatoErrato() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (16, 'user@test.it', 1, 'prenotato')");
        }

        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn("16");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("stato") || 
            arg.toString().toLowerCase().contains("in corso") ||
            arg.toString().toLowerCase().contains("ritirato")
        )));
    }

    @Test
    void testIntegration_TC_9_9_6_RegistraRestituzione_LibroNonEsiste() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (17, 'user@test.it', 9999, 'In Corso')");
        }

        when(request.getParameter("azione")).thenReturn("restituzione");
        when(request.getParameter("idPrestito")).thenReturn("17");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("libro") || 
            arg.toString().toLowerCase().contains("non esiste") ||
            arg.toString().toLowerCase().contains("trovato") ||
            arg.toString().toLowerCase().contains("prestito")
        )));
    }

    @Test
    void testIntegration_Sicurezza_NonBibliotecario() throws Exception {
        when(session.getAttribute("ruoloUtente")).thenReturn("LETTORE");

        Method doPost = GestionePrestitiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("login.jsp");
    }
}