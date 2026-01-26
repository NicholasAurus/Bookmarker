package it.bookmarker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.LibroServiceException.*;;

//Test di Unità per LibroService 

public class LibroServiceTest {

    @Mock
    private LibriDAO libriDAO;

    @InjectMocks
    private LibroService libroService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //SEZIONE TEST AGGIUNGERE UN LIBRO AL CATALOGO (9.10)
    
    @Test
    public void testTC9_10_1_AggiuntaLibroSuccesso() throws Exception {
        String titolo = "Il Signore degli Anelli";
        String autore = "J.R.R. Tolkien";
        String genere = "Fantasy";
        String copie = "10";
        String data = "1954-07-29";
        String copertina = "cover.jpg";
        String descrizione = "Un libro fantasy epico.";

        libroService.aggiungiLibro(titolo, autore, genere, copie, data, copertina, descrizione);

        verify(libriDAO).inserisciLibro(any(Libro.class));
    }

    @Test
    public void testTC9_10_2_TitoloNull() {
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String data = "2020-01-01";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(null, autore, genere, copie, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_3_AutoreNull() {
        String titolo = "Titolo";
        String genere = "Genere";
        String copie = "5";
        String data = "2020-01-01";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, null, genere, copie, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_4_GenereNull() {
        String titolo = "Titolo";
        String autore = "Autore";
        String copie = "5";
        String data = "2020-01-01";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, null, copie, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_5_CopieNull() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String data = "2020-01-01";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, null, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_6_CopieNegative() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "-1";
        String data = "2020-01-01";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(CopieNegativeException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_7_DataNull() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, null, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_8_DataFuturaNonValida() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String dataFutura = LocalDate.now().plusYears(2).toString(); 
        String copertina = "img.jpg";
        String descrizione = "Desc";

        assertThrows(DataNonValidaException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, dataFutura, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_9_CopertinaNull() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String data = "2020-01-01";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, data, null, descrizione);
        });
    }

    @Test
    public void testTC9_10_10_CopertinaFormatoErrato() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String data = "2020-01-01";
        String copertina = "file.txt";
        String descrizione = "Desc";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, data, copertina, descrizione);
        });
    }

    @Test
    public void testTC9_10_11_DescrizioneNull() {
        String titolo = "Titolo";
        String autore = "Autore";
        String genere = "Genere";
        String copie = "5";
        String data = "2020-01-01";
        String copertina = "img.jpg";

        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiungiLibro(titolo, autore, genere, copie, data, copertina, null);
        });
    }

    //SEZIONE TEST MODIFICA COPIE DISPONIBILI (9.11)

    @Test
    public void testTC9_11_1_AggiornamentoSuccesso() throws Exception {
        String idStr = "1";
        String nuoveCopie = "50";

        when(libriDAO.aggiornaDisponibilita(1, 50)).thenReturn(true);

        libroService.aggiornaDisponibilita(idStr, nuoveCopie);

        verify(libriDAO).aggiornaDisponibilita(1, 50);
    }

    @Test
    public void testTC9_11_2_IdNull() {
        String copie = "10";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiornaDisponibilita(null, copie);
        });
    }

    @Test
    public void testTC9_11_3_CopieNull() {
        String idStr = "1";
        assertThrows(FormatoDatiNonValidoException.class, () -> {
            libroService.aggiornaDisponibilita(idStr, null);
        });
    }

    @Test
    public void testTC9_11_4_CopieNegative() {
        String idStr = "1";
        String copieNegative = "-5";

        assertThrows(CopieNegativeException.class, () -> {
            libroService.aggiornaDisponibilita(idStr, copieNegative);
        });
    }

}