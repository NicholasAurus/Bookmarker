package it.bookmarker.service;

import java.sql.Date;
import it.bookmarker.dao.LibriDAO;
import it.bookmarker.model.Libro;
import java.util.List;

public class LibroService {

    private LibriDAO libriDAO;

    // Dependency Injection
    public LibroService(LibriDAO libriDAO) {
        this.libriDAO = libriDAO;
    }

    public boolean aggiungiLibro(String titolo, String autore, String genere, String copieStr, String dataPubStr, String copertina, String descrizione) {
        try {
        	
            // Parsing dei dati (da Stringa a Tipi corretti)
            int copie = Integer.parseInt(copieStr);
            Date dataPub = Date.valueOf(dataPubStr); //Formato atteso: yyyy-mm-dd 
            //aggiungere controllo anno
            //Creazione dell'oggetto Libro
            Libro nuovo = new Libro();
            nuovo.setTitolo(titolo);
            nuovo.setAutore(autore);
            nuovo.setGenere(genere);
            nuovo.setDisponibilita(copie);
            nuovo.setDataPubblicazione(dataPub);
            nuovo.setCopertina(copertina);
            nuovo.setDescrizione(descrizione);

            //Chiamata al DAO
            libriDAO.inserisciLibro(nuovo);
            
            return true; // Se arriviamo qui, ok

        } catch (Exception e) {
            //errori SQL del DAO, errori di parsing
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean aggiornaDisponibilita(String idStr, String quantitaStr) {
        try {
            if (idStr == null || quantitaStr == null) return false;

            int id = Integer.parseInt(idStr);
            int nuoveCopie = Integer.parseInt(quantitaStr);

            // no copie negative
            if (nuoveCopie < 0) return false; 

            // Chiama il DAO e restituisce direttamente true/false
            return libriDAO.aggiornaDisponibilita(id, nuoveCopie);

        } catch (NumberFormatException e) {
            // Se i dati non sono numeri validi
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean rimuoviLibro(String idStr) {
        try {
            if (idStr == null) return false;
            
            int id = Integer.parseInt(idStr);
            
            return libriDAO.rimuoviLibro(id);
            
        } catch (NumberFormatException e) {
            // L'ID non era un numero valido
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Libro> getCatalogoCompleto() {
        return libriDAO.getAllLibri();
    }
    
    public Libro getDettaglioLibro(int id) {
        return libriDAO.getLibroById(id);
    }
}

