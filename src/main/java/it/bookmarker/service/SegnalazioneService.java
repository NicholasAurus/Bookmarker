package it.bookmarker.service;

import java.util.List;

import it.bookmarker.dao.SegnalazioniDAO;
import it.bookmarker.model.Segnalazione;

public class SegnalazioneService {

    private SegnalazioniDAO segnalazioniDAO;

    public SegnalazioneService(SegnalazioniDAO segnalazioniDAO) {
        this.segnalazioniDAO = segnalazioniDAO;
    }


    public String segnalaRecensione(String idRecensioneStr, String emailUtente, String motivo) {
        
        // Controllo dati mancanti
        if (idRecensioneStr == null || emailUtente == null || motivo == null) {
            return "errore_generico";
        }

        // Lunghezza motivo
        if (motivo.trim().length() < 20) {
            return "motivo_breve";
        }

        try {
            // Parsing ID
            int idRecensione = Integer.parseInt(idRecensioneStr);
            
            boolean inserito = segnalazioniDAO.inserisciSegnalazione(idRecensione, emailUtente, motivo);
            
            if (inserito) {
                return "successo";
            } else {
                return "errore_generico";
            }

        } catch (NumberFormatException e) {
            // ID non valido
            return "errore_generico";
        } catch (Exception e) {
            e.printStackTrace();
            return "errore_generico";
        }
    }
    
    public List<Segnalazione> getTutteLeSegnalazioni() {
        return segnalazioniDAO.getAllSegnalazioni();
    }

    public boolean chiudiSegnalazione(String idStr, String note) {
        try {
            if (idStr == null || idStr.isEmpty()) {
                return false;
            }

            int id = Integer.parseInt(idStr);
            
            if (note == null) note = "";

            segnalazioniDAO.chiudiSegnalazione(id, note);
            return true;

        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}