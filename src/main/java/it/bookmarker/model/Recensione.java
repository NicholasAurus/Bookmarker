package it.bookmarker.model;

import java.sql.Date;

public class Recensione {
    private int id;
    private int utenteId; 
    private int libroId;  
    private String testo;
    private Date dataInserimento;
    
    // *** NUOVO CAMPO: VOTO ***
    private int voto; // Valore da 1 a 5
    
    private String nomeUtenteDisplay; 

    // --- GETTER E SETTER ---

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

    
    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    
    public String getNomeUtenteDisplay() { return nomeUtenteDisplay; }
    public void setNomeUtenteDisplay(String nomeUtenteDisplay) { this.nomeUtenteDisplay = nomeUtenteDisplay; }
}