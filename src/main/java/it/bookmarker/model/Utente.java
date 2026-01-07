package it.bookmarker.model;

public class Utente {
    private String nome;
    private String email;
    private String ruolo;
    private String passwordHash; // Ci serve l'hash per controllarlo nella servlet

    public Utente(String nome, String email, String ruolo, String passwordHash) {
        this.nome = nome;
        this.email = email;
        this.ruolo = ruolo;
        this.passwordHash = passwordHash;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRuolo() { return ruolo; }
    public String getPasswordHash() { return passwordHash; }
}