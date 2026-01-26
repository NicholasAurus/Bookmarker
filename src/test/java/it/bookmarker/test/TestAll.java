package it.bookmarker.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import it.bookmarker.service.LibroServiceTest;
import it.bookmarker.service.PrestitoServiceTest;
import it.bookmarker.service.UtenteServiceTest;

@Suite
@SelectClasses({
    UtenteServiceTest.class,    // Test su Registrazione, Login, Reset Password, Gestione Utenti
    PrestitoServiceTest.class,  // Test su Prenotazione, Approvazione, Ritiro, Restituzione, Annullamento
    LibroServiceTest.class      // Test su Aggiunta Libro, Modifica Disponibilità
})
public class TestAll {
	
}