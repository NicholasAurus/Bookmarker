package it.bookmarker.service;

import java.sql.Date;
import java.util.List;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;
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

    public String prenotaLibro(String emailUtente, String idLibroStr, String dataRitiroStr) {
        try {
            if (emailUtente == null || idLibroStr == null) {
                return "Dati mancanti.";
            }

            if (dataRitiroStr == null || dataRitiroStr.trim().isEmpty()) {
                return "Devi selezionare una data per il ritiro.";
            }

            int idLibro;
            try {
                idLibro = Integer.parseInt(idLibroStr);
            } catch (NumberFormatException e) {
                return "ID libro non valido.";
            }

            LocalDate dataScelta;
            try {
                dataScelta = LocalDate.parse(dataRitiroStr);
            } catch (Exception e) {
                return "Formato data non valido.";
            }

            LocalDate today = LocalDate.now();
            LocalDate limiteMassimo = today.plusDays(2);

            if (dataScelta.isBefore(today)) {
                 return "Non puoi selezionare una data passata."; 
            }
            
            if (dataScelta.isAfter(limiteMassimo)) {
                 return "Puoi prenotare il ritiro solo entro i prossimi 2 giorni.";
            }

            if (prestitiDAO.contaPrestitiPendenti(emailUtente) >= 3) {
                return "Hai raggiunto il limite massimo di 3 prenotazioni attive.";
            }

            if (prestitiDAO.isLibroGiaRichiesto(emailUtente, idLibro)) {
                return "Hai già richiesto o hai già in prestito questo libro";
            }
            
            Date dataRitiroSQL = Date.valueOf(dataScelta);

            boolean esito = prestitiDAO.prenotaLibro(emailUtente, idLibro, dataRitiroSQL);
            
            if (esito) {
                return null; // SUCCESSO
            } else {
                return "Errore generico nel database durante la prenotazione.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore tecnico del server.";
        }
    }
    
    public String approvaRichiestaPrestito(String idStr) {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            return "ID mancante.";
        }

        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return "Formato ID non valido.";
        }

        // Esistenza Prestito 
        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            return "Prestito non trovato.";
        }

        // Controllo Stato("Richiesto")
        if (!"Richiesto".equals(p.getStato())) {
            return "Impossibile approvare: la richiesta non è in stato 'Richiesto' (Stato attuale: " + p.getStato() + ")";
        }

        // Controllo Disponibilità Libro
        int copieDisponibili = libriDAO.getCopieDisponibili(p.getLibroId());
        if (copieDisponibili <= 0) {
            return "Impossibile approvare: non ci sono copie disponibili per questo libro.";
        }
        
        boolean esito = prestitiDAO.gestisciPrestito(idPrestito, "prenotato", null);
        
        
        if (esito) {
            return null;
        } else {
            return "Errore durante l'aggiornamento del prestito.";
        }
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
    
    public String confermaRitiro(String idStr) {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            return "ID mancante.";
        }

        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return "Formato ID non valido.";
        }

        // Esistenza Prestito
        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            return "Prestito non trovato.";
        }

        // Controllo Stato
        if (!"prenotato".equals(p.getStato())) {
            return "Impossibile confermare il ritiro: il prestito non è in stato 'prenotato'.";
        }

        prestitiDAO.confermaRitiro(idPrestito);
        
       
        return null;
    }

    public String annullaPrestito(String idStr, String motivazione) {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            return "ID mancante.";
        }
        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return "Formato ID non valido.";
        }

        // Validità Motivazione
        if (motivazione == null || motivazione.trim().length() < 10) {
            return "La motivazione è obbligatoria e deve contenere almeno 10 caratteri.";
        }

        // Controllo Esistenza Prestito
        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            return "Prestito non trovato.";
        }

        // Controllo Stato
        String statoAttuale = p.getStato();
        if (!"Richiesto".equals(statoAttuale) && !"prenotato".equals(statoAttuale)) {
            return "Impossibile annullare: il prestito si trova nello stato '" + statoAttuale + "'.";
        }

        // Gestione Copie
        if ("prenotato".equals(statoAttuale)) {
            try {
                int copieAttuali = libriDAO.getCopieDisponibili(p.getLibroId());
                if (copieAttuali >= 0) {
                    libriDAO.aggiornaDisponibilita(p.getLibroId(), copieAttuali + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        prestitiDAO.gestisciPrestito(idPrestito, "annullato", motivazione);
        
       
        return null;
    }

    public String registraRestituzione(String idStr) {
        
        if (idStr == null || idStr.trim().isEmpty()) {
            return "ID mancante.";
        }
        int idPrestito;
        try {
            idPrestito = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return "Formato ID non valido.";
        }

        // Controllo prestito
        Prestito p = prestitiDAO.getPrestitoById(idPrestito);
        if (p == null) {
            return "Prestito non trovato.";
        }

        // Controllo Stato
        if (!"In Corso".equals(p.getStato())) {
            return "Impossibile registrare restituzione: il prestito non è 'In Corso'. Stato attuale: " + p.getStato();
        }

         // Chiude il prestito
         prestitiDAO.terminaPrestito(idPrestito);
            
         // Incrementa le copie disponibili del libro
         int copieAttuali = libriDAO.getCopieDisponibili(p.getLibroId());
         // Se libro esiste
         if (copieAttuali >= 0) {
             libriDAO.aggiornaDisponibilita(p.getLibroId(), copieAttuali + 1);
         }
            
        
         return null;
    }
    
    public List<Prestito> getStoricoUtente(String email) {
        if (email == null) return null;
        return prestitiDAO.getStoricoByUtente(email);
    }
    
    public List<Prestito> getRichiesteInAttesa() {
        return prestitiDAO.getPrestitiRichiesti();
    }
}