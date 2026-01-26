package it.bookmarker.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.PrestitoServiceException.*;
import it.bookmarker.service.exception.LibroServiceException.*;

import java.time.LocalDate;

public class PrestitoService {

    private PrestitiDAO prestitiDAO;
    private LibriDAO libriDAO;

    
    public PrestitoService(PrestitiDAO prestitiDAO, LibriDAO libriDAO) {
        this.prestitiDAO = prestitiDAO;
        this.libriDAO = libriDAO;
    }

    public List<Prestito> getPrenotati() {
        return prestitiDAO.getPrestitiPrenotati();
    }

    public List<Prestito> getAttivi() {
        return prestitiDAO.getPrestitiAttivi();
    }

    public List<Prestito> getRestituiti() {
        return prestitiDAO.getPrestitiRestituiti();
    }

    public void prenotaLibro(String emailUtente, String idLibroStr, String dataRitiroStr) 
            throws FormatoDatiNonValidoException, DataNonValidaException, LimitePrestitiSuperatoException, 
                   PrestitoGiaEsistenteException, SQLException {
        
        if (emailUtente == null) {
            throw new FormatoDatiNonValidoException("Dati mancanti.");
        }
        
        if (idLibroStr == null) {
            throw new FormatoDatiNonValidoException("Dati mancanti.");
        }

        if (dataRitiroStr == null || dataRitiroStr.trim().isEmpty()) {
            throw new DataNonValidaException("Devi selezionare una data per il ritiro.");
        }

        int idLibro;
        try {
            idLibro = Integer.parseInt(idLibroStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("ID libro non valido.");
        }

        LocalDate dataScelta;
        try {
            dataScelta = LocalDate.parse(dataRitiroStr);
        } catch (Exception e) {
            throw new DataNonValidaException("Formato data non valido.");
        }

        LocalDate today = LocalDate.now();
        LocalDate limiteMassimo = today.plusDays(2);

        if (dataScelta.isBefore(today)) {
             throw new DataNonValidaException("Non puoi selezionare una data passata."); 
        }
        
        if (dataScelta.isAfter(limiteMassimo)) {
             throw new DataNonValidaException("Puoi prenotare il ritiro solo entro i prossimi 2 giorni.");
        }
        
        if (prestitiDAO.contaPrestitiPendenti(emailUtente) >= 3) {
            throw new LimitePrestitiSuperatoException("Hai raggiunto il limite massimo di 3 prenotazioni attive.");
        }

        if (prestitiDAO.isLibroGiaRichiesto(emailUtente, idLibro)) {
            throw new PrestitoGiaEsistenteException("Hai già richiesto o hai già in prestito questo libro");
        }
        
        Date dataRitiroSQL = Date.valueOf(dataScelta);

        prestitiDAO.prenotaLibro(emailUtente, idLibro, dataRitiroSQL);
    }
    
    public void approvaRichiestaPrestito(String idStr) 
            throws FormatoDatiNonValidoException, PrestitoNonTrovatoException, 
                   StatoPrestitoNonValidoException, CopieNonDisponibiliException {

        if (idStr == null || idStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("ID mancante.");
        }

        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Formato ID non valido.");
        }

        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            throw new PrestitoNonTrovatoException("Prestito non trovato.");
        }

        if (!"Richiesto".equals(p.getStato())) {
            throw new StatoPrestitoNonValidoException("Impossibile approvare: la richiesta non è in stato 'Richiesto'.");
        }

        int copieDisponibili = libriDAO.getCopieDisponibili(p.getLibroId());
        if (copieDisponibili <= 0) {
            throw new CopieNonDisponibiliException("Impossibile approvare: non ci sono copie disponibili per questo libro.");
        }
        
        prestitiDAO.gestisciPrestito(idPrestito, "prenotato", null);
    }

    public boolean rifiutaRichiestaPrestito(String idStr, String motivazione) {
        try {
            int id = Integer.parseInt(idStr);
            return prestitiDAO.gestisciPrestito(id, "rifiutato", motivazione);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void confermaRitiro(String idStr) 
            throws FormatoDatiNonValidoException, PrestitoNonTrovatoException, StatoPrestitoNonValidoException {

        if (idStr == null || idStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("ID mancante.");
        }

        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Formato ID non valido.");
        }

        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            throw new PrestitoNonTrovatoException("Prestito non trovato.");
        }

        if (!"prenotato".equals(p.getStato())) {
            throw new StatoPrestitoNonValidoException("Impossibile confermare il ritiro: il prestito non è in stato 'prenotato'.");
        }

        prestitiDAO.confermaRitiro(idPrestito);
    }

    public void annullaPrestito(String idStr, String motivazione) 
            throws FormatoDatiNonValidoException, PrestitoNonTrovatoException, 
                   StatoPrestitoNonValidoException, LibroNonTrovatoException {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("ID mancante.");
        }

        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Formato ID non valido.");
        }

        if (motivazione == null || motivazione.trim().length() < 10) {
            throw new FormatoDatiNonValidoException("La motivazione è obbligatoria e deve contenere almeno 10 caratteri.");
        }

        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            throw new PrestitoNonTrovatoException("Prestito non trovato.");
        }

        String statoAttuale = p.getStato();
        if (!"Richiesto".equals(statoAttuale) && !"prenotato".equals(statoAttuale)) {
            throw new StatoPrestitoNonValidoException("Impossibile annullare: il prestito si trova nello stato '" + statoAttuale + "'.");
        }

        if ("prenotato".equals(statoAttuale)) {
            int copieAttuali = libriDAO.getCopieDisponibili(p.getLibroId());
            
            if (copieAttuali == -1) { 
                throw new LibroNonTrovatoException("Il libro associato al prestito non esiste.");
            }

            libriDAO.aggiornaDisponibilita(p.getLibroId(), copieAttuali + 1);
        }

        prestitiDAO.gestisciPrestito(idPrestito, "annullato", motivazione);
    }

    public void registraRestituzione(String idStr) 
            throws FormatoDatiNonValidoException, PrestitoNonTrovatoException, 
                   StatoPrestitoNonValidoException, LibroNonTrovatoException {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("ID mancante.");
        }
        
        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Formato ID non valido.");
        }

        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            throw new PrestitoNonTrovatoException("Prestito non trovato.");
        }

        if (!"In Corso".equals(p.getStato())) {
            throw new StatoPrestitoNonValidoException("Impossibile registrare restituzione: il prestito non è 'In Corso'.");
        }

        int copieAttuali = libriDAO.getCopieDisponibili(p.getLibroId());
        
        if (copieAttuali == -1) {
            throw new LibroNonTrovatoException("Il libro associato al prestito non esiste.");
        }

        prestitiDAO.terminaPrestito(idPrestito);
        libriDAO.aggiornaDisponibilita(p.getLibroId(), copieAttuali + 1);
    }
    
    public List<Prestito> getStoricoUtente(String email) {
        if (email == null) return null;
        return prestitiDAO.getStoricoByUtente(email);
    }
    
    public List<Prestito> getRichiesteInAttesa() {
        return prestitiDAO.getPrestitiRichiesti();
    }
}