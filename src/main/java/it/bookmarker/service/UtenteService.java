package it.bookmarker.service;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;
import it.bookmarker.dao.UtenteDAO;
import it.bookmarker.model.Utente;
import it.bookmarker.service.exception.GenericException.*;
import it.bookmarker.service.exception.UtenteServiceException.*;

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

    public void registraUtente(String nome, String cognome, String cf, String email, String password, String confirmPassword, String domanda, String risposta) 
    		throws FormatoDatiNonValidoException,FormatoPasswordNonValidoException, PasswordNonCorrispondentiException, EmailGiaRegistrataException, CodiceFiscaleGiaRegistratoException, SQLException {

		if (password == null || !password.equals(confirmPassword)) {
		throw new PasswordNonCorrispondentiException("Le password non corrispondono.");
		}
		
		if (!isNomeValido(nome)) {
		throw new FormatoDatiNonValidoException("Il nome deve contenere solo lettere.");
		}
		
		if (!isNomeValido(cognome)) {
		throw new FormatoDatiNonValidoException("Il cognome deve contenere solo lettere.");
		}
		
		if (!isCodiceFiscaleValido(cf)) {
		throw new FormatoDatiNonValidoException("Il Codice Fiscale deve essere di 16 caratteri alfanumerici.");
		}
		
		if (!isEmailFormatoValido(email)) {
		throw new FormatoDatiNonValidoException("Inserisci un indirizzo email valido (con @ e .).");
		}
		
		if (!isPasswordValida(password)) {
		throw new FormatoPasswordNonValidoException("La password deve contenere almeno 8 caratteri, una maiuscola, un numero e un simbolo.");
		}
		
		if (domanda == null || domanda.trim().isEmpty()) {
		throw new FormatoDatiNonValidoException("Seleziona una domanda di sicurezza.");
		}
		
		if (risposta == null || risposta.trim().isEmpty()) {
		throw new FormatoDatiNonValidoException("La risposta alla domanda di sicurezza è obbligatoria.");
		}
		
		if (utenteDAO.esisteEmail(email)) {
		throw new EmailGiaRegistrataException("L'email inserita è già registrata.");
		}
		
		if (utenteDAO.esisteCodiceFiscale(cf)) {
		throw new CodiceFiscaleGiaRegistratoException("Il Codice Fiscale inserito è già registrato.");
		}
		
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		Utente nuovoUtente = new Utente(nome, cognome, cf, email, hashedPassword);
		nuovoUtente.setDomandaSicurezza(domanda);
		nuovoUtente.setRispostaSicurezza(risposta);
		
		utenteDAO.registraUtente(nuovoUtente);
		
		}



    
    public Utente login(String email, String password) throws CredenzialiNonValideException, UtenteNonAbilitatoException {
        
        Utente utente = utenteDAO.getUtenteByEmail(email);

        if (utente == null || !BCrypt.checkpw(password, utente.getPassword())) {
            throw new CredenzialiNonValideException("Email o password non validi.");
        }

        String stato = utente.getStato();
        if (stato != null) {
            if ("in_attesa".equals(stato)) {
                throw new UtenteNonAbilitatoException("Registrazione in attesa di approvazione da parte del bibliotecario.");
            }
        }

        return utente;
    }
    
    public Utente getDatiUtente(String email) {
        if (email == null) return null;
        return utenteDAO.getUtenteByEmail(email);
    }
    
    public List<Utente> getUtentiDaApprovare() {
        return utenteDAO.getUtentiInAttesa();
    }

    public void accettaUtente(String email) 
            throws FormatoDatiNonValidoException, UtenteNonTrovatoException, StatoUtenteNonValidoException {
        
        if (email == null) {
            throw new FormatoDatiNonValidoException("Email null.");
        }
        
        Utente u = utenteDAO.getUtenteByEmail(email);
        
        if (u == null) {
            throw new UtenteNonTrovatoException("Email non valida.");
        }

        if (!"in_attesa".equals(u.getStato())) {
            throw new StatoUtenteNonValidoException("L'utente non è In Attesa.");
        }
        
        utenteDAO.updateStato(email, "attivo");
    }

    public void rifiutaUtente(String email) 
            throws FormatoDatiNonValidoException, UtenteNonTrovatoException, StatoUtenteNonValidoException {
        
        if (email == null) {
            throw new FormatoDatiNonValidoException("Email null.");
        }
        
        Utente u = utenteDAO.getUtenteByEmail(email);
        
        if (u == null) {
            throw new UtenteNonTrovatoException("Email non valida.");
        }

        if (!"in_attesa".equals(u.getStato())) {
            throw new StatoUtenteNonValidoException("L'utente non è In Attesa.");
        }
        
        utenteDAO.deleteUtente(email);
    }

    public String recuperaDomanda(String email) throws UtenteNonTrovatoException {
        Utente utente = utenteDAO.getUtenteByEmail(email);
        if (utente == null) {
            throw new UtenteNonTrovatoException("Email non trovata.");
        }
        return utente.getDomandaSicurezza();
    }

    public void verificaRispostaSicurezza(String email, String risposta) 
            throws UtenteNonTrovatoException, RispostaSicurezzaErrataException {
        
        Utente utente = utenteDAO.getUtenteByEmail(email);
        if (utente == null) {
            throw new UtenteNonTrovatoException("Email non trovata.");
        }
        
        if (!utente.getRispostaSicurezza().equalsIgnoreCase(risposta)) {
            throw new RispostaSicurezzaErrataException("Risposta di sicurezza errata.");
        }
    }

    public void resetPassword(String email, String rispostaSicurezza, String nuovaPassword, String confermaPassword) 
            throws UtenteNonTrovatoException, RispostaSicurezzaErrataException, 
                   FormatoPasswordNonValidoException, PasswordNonCorrispondentiException {

        Utente utente = utenteDAO.getUtenteByEmail(email);

        if (utente == null) {
            throw new UtenteNonTrovatoException("Email non trovata.");
        }

        if (!utente.getRispostaSicurezza().equalsIgnoreCase(rispostaSicurezza)) {
            throw new RispostaSicurezzaErrataException("Risposta di sicurezza errata.");
        }

        if (!isPasswordValida(nuovaPassword)) {
            throw new FormatoPasswordNonValidoException("La password non rispetta il formato richiesto.");
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            throw new PasswordNonCorrispondentiException("I campi password e conferma password non corrispondono.");
        }

        String hashedPassword = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt());
        utenteDAO.updatePassword(email, hashedPassword);
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
    private boolean isPasswordValida(String password) {
        if (password == null) return false;
        // Controlla: 8 chars, 1 Maiusc, 1 Numero, 1 Simbolo
        return PASS_PATTERN.matcher(password).matches();
    }
}