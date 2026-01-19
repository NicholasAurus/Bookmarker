package it.bookmarker.service;

import it.bookmarker.dao.RecensioneDAO;
import java.util.List;
import it.bookmarker.model.Recensione;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import it.bookmarker.model.Prestito;


public class RecensioneService {

    private RecensioneDAO recensioneDAO;

    public RecensioneService(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = recensioneDAO;
    }

    public boolean aggiungiRecensione(String emailUtente, String idLibroStr, String testo, String votoStr) {
    	
        //Controlla che i parametri fondamentali non siano nulli
        if (idLibroStr != null && votoStr != null) {
            try {
                //Parsing
                int idLibro = Integer.parseInt(idLibroStr);
                int voto = Integer.parseInt(votoStr);

                //Chiamata al DAO
                return recensioneDAO.salvaRecensione(emailUtente, idLibro, testo, voto);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    public List<Recensione> getRecensioniPerLibro(int idLibro) {
        return recensioneDAO.getRecensioniByLibro(idLibro);
    }
    
    public List<Recensione> getRecensioniPubbliche(int idLibro) {
        return recensioneDAO.getRecensioniPubbliche(idLibro);
    }
    
    public Map<Integer, Recensione> getMappaRecensioniPerStorico(String email, List<Prestito> storico) {
        Map<Integer, Recensione> mappa = new HashMap<>();
        
        if (email == null || storico == null) {
            return mappa; // Ritorna mappa vuota
        }

        for (Prestito p : storico) {
            // Se il prestito risulta recensito, cerchiamo la recensione
            if (p.isRecensito()) {
                Recensione r = recensioneDAO.getRecensioneByUtenteAndLibro(email, p.getLibroId());
                if (r != null) {
                    mappa.put(p.getLibroId(), r);
                }
            }
        }
        return mappa;
    }
    
    public boolean deleteRecensioneUtente(String emailUtente, String idLibroStr) {
        try {
            if (emailUtente == null || idLibroStr == null) return false;
            
            int idLibro = Integer.parseInt(idLibroStr);
            
            // Chiama il metodo "sicuro" del DAO che controlla l'email
            return recensioneDAO.deleteRecensioneUtente(emailUtente, idLibro);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODO PER IL MODERATORE (Cancella QUALSIASI recensione) ---
    public boolean deleteRecensioneModeratore(String idRecensioneStr) {
        try {
            if (idRecensioneStr == null) return false;

            int idRecensione = Integer.parseInt(idRecensioneStr);
            
            // Chiama il metodo "potente" del DAO che usa l'ID primario
            return recensioneDAO.deleteRecensioneModeratore(idRecensione);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean impostaVisibilita(String idStr, boolean visibile) {
        try {
            int id = Integer.parseInt(idStr);
            // true = mostra, false = nascondi
            return recensioneDAO.cambiaVisibilita(id, visibile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}