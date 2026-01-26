package it.bookmarker.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Libro;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.LibroServiceException.*;

public class LibroService {

    private LibriDAO libriDAO;
    private PrestitiDAO prestitiDAO;

    public LibroService(LibriDAO libriDAO) {
        this.libriDAO = libriDAO;
        this.prestitiDAO = new PrestitiDAO();
    }


    public void aggiungiLibro(String titolo, String autore, String genere, String copieStr, String dataPubStr, String copertina, String descrizione) 
            throws FormatoDatiNonValidoException, DataNonValidaException, CopieNegativeException {
        
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("Il titolo è obbligatorio.");
        }
        if (autore == null || autore.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("L'autore è obbligatorio.");
        }
        if (genere == null || genere.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("Il genere è obbligatorio.");
        }
        if (copieStr == null || copieStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("Il numero di copie è obbligatorio.");
        }
        if (dataPubStr == null || dataPubStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("La data di pubblicazione è obbligatoria.");
        }
        if (copertina == null || copertina.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("La copertina è obbligatoria.");
        }
        if(descrizione == null || descrizione.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("La descrizione è obbligatoria.");
        }
        
        String copertinaLower = copertina.toLowerCase();
        if (!copertinaLower.endsWith(".jpg") && 
            !copertinaLower.endsWith(".jpeg") && 
            !copertinaLower.endsWith(".png")) {
            throw new FormatoDatiNonValidoException("Il file della copertina deve essere nei seguenti formati: jpg, png, jpeg");
        }

        int copie;
        try {
            copie = Integer.parseInt(copieStr);
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Il campo 'Copie' deve contenere un numero intero valido.");
        }

        if (copie < 0) {
            throw new CopieNegativeException("Il numero di copie non può essere negativo.");
        }

        Date dataPub;
        try {
            dataPub = Date.valueOf(dataPubStr);
        } catch (IllegalArgumentException e) {
            throw new DataNonValidaException("Formato data non valido.");
        }
        
        LocalDate localDataPub = dataPub.toLocalDate();
        if (localDataPub.getYear() > LocalDate.now().getYear()) {
            throw new DataNonValidaException("L'anno di pubblicazione non è valido");
        }

        Libro libro = new Libro();
        libro.setTitolo(titolo);
        libro.setAutore(autore);
        libro.setGenere(genere);
        libro.setDisponibilita(copie);
        libro.setDataPubblicazione(dataPub);
        libro.setCopertina(copertina);
        libro.setDescrizione(descrizione);

        libriDAO.inserisciLibro(libro); 
    }
    
    public void aggiornaDisponibilita(String idStr, String quantitaStr) 
            throws FormatoDatiNonValidoException, CopieNegativeException, LibroNonTrovatoException {
            
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("ID libro mancante.");
        }
        if (quantitaStr == null || quantitaStr.trim().isEmpty()) {
            throw new FormatoDatiNonValidoException("Quantità mancante.");
        }
        
        int id;
        int nuoveCopie;

        try {
            id = Integer.parseInt(idStr.trim());
            nuoveCopie = Integer.parseInt(quantitaStr.trim());
        } catch (NumberFormatException e) {
            throw new FormatoDatiNonValidoException("Devi inserire un numero intero valido.");
        }

        if (nuoveCopie < 0) {
            throw new CopieNegativeException("La disponibilità non può essere negativa.");
        }
        
        boolean esito = libriDAO.aggiornaDisponibilita(id, nuoveCopie);

        if (!esito) {
            throw new LibroNonTrovatoException("Errore: Impossibile aggiornare, libro non trovato.");
        }
    }
    
    public String rimuoviLibro(String idStr) {
        try {
            if (idStr == null || idStr.trim().isEmpty()) return "ID mancante";
            
            int id = Integer.parseInt(idStr);
            
            if (prestitiDAO.esistonoPrestitiAttiviPerLibro(id)) {
                return "Impossibile eliminare il libro: ci sono prestiti attivi (Richiesti, Prenotati o In Corso).";
            }
            
            boolean esito = libriDAO.rimuoviLibro(id);
            return esito ? null : "Errore generico durante l'eliminazione.";
            
        } catch (NumberFormatException e) {
            return "ID non valido";
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore tecnico del server.";
        }
    }
    
    public List<Libro> getCatalogoCompleto() {
        return libriDAO.getAllLibri();
    }
    
    public Libro getDettaglioLibro(int id) {
        return libriDAO.getLibroById(id);
    }
}