package it.bookmarker.integration.GestioneUtenti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.GestioneUtentiServlet;
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

public class GestioneUtentiServletIntegrationTest {

    private GestioneUtentiServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_gestione_full;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new GestioneUtentiServlet();
        
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Simuliamo un bibliotecario loggato per tutti i test
        when(session.getAttribute("ruoloUtente")).thenReturn("BIBLIOTECARIO");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS utenti");
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS libri");
            
            stmt.execute("CREATE TABLE utenti (" +
                    "id_utente INT PRIMARY KEY AUTO_INCREMENT, " +
                    "nome VARCHAR(255), " +
                    "cognome VARCHAR(255), " +
                    "codice_fiscale VARCHAR(16), " +
                    "email VARCHAR(255) UNIQUE, " +
                    "password VARCHAR(255), " +
                    "ruolo VARCHAR(50), " +
                    "stato VARCHAR(50), " +
                    "data_registrazione DATE, " +
                    "domanda_sicurezza VARCHAR(255), " +
                    "risposta_sicurezza VARCHAR(255)" +
                    ")");

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
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS utenti");
            stmt.execute("DROP TABLE IF EXISTS prestiti");
            stmt.execute("DROP TABLE IF EXISTS libri");
        }
    }

    //TEST SEZIONE 9.4: APPROVARE RICHIESTA REGISTRAZIONE

    @Test
    void testIntegration_TC_9_4_1_ApprovaUtente_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO utenti (email, stato, ruolo) VALUES ('user@test.it', 'in_attesa', 'LETTORE')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("utente");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("emailUtente")).thenReturn("user@test.it");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestioneUtentiServlet?tab=utenti");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato FROM utenti WHERE email = 'user@test.it'")) {
            assertTrue(rs.next());
            assertEquals("attivo", rs.getString("stato"));
        }
    }

    @Test
    void testIntegration_TC_9_4_2_ApprovaUtente_EmailNull() throws Exception {
        when(request.getParameter("tipoOperazione")).thenReturn("utente");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("emailUtente")).thenReturn(null);

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (arg.toString().toLowerCase().contains("null") || arg.toString().toLowerCase().contains("obbligatori"))));
    }

    @Test
    void testIntegration_TC_9_4_3_ApprovaUtente_EmailNonValida() throws Exception {
        when(request.getParameter("tipoOperazione")).thenReturn("utente");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("emailUtente")).thenReturn("non_esiste@test.it");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (arg.toString().toLowerCase().contains("non valida") || arg.toString().toLowerCase().contains("non trovata"))));
    }

    @Test
    void testIntegration_TC_9_4_4_ApprovaUtente_StatoNonInAttesa() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO utenti (email, stato, ruolo) VALUES ('active@test.it', 'attivo', 'LETTORE')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("utente");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("emailUtente")).thenReturn("active@test.it");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && arg.toString().toLowerCase().contains("in attesa")));
    }

    @Test
    void testIntegration_RifiutaUtente_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO utenti (email, stato, ruolo) VALUES ('baduser@test.it', 'in_attesa', 'LETTORE')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("utente");
        when(request.getParameter("azione")).thenReturn("rifiuta");
        when(request.getParameter("emailUtente")).thenReturn("baduser@test.it");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestioneUtentiServlet?tab=utenti");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM utenti WHERE email = 'baduser@test.it'")) {
            if (rs.next()) {
                assertEquals(0, rs.getInt(1));
            }
        }
    }

 //TEST SEZIONE 9.6: APPROVARE PRENOTAZIONE PRESTITO

    @Test
    void testIntegration_TC_9_6_1_ApprovaPrestito_Successo() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (id_libro, titolo, disponibilita) VALUES (1, 'Libro Disponibile', 5)");
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (10, 'user@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn("10");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect("GestioneUtentiServlet?tab=prestiti");

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato FROM prestiti WHERE id = 10")) {
            assertTrue(rs.next());
            assertEquals("prenotato", rs.getString("stato"));
        }

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT disponibilita FROM libri WHERE id_libro = 1")) {
            assertTrue(rs.next());
            assertEquals(4, rs.getInt("disponibilita"));
        }
    }

    @Test
    void testIntegration_TC_9_6_2_ApprovaPrestito_IdNull() throws Exception {
        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn(null);

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("null") || 
            arg.toString().toLowerCase().contains("mancante") || 
            arg.toString().toLowerCase().contains("obbligatorio")
        )));
    }

    @Test
    void testIntegration_TC_9_6_3_ApprovaPrestito_IdFormatoErrato() throws Exception {
        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn("abc");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);


        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("formato") || 
            arg.toString().toLowerCase().contains("numero") ||
            arg.toString().toLowerCase().contains("id")
        )));
    }

    @Test
    void testIntegration_TC_9_6_4_ApprovaPrestito_NonEsiste() throws Exception {
        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn("999");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("non esiste") || 
            arg.toString().toLowerCase().contains("trovato")
        )));
    }

    @Test
    void testIntegration_TC_9_6_5_ApprovaPrestito_StatoErrato() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (id_libro, titolo, disponibilita) VALUES (1, 'Libro Disponibile', 5)");
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (11, 'user@test.it', 1, 'Restituito')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn("11");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), argThat(arg -> arg != null && (
            arg.toString().toLowerCase().contains("stato") || 
            arg.toString().toLowerCase().contains("richiesto")
        )));
    }

    @Test
    void testIntegration_TC_9_6_6_ApprovaPrestito_CopieEsaurite() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO libri (id_libro, titolo, disponibilita) VALUES (1, 'Libro Raro', 0)");
            stmt.execute("INSERT INTO prestiti (id, utente_email, libro_id, stato) VALUES (10, 'user@test.it', 1, 'Richiesto')");
        }

        when(request.getParameter("tipoOperazione")).thenReturn("prestito");
        when(request.getParameter("azione")).thenReturn("accetta");
        when(request.getParameter("idPrestito")).thenReturn("10");

        Method doPost = GestioneUtentiServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("errore"), contains("non ci sono copie disponibili"));

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT stato FROM prestiti WHERE id = 10")) {
            assertTrue(rs.next());
            assertEquals("Richiesto", rs.getString("stato"));
        }
    }
}