package it.bookmarker.model;

import java.sql.Date;

public class Recensione {
    private int id;
    private int utenteId; 
    private int libroId;  
    private String testo;
    private Date dataInserimento;
    
    
    private String nomeUtenteDisplay; 

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
    
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    
    public Date getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(Date dataInserimento) { this.dataInserimento = dataInserimento; }

   
    public String getNomeUtenteDisplay() { return nomeUtenteDisplay; }
    public void setNomeUtenteDisplay(String nomeUtenteDisplay) { this.nomeUtenteDisplay = nomeUtenteDisplay; }
}