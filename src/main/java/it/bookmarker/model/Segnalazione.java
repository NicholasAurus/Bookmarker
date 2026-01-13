package it.bookmarker.model;
import java.sql.Date;

public class Segnalazione {
    
    private int id;
    private String utenteEmail;
    private int recensioneId;
    private String motivo;
    private Date dataSegnalazione;
    private String stato;

    public Segnalazione() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }
    
    public int getRecensioneId() { return recensioneId; }
    public void setRecensioneId(int recensioneId) { this.recensioneId = recensioneId; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public Date getDataSegnalazione() { return dataSegnalazione; }
    public void setDataSegnalazione(Date dataSegnalazione) { this.dataSegnalazione = dataSegnalazione; }
    
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
}