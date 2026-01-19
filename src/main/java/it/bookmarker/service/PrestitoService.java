package it.bookmarker.service;

import java.sql.Date;
import java.util.List;
import it.bookmarker.dao.PrestitiDAO;
import it.bookmarker.model.Prestito;

public class PrestitoService {

    private PrestitiDAO prestitiDAO;

    public PrestitoService(PrestitiDAO prestitiDAO) {
        this.prestitiDAO = prestitiDAO;
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

    public boolean confermaRitiro(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            prestitiDAO.confermaRitiro(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean prenotaLibro(String emailUtente, String idLibroStr, String dataRitiroStr) {
        try {
            if (emailUtente == null || idLibroStr == null || dataRitiroStr == null) {
                return false;
            }
            if (dataRitiroStr.trim().isEmpty()) {
                return false;
            }

            //Parsing dei dati
            int idLibro = Integer.parseInt(idLibroStr);
            Date dataRitiro = Date.valueOf(dataRitiroStr); // Formato yyyy-mm-dd

            //Controllo validità data 
            if (dataRitiro.before(new java.util.Date())) {
                 return false; 
            }

            //Chiamata al DAO
            return prestitiDAO.prenotaLibro(emailUtente, idLibro, dataRitiro);

        } catch (IllegalArgumentException | NullPointerException e) {
            // Cattura errori di formato data o numero
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean annullaPrestito(String idStr, String motivazione) {
        try {
            int id = Integer.parseInt(idStr);
            if (motivazione == null) motivazione = "";
            
            prestitiDAO.gestisciPrestito(id, "annullato", motivazione);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registraRestituzione(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            prestitiDAO.terminaPrestito(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Prestito> getStoricoUtente(String email) {
        if (email == null) return null;
        return prestitiDAO.getStoricoByUtente(email);
    }
    
    public List<Prestito> getRichiesteInAttesa() {
        return prestitiDAO.getPrestitiRichiesti();
    }

    public boolean approvaRichiestaPrestito(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            //"prenotato" è lo stato che conferma la richiesta
            return prestitiDAO.gestisciPrestito(id, "prenotato", null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
}
