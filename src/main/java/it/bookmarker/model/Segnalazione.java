package it.bookmarker.model;
import java.sql.Date;

public class Segnalazione {
    private int id;
    private int recensioneId;
    private String utenteEmail;
    private String motivo; 
    private Date dataSegnalazione;
    private String stato;
    
    
    private int libroId;       
    private String noteChiusura;

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRecensioneId() { return recensioneId; }
    public void setRecensioneId(int recensioneId) { this.recensioneId = recensioneId; }
    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Date getDataSegnalazione() { return dataSegnalazione; }
    public void setDataSegnalazione(Date dataSegnalazione) { this.dataSegnalazione = dataSegnalazione; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    public String getNoteChiusura() { return noteChiusura; }
    public void setNoteChiusura(String noteChiusura) { this.noteChiusura = noteChiusura; }
}