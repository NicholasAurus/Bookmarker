package it.bookmarker.model;

import java.sql.Date; 

public class Utente {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String recapito;       
    private String email;          
    private String password;       
    private String ruolo;
    private Date data_registrazione;

    public Utente() {
        this.ruolo = "lettore";
    }

    public Utente(String nome, String cognome, String codiceFiscale, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
        this.ruolo = "lettore";
    }
    
    public Utente(String nome, String cognome, String codiceFiscale, String recapito) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.recapito = recapito;
        this.ruolo = "lettore";
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    
    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }
    
    public String getRecapito() { return recapito; }
    public void setRecapito(String recapito) { this.recapito = recapito; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    
    public Date getDataRegistrazione() { return data_registrazione; }
    public void setDataRegistrazione(Date data_registrazione) { this.data_registrazione = data_registrazione; }
}