package it.bookmarker.model;

import java.sql.Date;

public class Prestito {

    private int id;
    private String utenteEmail;
    private int libroId;
    private Date dataInizio;
    private Date dataFinePrevista;
    private Date dataRestituzioneEffettiva;

    private String titoloLibro;
    private String autoreLibro;
    private String copertinaLibro;
    private String descrizioneLibro;
    
    private boolean recensito; 

    public Prestito() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }
    
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    
    public Date getDataInizio() { return dataInizio; }
    public void setDataInizio(Date dataInizio) { this.dataInizio = dataInizio; }
    
    public Date getDataFinePrevista() { return dataFinePrevista; }
    public void setDataFinePrevista(Date dataFinePrevista) { this.dataFinePrevista = dataFinePrevista; }
    
    public Date getDataRestituzioneEffettiva() { return dataRestituzioneEffettiva; }
    public void setDataRestituzioneEffettiva(Date dataRestituzioneEffettiva) { this.dataRestituzioneEffettiva = dataRestituzioneEffettiva; }

    public String getTitoloLibro() { return titoloLibro; }
    public void setTitoloLibro(String titoloLibro) { this.titoloLibro = titoloLibro; }
    
    public String getAutoreLibro() { return autoreLibro; }
    public void setAutoreLibro(String autoreLibro) { this.autoreLibro = autoreLibro; }
    
    public String getCopertinaLibro() { return copertinaLibro; }
    public void setCopertinaLibro(String copertinaLibro) { this.copertinaLibro = copertinaLibro; }
    
    public String getDescrizioneLibro() { return descrizioneLibro; }
    public void setDescrizioneLibro(String descrizioneLibro) { this.descrizioneLibro = descrizioneLibro; }
    
    public boolean isRecensito() { return recensito; }
    public void setRecensito(boolean recensito) { this.recensito = recensito; }
}