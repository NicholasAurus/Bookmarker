package it.bookmarker.integration.GestioneUtenti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.LoginServlet;
import it.bookmarker.dao.DBUtil;
import it.bookmarker.model.Utente;
import org.mindrot.jbcrypt.BCrypt;

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
import java.lang.reflect.Method;

public class LoginServletIntegrationTest {

    private LoginServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_login;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new LoginServlet();
        
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
    void testIntegration_TC_9_2_1_Login_Successo() throws Exception {
        String passwordPlain = "PasswordSicura1!";
        String passwordHash = BCrypt.hashpw(passwordPlain, BCrypt.gensalt());

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = String.format("INSERT INTO utenti (nome, email, password, ruolo, stato) " +
                    "VALUES ('Mario', 'mario.rossi@email.it', '%s', 'LETTORE', 'attivo')", passwordHash);
            stmt.execute(sql);
        }

        when(request.getParameter("email")).thenReturn("mario.rossi@email.it");
        when(request.getParameter("password")).thenReturn(passwordPlain);

        Method doPost = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(session).setAttribute(eq("utenteLoggato"), eq("Mario"));
        verify(session).setAttribute(eq("emailUtente"), eq("mario.rossi@email.it"));
        verify(session).setAttribute(eq("ruoloUtente"), eq("LETTORE"));
        verify(session).setAttribute(eq("utenteObj"), any(Utente.class));
        
        verify(response).sendRedirect("index.jsp");
    }

    @Test
    void testIntegration_TC_9_2_2_EmailNonPresente() throws Exception {
        // Nessun utente inserito nel DB

        when(request.getParameter("email")).thenReturn("email.inesistente@email.it");
        when(request.getParameter("password")).thenReturn("QualsiasiPassword");

        Method doPost = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Email o password non validi"));
        verify(dispatcher).forward(request, response);
        verify(session, never()).setAttribute(eq("utenteLoggato"), anyString());
    }

    @Test
    void testIntegration_TC_9_2_3_CredenzialiErrate() throws Exception {
        String passwordHash = BCrypt.hashpw("PasswordVera1!", BCrypt.gensalt());

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = String.format("INSERT INTO utenti (nome, email, password, ruolo, stato) " +
                    "VALUES ('Luigi', 'luigi.verdi@email.it', '%s', 'LETTORE', 'attivo')", passwordHash);
            stmt.execute(sql);
        }

        when(request.getParameter("email")).thenReturn("luigi.verdi@email.it");
        when(request.getParameter("password")).thenReturn("PasswordSbagliata");

        Method doPost = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("Email o password non validi"));
        verify(dispatcher).forward(request, response);
        verify(session, never()).setAttribute(eq("utenteLoggato"), anyString());
    }

    @Test
    void testIntegration_TC_9_2_4_UtenteInAttesa() throws Exception {
        String passwordPlain = "PasswordSicura1!";
        String passwordHash = BCrypt.hashpw(passwordPlain, BCrypt.gensalt());

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = String.format("INSERT INTO utenti (nome, email, password, ruolo, stato) " +
                    "VALUES ('Anna', 'anna.bianchi@email.it', '%s', 'LETTORE', 'in_attesa')", passwordHash);
            stmt.execute(sql);
        }

        when(request.getParameter("email")).thenReturn("anna.bianchi@email.it");
        when(request.getParameter("password")).thenReturn(passwordPlain);

        Method doPost = LoginServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("errorMessage"), contains("in attesa di approvazione"));
        verify(dispatcher).forward(request, response);
        verify(session, never()).setAttribute(eq("utenteLoggato"), anyString());
    }
}