<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    String ruolo = (String) session.getAttribute("ruoloUtente");
    boolean isLoggato = (nomeUtente != null);
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Area Moderatore - BookMarker</title>
    <link rel="stylesheet" href="css/moderatore.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        <nav class="nav-buttons">
            <% if (isLoggato) { %>
                <span class="user-greeting">Ciao, <b><%= nomeUtente %></b></span>
                <a href="logout.jsp" class="btn">Logout</a>
            <% } %>
        </nav>
    </header>

    <main class="moderatore-main">
        <div class="moderatore-container">
            <% if (isLoggato && "MODERATORE".equals(ruolo)) { %>
                
                <a href="listaRecensioni.jsp" class="moderatore-link">
                    <div class="moderatore-square">
                        <i class="fa-solid fa-headset"></i>
                    </div>
                    <span>AREA MODERATORE</span>
                </a>
                
                <a href="areaUtente.jsp" class="moderatore-link">
                    <div class="moderatore-square">
                        <i class="fa-solid fa-user"></i>
                    </div>
                    <span>AREA UTENTE</span>
                </a>

            <% } else { %>
                <div class="error-msg">
                    <h2>Accesso Negato</h2>
                    <p>Non hai i permessi per accedere a questa sezione riservata ai moderatori.</p>
                    <a href="index.jsp" class="btn">Torna alla Home</a>
                </div>
            <% } %>
        </div>
    </main>

</body>
</html>