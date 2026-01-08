package it.bookmarker.model;

import java.sql.Date;
import java.text.SimpleDateFormat; 

public class Libro {
    private int id; 
    private String titolo;
    private String autore;
    private String genere;
    
    
    private int disponibilita; 
    
    // se disponibilita == 0
    private Date dataRientro; 
    
    private Date dataPubblicazione; 
    private String descrizione; 
    private String copertina; 

    public Libro() {}

    
    public Libro(int id, String titolo, String autore, String genere, int disponibilita, Date dataRientro,
                 Date dataPubblicazione, String descrizione, String copertina) {
        this.id = id;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.disponibilita = disponibilita;
        this.dataRientro = dataRientro;
        this.dataPubblicazione = dataPubblicazione;
        this.descrizione = descrizione;
        this.copertina = copertina;
    }

    //GETTER E SETTER
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public int getDisponibilita() { return disponibilita; }
    public void setDisponibilita(int disponibilita) { this.disponibilita = disponibilita; }

    public Date getDataRientro() { return dataRientro; }
    public void setDataRientro(Date dataRientro) { this.dataRientro = dataRientro; }

    public Date getDataPubblicazione() { return dataPubblicazione; }
    public void setDataPubblicazione(Date dataPubblicazione) { this.dataPubblicazione = dataPubblicazione; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getCopertina() { return copertina; }
    public void setCopertina(String copertina) { this.copertina = copertina; }
    
    
    
    //METODO DI UTILITÀ PER LA JSP
    public String getMessaggioStato() {
        if (disponibilita > 0) {
            return "<span style='color:green'>Disponibile (" + disponibilita + " copie)</span>";
        } else {
            if (dataRientro != null) {
                //data in italiano (gg/mm/aaaa)
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return "<span style='color:red'>Non disponibile. Rientra il: " + sdf.format(dataRientro) + "</span>";
            } else {
                return "<span style='color:red'>Non disponibile</span>";
            }
        }
    }
}