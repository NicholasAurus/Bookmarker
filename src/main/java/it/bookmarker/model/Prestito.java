package it.bookmarker.model;
import java.sql.Date; // Usiamo sql.Date per facilità con JDBC

public class Prestito {

    private int id;
    private int utenteId;
    private int libroId;
    private Date dataInizio;
    private Date dataFinePrevista;
    private Date dataRestituzioneEffettiva; // Può essere null se non ancora restituito




    public Prestito() {}

    // Getter e Setter standard
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
    
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    
    public Date getDataInizio() { return dataInizio; }
    public void setDataInizio(Date dataInizio) { this.dataInizio = dataInizio; }
    
    public Date getDataFinePrevista() { return dataFinePrevista; }
    public void setDataFinePrevista(Date dataFinePrevista) { this.dataFinePrevista = dataFinePrevista; }
    
    public Date getDataRestituzioneEffettiva() { return dataRestituzioneEffettiva; }
    public void setDataRestituzioneEffettiva(Date dataRestituzioneEffettiva) { this.dataRestituzioneEffettiva = dataRestituzioneEffettiva; }


}
