package it.bookmarker.model;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String numeroTessera; 
    private String recapito;      // Solo per non registrati
    private String email;         // Solo per registrati
    private String password;      // Solo per registrati
    private String ruolo;

    public Utente() {
        this.ruolo = "lettore";
    }

    // Costruttore per registrazione 
    public Utente(String nome, String cognome, String numeroTessera, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.numeroTessera = numeroTessera;
        this.email = email;
        this.password = password;
        this.ruolo = "lettore";
    }
    


    // Costruttore per utente non registrato 
    public Utente(String nome, String cognome, String numeroTessera, String recapito) {
        this.nome = nome;
        this.cognome = cognome;
        this.numeroTessera = numeroTessera;
        this.recapito = recapito;
        this.ruolo = "lettore";
    }

    // getter e setter (lasciali come sono)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    
    public String getNumeroTessera() { return numeroTessera; }
    public void setNumeroTessera(String numeroTessera) { this.numeroTessera = numeroTessera; }
    
    public String getRecapito() { return recapito; }
    public void setRecapito(String recapito) { this.recapito = recapito; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}