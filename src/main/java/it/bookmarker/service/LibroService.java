package it.bookmarker.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import it.bookmarker.dao.LibriDAO;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Libro;

public class LibroService {

    private LibriDAO libriDAO;
    private PrestitiDAO prestitiDAO;

    public LibroService(LibriDAO libriDAO) {
        this.libriDAO = libriDAO;
        this.prestitiDAO = new PrestitiDAO();
    }


    public String aggiungiLibro(String titolo, String autore, String genere, String copieStr, String dataPubStr, String copertina, String descrizione) {
        
        //CONTROLLO CAMPI OBBLIGATORI (Stringhe vuote o null)
        if (titolo == null || titolo.trim().isEmpty()) {
            return "Il titolo è obbligatorio.";
        }
        if (autore == null || autore.trim().isEmpty()) {
            return "L'autore è obbligatorio.";
        }
        if (genere == null || genere.trim().isEmpty()) {
            return "Il genere è obbligatorio.";
        }
        if (copieStr == null || copieStr.trim().isEmpty()) {
            return "Il numero di copie è obbligatorio.";
        }
        if (dataPubStr == null || dataPubStr.trim().isEmpty()) {
            return "La data di pubblicazione è obbligatoria.";
        }
        if (copertina == null || copertina.trim().isEmpty()) {
            return "La copertina è obbligatoria.";
        }
        if(descrizione == null || descrizione.trim().isEmpty()) {
        	return "La descrizione è obbligatoria.";
        }
        
        String copertinaLower = copertina.toLowerCase();
        if (!copertinaLower.endsWith(".jpg") && 
            !copertinaLower.endsWith(".jpeg") && 
            !copertinaLower.endsWith(".png")) {
            return "Il file della copertina deve essere nei seguenti formati: jpg, png, jpeg";
        }

            //CONTROLLO NUMERICO COPIE
            int copie;
            try {
                copie = Integer.parseInt(copieStr);
            } catch (NumberFormatException e) {
                return "Il campo 'Copie' deve contenere un numero intero valido.";
            }

            if (copie < 0) {
                return "Il numero di copie non può essere negativo.";
            }

            //CONTROLLO DATA
            Date dataPub;
            try {
                dataPub = Date.valueOf(dataPubStr); // Formato yyyy-mm-dd
            } catch (IllegalArgumentException e) {
                return "Formato data non valido.";
            }
            
            //Data futura prevista: al massimo un anno (magari si deve inserire un libro che uscirà poi)
            LocalDate localDataPub = dataPub.toLocalDate();
            if (localDataPub.getYear() > LocalDate.now().getYear()) {
                return "L'anno di pubblicazione non è valido";
            }

            
            Libro libro = new Libro();
            libro.setTitolo(titolo);
            libro.setAutore(autore);
            libro.setGenere(genere);
            libro.setDisponibilita(copie);
            libro.setDataPubblicazione(dataPub);
            libro.setCopertina(copertina);
            libro.setDescrizione(descrizione);

            //Chiamata al DAO
            boolean esito = libriDAO.inserisciLibro(libro); 

            if (esito) {
                return null; // Ritorna null per indicare "Nessun Errore"
            } else {
                return "Errore durante il salvataggio nel database.";
            }

    }
    
    public String aggiornaDisponibilita(String idStr, String quantitaStr) {
            if (idStr == null || idStr.trim().isEmpty()) return "ID libro mancante.";
            if (quantitaStr == null || quantitaStr.trim().isEmpty()) return "Quantità mancante.";
            
            int id;
            int nuoveCopie;

            try {
                id = Integer.parseInt(idStr.trim());
                nuoveCopie = Integer.parseInt(quantitaStr.trim());
            } catch (NumberFormatException e) {
                return "Devi inserire un numero intero valido.";
            }

            if (nuoveCopie < 0) {
                return "La disponibilità non può essere negativa.";
            }
            
            boolean esito = libriDAO.aggiornaDisponibilita(id, nuoveCopie);

            if (esito) {
                return null; // successo
            } else {
                return "Errore nel salvataggio sul database.";
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