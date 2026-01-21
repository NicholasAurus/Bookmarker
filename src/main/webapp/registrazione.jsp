<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione</title>
    
    <link rel="stylesheet" href="css/style.css">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>

<header>
    <div class="header-spacer"></div>
    <a href="index.jsp" class="logo-container"> 
        <img src="img/logo.png" alt="BookMarker Logo - Home">
    </a>
    <nav class="nav-buttons">
        <a href="login.jsp" class="btn">Login</a>
    </nav>
</header>

    <main>
        <div class="registration-card">
            <h2>Registrati</h2>
            
            <%-- Blocco per mostrare messaggi di errore dalla Servlet --%>
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
            %>
                <div class="error-message"><%= errorMessage %></div>
            <% 
                } 
            %>
            
            <form action="RegistrazioneServlet" method="POST"> 
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" placeholder="Nome" required>
                </div>
                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" placeholder="Cognome" required>
                </div>
                <div class="form-group">
                    <label for="codice_fiscale">Codice Fiscale</label>
                    <input type="text" id="codice_fiscale" name="codice_fiscale" placeholder="Es. RSSMRA80A01H501U" maxlength="16" style="text-transform: uppercase;" required>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="example@mail.com" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="password" required>
                </div>
                <div class="form-group">
                    <label for="conferma_password">Conferma password</label>
                    <input type="password" id="conferma_password" name="conferma_password" placeholder="conferma password" required>
                </div>

                <div class="form-group">
                    <label for="domanda">Domanda di Sicurezza</label>
                    <select name="domanda" id="domanda" required>
                        <option value="" disabled selected>-- Seleziona una domanda --</option>
                        <option value="Qual è il cognome da nubile di tua madre?">Qual è il cognome da nubile di tua madre?</option>
                        <option value="Come si chiamava il tuo primo animale domestico?">Come si chiamava il tuo primo animale domestico?</option>
                        <option value="Qual è il nome della tua scuola elementare?">Qual è il nome della tua scuola elementare?</option>
                        <option value="Qual è il tuo libro preferito?">Qual è il tuo libro preferito?</option>
                        <option value="In che città si sono conosciuti i tuoi genitori?">In che città si sono conosciuti i tuoi genitori?</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="risposta">Risposta di Sicurezza</label>
                    <input type="text" id="risposta" name="risposta" placeholder="La tua risposta..." required>
                    <small>Serve per recuperare la password se la dimentichi.</small>
                </div>
                <button type="submit" class="submit-btn">Registrati</button>
            </form>
            
            <div class="form-links">
                <a href="login.jsp">Hai già un account? Accedi</a>
            </div>
        </div>
    </main>

</body>
</html>