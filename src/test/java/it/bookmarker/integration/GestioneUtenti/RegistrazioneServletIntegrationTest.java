package it.bookmarker.integration.GestioneUtenti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.RegistrazioneServlet;
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

public class RegistrazioneServletIntegrationTest {

    private RegistrazioneServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_reg;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new RegistrazioneServlet();
        
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS utenti");
            
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
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS utenti");
        }
    }

    @Test
    void testIntegration_TC_9_1_1_Registrazione_Successo() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Nome del tuo primo animale domestico?");
        when(request.getParameter("risposta")).thenReturn("Fido");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(response).sendRedirect(contains("login.jsp?reg=success"));

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM utenti WHERE email = 'mario.rossi@email.it'")) {
            
            assertTrue(rs.next());
            assertEquals("Mario", rs.getString("nome"));
            assertEquals("in_attesa", rs.getString("stato"));
            assertNotNull(rs.getString("password")); 
        }
    }

    @Test
    void testIntegration_TC_9_1_2_EmailEsistente() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO utenti (nome, cognome, codice_fiscale, email, password, stato) " +
                         "VALUES ('Luigi', 'Verdi', 'VRDLGU80A01H501U', 'luigi.verdi@email.it', 'pass', 'attivo')");
        }

        when(request.getParameter("nome")).thenReturn("Luigi");
        when(request.getParameter("cognome")).thenReturn("Verdi");
        when(request.getParameter("codice_fiscale")).thenReturn("VRDLGU90A01H501X");
        when(request.getParameter("email")).thenReturn("luigi.verdi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("email inserita è già registrata"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_3_PasswordNonCorrispondenti() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordDiversa2!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("password non corrispondono"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_4_FormatoNomeNonValido() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario123"); 
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("nome deve contenere solo lettere"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_5_FormatoCognomeNonValido() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi123"); 
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("cognome deve contenere solo lettere"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_6_FormatoCFNonValido() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("CF_CORTO"); 
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Codice Fiscale deve essere di 16 caratteri"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_7_FormatoEmailNonValido() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("email_senza_chiocciola.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("indirizzo email valido"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_8_FormatoPasswordNonValido() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("nuovo.mario@email.it");
        when(request.getParameter("password")).thenReturn("debole"); 
        when(request.getParameter("conferma_password")).thenReturn("debole");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("password deve contenere almeno 8 caratteri"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_9_DomandaSicurezzaNonSelezionata() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn(""); 
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Seleziona una domanda di sicurezza"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_10_RispostaSicurezzaNonValida() throws Exception {
        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U");
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn(""); 

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("risposta alla domanda di sicurezza è obbligatoria"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_1_11_CFGiaPresente() throws Exception {
        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("INSERT INTO utenti (nome, cognome, codice_fiscale, email, password, stato) " +
                         "VALUES ('Gino', 'Blu', 'RSSMRA80A01H501U', 'gino.blu@email.it', 'pass', 'attivo')");
        }

        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("codice_fiscale")).thenReturn("RSSMRA80A01H501U"); 
        when(request.getParameter("email")).thenReturn("mario.rossi@email.it"); 
        when(request.getParameter("password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("conferma_password")).thenReturn("PasswordSegreta1!");
        when(request.getParameter("domanda")).thenReturn("Domanda");
        when(request.getParameter("risposta")).thenReturn("Risposta");

        Method doPost = RegistrazioneServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Codice Fiscale inserito è già registrato"));
        verify(dispatcher).forward(request, response);
    }
}