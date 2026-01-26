package it.bookmarker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.bookmarker.model.Utente;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.UtenteServiceException.*;
import it.bookmarker.dao.UtenteDAO;


//Test di Unità per UtenteService 

public class UtenteServiceTest {

    @Mock
    private UtenteDAO utenteDAO; // Mock del database

    @InjectMocks
    private UtenteService utenteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //SEZIONE TEST REGISTRAZIONE (9.1)
     
    @Test
    public void testTC9_1_1_Successo() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!"; 
        String confermaPassword = "Password120!"; 
        String domanda = "qual è il tuo libro preferito?"; 
        String risposta = "1984"; 

        when(utenteDAO.esisteEmail(email)).thenReturn(false);
        when(utenteDAO.esisteCodiceFiscale(cf)).thenReturn(false);
        when(utenteDAO.registraUtente(any(Utente.class))).thenReturn(true);

        assertDoesNotThrow(() -> utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        ArgumentCaptor<Utente> utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteDAO).registraUtente(utenteCaptor.capture());
        
        Utente salvato = utenteCaptor.getValue();
        assertEquals(nome, salvato.getNome());
        assertEquals(cognome, salvato.getCognome());
        assertEquals(cf, salvato.getCodiceFiscale());
        assertEquals(email, salvato.getEmail());
        assertEquals(domanda, salvato.getDomandaSicurezza());
        assertEquals(risposta, salvato.getRispostaSicurezza());
    }
    
    @Test
    public void testTC9_1_2_EmailEsistente() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!"; 
        String confermaPassword = "Password120!"; 
        String domanda = "qual è il tuo libro preferito?"; 
        String risposta = "1984"; 

        when(utenteDAO.esisteEmail(email)).thenReturn(true);

        assertThrows(EmailGiaRegistrataException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }
    
    @Test
    public void testTC9_1_3_PasswordNonCorrispondenti() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "PasswordDiversa!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(PasswordNonCorrispondentiException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_4_FormatoNomeNonValido() throws Exception {
        String nome = "Nicholas123"; 
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_5_FormatoCognomeNonValido() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi1"; 
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_6_FormatoCodiceFiscaleNonValido() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "CF_NON_VALIDO"; 
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_7_FormatoEmailNonValido() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0gmail.com"; 
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_8_FormatoPasswordNonValido() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "password"; 
        String confermaPassword = "password";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        assertThrows(FormatoPasswordNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_9_DomandaSicurezzaNonSelezionata() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = ""; 
        String risposta = "1984";

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_10_RispostaSicurezzaNonValida() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = ""; 

        assertThrows(FormatoDatiNonValidoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }

    @Test
    public void testTC9_1_11_CodiceFiscaleGiaPresente() throws Exception {
        String nome = "Nicholas";
        String cognome = "Grimaldi";
        String cf = "GRMNCL65S67M126L";
        String email = "nicholasvoid0@gmail.com";
        String password = "Password120!";
        String confermaPassword = "Password120!";
        String domanda = "qual è il tuo libro preferito?";
        String risposta = "1984";

        when(utenteDAO.esisteEmail(email)).thenReturn(false);
        // Qui la throws Exception è obbligatoria perché esisteCodiceFiscale lancia SQLException
        when(utenteDAO.esisteCodiceFiscale(cf)).thenReturn(true);

        assertThrows(CodiceFiscaleGiaRegistratoException.class, () -> 
            utenteService.registraUtente(nome, cognome, cf, email, 
                            password, confermaPassword, domanda, risposta));

        verify(utenteDAO, never()).registraUtente(any(Utente.class));
    }
    
    // SEZIONE TEST LOGIN (9.2)

    @Test
    public void testTC9_2_1_Successo() throws Exception {
        String email = "mario.rossi@example.com";
        String passwordClear = "Password!123";
        
        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setPassword(BCrypt.hashpw(passwordClear, BCrypt.gensalt())); 
        utenteMock.setStato("attivo");

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        Utente result = utenteService.login(email, passwordClear);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(utenteDAO).getUtenteByEmail(email);
    }

    @Test
    public void testTC9_2_2_EmailNonTrovata() throws Exception {
        String email = "inesistente@example.com";
        String password = "Password!123";

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(null);

        assertThrows(CredenzialiNonValideException.class, () -> {
            utenteService.login(email, password);
        });
    }

    @Test
    public void testTC9_2_3_PasswordErrata() throws Exception {
        String email = "mario.rossi@example.com";
        String passwordCorretta = "Password!123";
        String passwordErrata = "PasswordErrata!123";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setPassword(BCrypt.hashpw(passwordCorretta, BCrypt.gensalt()));
        utenteMock.setStato("attivo");

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        assertThrows(CredenzialiNonValideException.class, () -> {
            utenteService.login(email, passwordErrata);
        });
    }

    @Test
    public void testTC9_2_4_UtenteInAttesa() throws Exception {
        String email = "nuovo.utente@example.com";
        String password = "Password!123";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        utenteMock.setStato("in_attesa");

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        assertThrows(UtenteNonAbilitatoException.class, () -> {
            utenteService.login(email, password);
        });
    }

    //SEZIONE TEST RECUPERO PASSWORD (9.3)
    
    @Test
    public void testTC9_3_1_ResetSuccesso() throws Exception {
        String email = "mario.rossi@example.com";
        String rispostaCorretta = "Fido";
        String nuovaPass = "PasswordNuova!1";
        String confermaPass = "PasswordNuova!1";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setRispostaSicurezza(rispostaCorretta);

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        utenteService.resetPassword(email, rispostaCorretta, nuovaPass, confermaPass);

        verify(utenteDAO).updatePassword(eq(email), anyString());
    }

    @Test
    public void testTC9_3_2_EmailNonTrovata() throws Exception {
        String email = "inesistente@example.com";
        String risposta = "Qualsiasi";
        String pass = "Password!1";

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(null);

        assertThrows(UtenteNonTrovatoException.class, () -> {
            utenteService.resetPassword(email, risposta, pass, pass);
        });
    }

    @Test
    public void testTC9_3_3_RispostaSbagliata() throws Exception {
        String email = "mario.rossi@example.com";
        String rispostaReale = "Fido";
        String rispostaErrata = "Gatto";
        String pass = "Password!1";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setRispostaSicurezza(rispostaReale);

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        assertThrows(RispostaSicurezzaErrataException.class, () -> {
            utenteService.resetPassword(email, rispostaErrata, pass, pass);
        });
    }

    @Test
    public void testTC9_3_4_FormatoPasswordErrato() throws Exception {
        String email = "mario.rossi@example.com";
        String risposta = "Fido";
        String passDebole = "123";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setRispostaSicurezza(risposta);

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        assertThrows(FormatoPasswordNonValidoException.class, () -> {
            utenteService.resetPassword(email, risposta, passDebole, passDebole);
        });
    }

    @Test
    public void testTC9_3_5_PasswordNonCorrispondenti() throws Exception {
        String email = "mario.rossi@example.com";
        String risposta = "Fido";
        String pass = "Password!1";
        String passDiversa = "Password!2";

        Utente utenteMock = new Utente();
        utenteMock.setEmail(email);
        utenteMock.setRispostaSicurezza(risposta);

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(utenteMock);

        assertThrows(PasswordNonCorrispondentiException.class, () -> {
            utenteService.resetPassword(email, risposta, pass, passDiversa);
        });
    }
    
    //SEZIONE TEST APPROVAZIONE REGISTRAZIONE UTENTE (9.4)
    
    @Test
    public void testTC9_4_1_ApprovazioneSuccesso() throws Exception {
        String email = "nuovo@example.com";
        Utente u = new Utente();
        u.setEmail(email);
        u.setStato("in_attesa");

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(u);

        utenteService.accettaUtente(email);

        verify(utenteDAO).updateStato(email, "attivo");
    }

    @Test
    public void testTC9_4_2_EmailNull() {
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            utenteService.accettaUtente(null);
        });
    }

    @Test
    public void testTC9_4_3_EmailNonTrovata() {
        String email = "inesistente@example.com";
        when(utenteDAO.getUtenteByEmail(email)).thenReturn(null);

        assertThrows(UtenteNonTrovatoException.class, () -> {
            utenteService.accettaUtente(email);
        });
    }

    @Test
    public void testTC9_4_4_StatoNonInAttesa() {
        String email = "gia.attivo@example.com";
        Utente u = new Utente();
        u.setEmail(email);
        u.setStato("attivo");

        when(utenteDAO.getUtenteByEmail(email)).thenReturn(u);

        assertThrows(StatoUtenteNonValidoException.class, () -> {
            utenteService.accettaUtente(email);
        });
    }
    
    
    
    
    
    
    
    
    
    
    
 
}