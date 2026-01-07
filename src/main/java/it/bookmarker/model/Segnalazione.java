package it.bookmarker.model;
import java.sql.Date;

public class Segnalazione {
    
    private int id;
    private int utenteId;     // Chi segnala
    private int recensioneId; // Quale recensione è segnalata
    private String motivo;
    private Date dataSegnalazione;
    private String stato;     // "APERTA", "CHIUSA"



    public Segnalazione() {}

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
    
    public int getRecensioneId() { return recensioneId; }
    public void setRecensioneId(int recensioneId) { this.recensioneId = recensioneId; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public Date getDataSegnalazione() { return dataSegnalazione; }
    public void setDataSegnalazione(Date dataSegnalazione) { this.dataSegnalazione = dataSegnalazione; }
    
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

}