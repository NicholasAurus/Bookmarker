package it.bookmarker.model;

public class Libro {
    private String titolo;
    private String autore;
    private String genere;

    public Libro(String titolo, String autore, String genere) {
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
    }

    
    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public String getGenere() { return genere; }
}
