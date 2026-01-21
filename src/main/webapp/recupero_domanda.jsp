<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Domanda di Sicurezza</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"><img src="img/logo.png" alt="Logo"></a>
        <nav class="nav-buttons"><a href="login.jsp" class="btn">Login</a></nav>
    </header>

    <main>
        <div class="registration-card">
            <h2>Verifica Identità</h2>
            
            <% String error = (String) request.getAttribute("error"); 
               if(error != null) { %>
                <div class="error-message"><%= error %></div>
            <% } %>

            <div style="background:#f9f9f9; padding:15px; border-radius:8px; border:1px solid #eee; margin-bottom:20px; text-align:center;">
                <span style="font-weight:bold; color:#267bbc;">Domanda:</span><br>
                <span style="font-size:1.1em;"><%= request.getAttribute("domanda") %></span>
            </div>

            <form action="RecuperoPasswordServlet" method="post">
                <input type="hidden" name="action" value="verificaRisposta">
                
                <div class="form-group">
                    <label>La tua Risposta</label>
                    <input type="text" name="risposta" required placeholder="Scrivi la risposta..." autocomplete="off">
                </div>

                <button type="submit" class="submit-btn">Verifica</button>
            </form>
            <div class="form-links"><a href="RecuperoPasswordServlet">Torna indietro</a></div>
        </div>
    </main>
</body>
</html>