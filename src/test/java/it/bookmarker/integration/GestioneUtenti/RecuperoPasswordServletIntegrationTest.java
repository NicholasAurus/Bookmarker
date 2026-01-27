package it.bookmarker.integration.GestioneUtenti;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import it.bookmarker.controller.RecuperoPasswordServlet;
import it.bookmarker.dao.DBUtil;
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
import java.sql.ResultSet;
import java.lang.reflect.Method;

public class RecuperoPasswordServletIntegrationTest {

    private RecuperoPasswordServlet servlet;
    
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:mem:testdb_bookmarker_rec;DB_CLOSE_DELAY=-1;MODE=MySQL";
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

        servlet = new RecuperoPasswordServlet();
        
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


            String passwordHash = BCrypt.hashpw("VecchiaPass1!", BCrypt.gensalt());
            String sql = String.format("INSERT INTO utenti (email, password, domanda_sicurezza, risposta_sicurezza) " +
                    "VALUES ('test@email.it', '%s', 'Nome cane?', 'Fido')", passwordHash);
            stmt.execute(sql);
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
    void testIntegration_TC_9_3_1_Successo() throws Exception {

        when(request.getParameter("action")).thenReturn("resetFinale");
        

        when(session.getAttribute("emailRecupero")).thenReturn("test@email.it");
        when(session.getAttribute("rispostaRecupero")).thenReturn("Fido");
        
        when(request.getParameter("password")).thenReturn("NuovaPass1!");
        when(request.getParameter("conferma_password")).thenReturn("NuovaPass1!");

        Method doPost = RecuperoPasswordServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);


        verify(session).removeAttribute("emailRecupero");
        verify(session).removeAttribute("rispostaRecupero");
        verify(response).sendRedirect(contains("login.jsp?msg=resetSuccess"));


        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT password FROM utenti WHERE email = 'test@email.it'")) {
            
            assertTrue(rs.next());
            String newHash = rs.getString("password");
            assertTrue(BCrypt.checkpw("NuovaPass1!", newHash));
        }
    }

    @Test
    void testIntegration_TC_9_3_2_EmailNonTrovata() throws Exception {
 
        when(request.getParameter("action")).thenReturn("cercaEmail");
        when(request.getParameter("email")).thenReturn("inesistente@email.it");

        Method doPost = RecuperoPasswordServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("error"), contains("Email non trovata"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_3_3_RispostaSbagliata() throws Exception {

        when(request.getParameter("action")).thenReturn("verificaRisposta");
        when(session.getAttribute("emailRecupero")).thenReturn("test@email.it");
        when(request.getParameter("risposta")).thenReturn("Sbagliata");

        Method doPost = RecuperoPasswordServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("error"), contains("Risposta di sicurezza errata"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testIntegration_TC_9_3_4_FormatoPasswordErrato() throws Exception {

        when(request.getParameter("action")).thenReturn("resetFinale");
        when(session.getAttribute("emailRecupero")).thenReturn("test@email.it");
        when(session.getAttribute("rispostaRecupero")).thenReturn("Fido");
        

        when(request.getParameter("password")).thenReturn("debole");
        when(request.getParameter("conferma_password")).thenReturn("debole");

        Method doPost = RecuperoPasswordServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);


        verify(request).setAttribute(eq("error"), argThat(arg -> arg != null && arg.toString().contains("formato")));
        verify(dispatcher).forward(request, response);

        try (Connection con = DBUtil.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT password FROM utenti WHERE email = 'test@email.it'")) {
            assertTrue(rs.next());
            assertTrue(BCrypt.checkpw("VecchiaPass1!", rs.getString("password")));
        }
    }

    @Test
    void testIntegration_TC_9_3_5_PasswordNonCoincidenti() throws Exception {

        when(request.getParameter("action")).thenReturn("resetFinale");
        when(session.getAttribute("emailRecupero")).thenReturn("test@email.it");
        when(session.getAttribute("rispostaRecupero")).thenReturn("Fido");
        

        when(request.getParameter("password")).thenReturn("NuovaPass1!");
        when(request.getParameter("conferma_password")).thenReturn("DiversaPass1!");

        Method doPost = RecuperoPasswordServlet.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(servlet, request, response);

        verify(request).setAttribute(eq("error"), argThat(arg -> arg != null && (arg.toString().contains("corrispondono") || arg.toString().contains("match"))));
        verify(dispatcher).forward(request, response);
    }
}