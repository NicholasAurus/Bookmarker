package it.bookmarker.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.model.Libro;
import it.bookmarker.service.PrestitoService;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.PrestitoServiceException.*;
import it.bookmarker.service.exception.LibroServiceException.*;

//Test di Unità per PrestitoService 

public class PrestitoServiceTest {

    @Mock
    private PrestitiDAO prestitiDAO;

    @Mock
    private LibriDAO libriDAO;

    @InjectMocks
    private PrestitoService prestitoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //SEZIONE TEST PRENOTAZIONE PRESTITO (9.5)
    
    @Test
    public void testTC9_5_1_PrenotazioneSuccesso() throws Exception {
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String dataValida = LocalDate.now().plusDays(1).toString(); 

        when(prestitiDAO.contaPrestitiPendenti(email)).thenReturn(0);
        when(prestitiDAO.isLibroGiaRichiesto(email, 10)).thenReturn(false);
        when(prestitiDAO.prenotaLibro(eq(email), eq(10), any(java.sql.Date.class))).thenReturn(true);

        prestitoService.prenotaLibro(email, idLibro, dataValida);

        verify(prestitiDAO).prenotaLibro(eq(email), eq(10), any(java.sql.Date.class));
    }

    @Test
    public void testTC9_5_2_EmailNull() throws Exception{
        String idLibro = "10";
        String data = LocalDate.now().toString();

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.prenotaLibro(null, idLibro, data);
        });
    }

    @Test
    public void testTC9_5_3_IdLibroNull() throws Exception{
        String email = "mario.rossi@example.com";
        String data = LocalDate.now().toString();

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.prenotaLibro(email, null, data);
        });
    }

    @Test
    public void testTC9_5_4_IdLibroFormatoErrato() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibroErrato = "abc";
        String data = LocalDate.now().toString();

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.prenotaLibro(email, idLibroErrato, data);
        });
    }

    @Test
    public void testTC9_5_5_DataNull() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        
        assertThrows(DataNonValidaException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, null);
        });
    }

    @Test
    public void testTC9_5_6_DataFormatoErrato() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String dataErrata = "31-02-2025"; 

        assertThrows(DataNonValidaException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, dataErrata);
        });
    }

    @Test
    public void testTC9_5_7_DataPassata() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String dataPassata = LocalDate.now().minusDays(1).toString();

        assertThrows(DataNonValidaException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, dataPassata);
        });
    }

    @Test
    public void testTC9_5_8_DataOltreLimite() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String dataLontana = LocalDate.now().plusDays(3).toString(); 

        assertThrows(DataNonValidaException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, dataLontana);
        });
    }

    @Test
    public void testTC9_5_9_LimitePrestitiRaggiunto() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String data = LocalDate.now().toString();

        when(prestitiDAO.contaPrestitiPendenti(email)).thenReturn(3); 

        assertThrows(LimitePrestitiSuperatoException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, data);
        });
    }

    @Test
    public void testTC9_5_10_PrenotazioneGiaEffettuata() throws Exception{
        String email = "mario.rossi@example.com";
        String idLibro = "10";
        String data = LocalDate.now().toString();

        when(prestitiDAO.contaPrestitiPendenti(email)).thenReturn(0);
        when(prestitiDAO.isLibroGiaRichiesto(email, 10)).thenReturn(true); 

        assertThrows(PrestitoGiaEsistenteException.class, () -> {
            prestitoService.prenotaLibro(email, idLibro, data);
        });
    }

    //SEZIONE TEST APPROVAZIONE PRENOTAZIONE PRESTITO (9.6)
    
    @Test
    public void testTC9_6_1_ApprovaSuccesso() throws Exception {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Richiesto");
        p.setLibroId(10);

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);
        when(libriDAO.getCopieDisponibili(10)).thenReturn(5);

        prestitoService.approvaRichiestaPrestito(idStr);

        verify(prestitiDAO).gestisciPrestito(eq(1), eq("prenotato"), any());
    }

    @Test
    public void testTC9_6_2_IdNull() {
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.approvaRichiestaPrestito(null);
        });
    }

    @Test
    public void testTC9_6_3_IdFormatoErrato() {
        String idStr = "abc";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.approvaRichiestaPrestito(idStr);
        });
    }

    @Test
    public void testTC9_6_4_PrestitoNonTrovato() {
        String idStr = "99";
        when(prestitiDAO.getPrestitoById(99)).thenReturn(null);

        assertThrows(PrestitoNonTrovatoException.class, () -> {
            prestitoService.approvaRichiestaPrestito(idStr);
        });
    }

    @Test
    public void testTC9_6_5_StatoNonValido() {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Concluso"); 

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        assertThrows(StatoPrestitoNonValidoException.class, () -> {
            prestitoService.approvaRichiestaPrestito(idStr);
        });
    }

    @Test
    public void testTC9_6_6_CopieEsaurite() {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Richiesto");
        p.setLibroId(10);

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);
        when(libriDAO.getCopieDisponibili(10)).thenReturn(0); 

        assertThrows(CopieNonDisponibiliException.class, () -> {
            prestitoService.approvaRichiestaPrestito(idStr);
        });
    }

    //SEZIONE TEST SEGNARE RITIRO PRESTITO (9.7)
    
    @Test
    public void testTC9_7_1_RitiroSuccesso() throws Exception {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("prenotato");

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        prestitoService.confermaRitiro(idStr);

        verify(prestitiDAO).confermaRitiro(1);
    }

    @Test
    public void testTC9_7_2_IdNull() {
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.confermaRitiro(null);
        });
    }

    @Test
    public void testTC9_7_3_IdFormatoErrato() {
        String idStr = "abc";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.confermaRitiro(idStr);
        });
    }

    @Test
    public void testTC9_7_4_PrestitoNonTrovato() {
        String idStr = "99";
        when(prestitiDAO.getPrestitoById(99)).thenReturn(null);

        assertThrows(PrestitoNonTrovatoException.class, () -> {
            prestitoService.confermaRitiro(idStr);
        });
    }

    @Test
    public void testTC9_7_5_StatoNonPrenotato() {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Richiesto"); 

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        assertThrows(StatoPrestitoNonValidoException.class, () -> {
            prestitoService.confermaRitiro(idStr);
        });
    }

    //SEZIONE TEST ANNULLARE UN PRESTITO (9.8)
    
    @Test
    public void testTC9_8_1_AnnullaSuccesso() throws Exception {
        String idStr = "1";
        String motivazione = "L'utente ha cambiato idea sul ritiro.";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Richiesto");
        p.setLibroId(10);

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        prestitoService.annullaPrestito(idStr, motivazione);

        verify(prestitiDAO).gestisciPrestito(1, "annullato", motivazione);
    }

    @Test
    public void testTC9_8_2_IdNull() {
        String motivazione = "Motivazione valida";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.annullaPrestito(null, motivazione);
        });
    }

    @Test
    public void testTC9_8_3_IdFormatoErrato() {
        String idStr = "abc";
        String motivazione = "Motivazione valida";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.annullaPrestito(idStr, motivazione);
        });
    }

    @Test
    public void testTC9_8_4_MotivazioneNull() {
        String idStr = "1";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.annullaPrestito(idStr, null);
        });
    }

    @Test
    public void testTC9_8_5_MotivazioneCorta() {
        String idStr = "1";
        String motivazioneCorta = "Corta";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.annullaPrestito(idStr, motivazioneCorta);
        });
    }

    @Test
    public void testTC9_8_6_PrestitoNonTrovato() {
        String idStr = "99";
        String motivazione = "Motivazione valida";
        when(prestitiDAO.getPrestitoById(99)).thenReturn(null);

        assertThrows(PrestitoNonTrovatoException.class, () -> {
            prestitoService.annullaPrestito(idStr, motivazione);
        });
    }

    @Test
    public void testTC9_8_7_StatoNonValido() {
        String idStr = "1";
        String motivazione = "Motivazione valida";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("Concluso");

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        assertThrows(StatoPrestitoNonValidoException.class, () -> {
            prestitoService.annullaPrestito(idStr, motivazione);
        });
    }

    @Test
    public void testTC9_8_8_LibroNonEsistente() {
        String idStr = "1";
        String motivazione = "Motivazione valida";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("prenotato"); 
        p.setLibroId(999); 

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);
        when(libriDAO.getCopieDisponibili(999)).thenReturn(-1); 

        assertThrows(LibroNonTrovatoException.class, () -> {
            prestitoService.annullaPrestito(idStr, motivazione);
        });
    }

    //SEZIONE TEST SEGNARE UN PRESTITO COME RESTITUITO (9.9)
    
    @Test
    public void testTC9_9_1_RestituzioneSuccesso() throws Exception {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("In Corso");
        p.setLibroId(10);

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);
        when(libriDAO.getCopieDisponibili(10)).thenReturn(5);

        prestitoService.registraRestituzione(idStr);

        verify(prestitiDAO).terminaPrestito(1);
        verify(libriDAO).aggiornaDisponibilita(10, 6);
    }

    @Test
    public void testTC9_9_2_IdNull() {
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.registraRestituzione(null);
        });
    }

    @Test
    public void testTC9_9_3_IdFormatoErrato() {
        String idStr = "abc";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            prestitoService.registraRestituzione(idStr);
        });
    }

    @Test
    public void testTC9_9_4_PrestitoNonTrovato() {
        String idStr = "99";
        when(prestitiDAO.getPrestitoById(99)).thenReturn(null);

        assertThrows(PrestitoNonTrovatoException.class, () -> {
            prestitoService.registraRestituzione(idStr);
        });
    }

    @Test
    public void testTC9_9_5_StatoNonInCorso() {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("prenotato"); 

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);

        assertThrows(StatoPrestitoNonValidoException.class, () -> {
            prestitoService.registraRestituzione(idStr);
        });
    }

    @Test
    public void testTC9_9_6_LibroNonEsistente() {
        String idStr = "1";
        Prestito p = new Prestito();
        p.setId(1);
        p.setStato("In Corso");
        p.setLibroId(999);

        when(prestitiDAO.getPrestitoById(1)).thenReturn(p);
        when(libriDAO.getCopieDisponibili(999)).thenReturn(-1);

        assertThrows(LibroNonTrovatoException.class, () -> {
            prestitoService.registraRestituzione(idStr);
        });
    }
    
}