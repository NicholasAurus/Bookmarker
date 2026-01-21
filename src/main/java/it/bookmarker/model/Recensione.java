package it.bookmarker.model;

import java.sql.Date;

public class Recensione {
    private int id;
    private String utenteEmail; 
    private int libroId;   
    private String testo;
    private Date dataInserimento;
    private int voto; 
    private String nomeUtenteDisplay; 
    private boolean visibile;
    private boolean eliminata;
    
    

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUtenteEmail() { return utenteEmail; }
    public void setUtenteEmail(String utenteEmail) { this.utenteEmail = utenteEmail; }
    
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

    public boolean isVisibile() { return visibile; }
    public void setVisibile(boolean visibile) { this.visibile = visibile; }
    public boolean isEliminata() { return eliminata; }
    public void setEliminata(boolean eliminata) { this.eliminata = eliminata; }
}