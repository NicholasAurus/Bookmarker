package it.bookmarker.service;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;
import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;

public class UtenteService {

    private UtenteDAO utenteDAO;

    // Pattern per le Espressioni Regolari 
    // ^ = inizio stringa, $ = fine stringa, + = uno o più caratteri
    private static final Pattern NOME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$"); // Solo lettere e spazi
    private static final Pattern CF_PATTERN = Pattern.compile("^[A-Z0-9]{16}$"); // Alfanumerico esatto 16 caratteri
    // Password: (?=.*[0-9]) deve esserci un numero, (?=.*[A-Z]) una maiuscola, etc.
    private static final Pattern PASS_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$");

    public UtenteService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public String registraUtente(String nome, String cognome, String cf, String email, String password, String confirmPassword, String domanda, String risposta) {
        
        // Controllo Corrispondenza Password
        if (password == null || !password.equals(confirmPassword)) {
            return "Le password non corrispondono.";
        }

        //Controlli di Formato (usando i metodi privati)
        if (!isNomeValido(nome)) {
            return "Il nome deve contenere solo lettere.";
        }
        
        if (!isNomeValido(cognome)) { // Riusiamo lo stesso metodo del nome
            return "Il cognome deve contenere solo lettere.";
        }

        if (!isCodiceFiscaleValido(cf)) {
            return "Il Codice Fiscale deve essere di 16 caratteri alfanumerici.";
        }

        if (!isEmailFormatoValido(email)) {
            return "Inserisci un indirizzo email valido (con @ e .).";
        }

        if (!isPasswordForte(password)) {
            return "La password deve contenere almeno 8 caratteri, una maiuscola, un numero e un simbolo.";
        }
        
        if (domanda == null || domanda.trim().isEmpty()) {
            return "Seleziona una domanda di sicurezza.";
        }

        if (risposta == null || risposta.trim().isEmpty()) {
            return "La risposta alla domanda di sicurezza è obbligatoria.";
        }

        try {
            //Controlli sul Database (Unicità email)
            if (utenteDAO.esisteEmail(email)) {
                return "L'email inserita è già registrata.";
            }

            //Il CF esiste già nel DB
            if (utenteDAO.esisteCodiceFiscale(cf)) {
                return "Il Codice Fiscale inserito è già registrato.";
            }

            //Se arriviamo qui, è tutto OK
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Utente nuovoUtente = new Utente(nome, cognome, cf, email, hashedPassword);
            nuovoUtente.setDomandaSicurezza(domanda);
            nuovoUtente.setRispostaSicurezza(risposta);
            
            boolean salvato = utenteDAO.registraUtente(nuovoUtente);
            
            if (salvato) {
                return null; //OK
            } else { //Fail
                return "Errore generico durante il salvataggio.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore tecnico del database: " + e.getMessage();
        }
    }
    
    public Utente login(String email, String password) throws Exception {
        
        //Cerca l'utente nel DB
        Utente utente = utenteDAO.getUtenteByEmail(email);

        //Controllo Esistenza e Password (BCrypt)
        if (utente == null || !BCrypt.checkpw(password, utente.getPassword())) {
            throw new Exception("Email o password non validi.");
        }

        //Controllo Stati Account
        String stato = utente.getStato();
        if (stato != null) {
            if ("in_attesa".equals(stato)) {
                throw new Exception("Registrazione in attesa di approvazione da parte del bibliotecario.");
            }
        }

        //arrivati qui, è ok
        return utente;
    }
    
    public Utente getDatiUtente(String email) {
        if (email == null) return null;
        return utenteDAO.getUtenteByEmail(email);
    }
    
    public List<Utente> getUtentiDaApprovare() {
        return utenteDAO.getUtentiInAttesa();
    }

    public boolean accettaUtente(String email) {
        if (email == null) return false;
        
        
        Utente u = utenteDAO.getUtenteByEmail(email);
        
        // Se l'utente non esiste O non è in stato "in_attesa"
        if (u == null || !"in_attesa".equals(u.getStato())) {
            return false; 
        }
        
        return utenteDAO.updateStato(email, "attivo");
    }

    public boolean rifiutaUtente(String email) {
        if (email == null) return false;
        
        Utente u = utenteDAO.getUtenteByEmail(email);
        
     // Se l'utente non esiste O non è in stato "in_attesa"
        if (u == null || !"in_attesa".equals(u.getStato())) {
            return false;
        }
        
        return utenteDAO.deleteUtente(email);
    }

    public String recuperaDomanda(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return utenteDAO.getDomandaSicurezza(email);
    }

    public boolean verificaRispostaSicurezza(String email, String rispostaUtente) {
        if (email == null || rispostaUtente == null) return false;
        
        String rispostaCorretta = utenteDAO.getRispostaSicurezza(email);
        
        if (rispostaCorretta == null) return false;
        
        return rispostaCorretta.trim().equalsIgnoreCase(rispostaUtente.trim());
    }

    public boolean resetPassword(String email, String nuovaPassword, String confermaPassword) {
        if (email == null || nuovaPassword == null) return false;
        
        if (!nuovaPassword.equals(confermaPassword)) return false;
        
        if (!isPasswordForte(nuovaPassword)) return false;

        String hashedPassword = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt());
        
        return utenteDAO.updatePassword(email, hashedPassword);
    }
    
    //Metodi utili
    
    // Controlla "Solo lettere" (per Nome e Cognome)
    private boolean isNomeValido(String testo) {
        if (testo == null) return false;
        // matcher.matches() restituisce true se il testo rispetta il pattern definito sopra
        return NOME_PATTERN.matcher(testo).matches();
    }

    // Controlla "Alfanumerico 16 caratteri"
    private boolean isCodiceFiscaleValido(String cf) {
        if (cf == null) return false;
        // Convertiamo in maiuscolo per sicurezza prima del controllo
        return CF_PATTERN.matcher(cf.toUpperCase()).matches();
    }

    // Controlla formato Email semplice (@ e .)
    private boolean isEmailFormatoValido(String email) {
        if (email == null) return false;
        // Qui uso un controllo semplice come hai chiesto, senza Regex complesse
        return email.contains("@") && email.contains(".");
    }

    // Controlla la complessità della Password
    private boolean isPasswordForte(String password) {
        if (password == null) return false;
        // Controlla: 8 chars, 1 Maiusc, 1 Numero, 1 Simbolo
        return PASS_PATTERN.matcher(password).matches();
    }
}