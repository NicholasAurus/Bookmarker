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
    <title>Area Gestore - BookMarker</title>
    <link rel="stylesheet" href="css/style.css">
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
                <a href="logout.jsp" class="btn logout-btn">Logout</a>
            <% } %>
        </nav>
    </header>

    <main class="gestore-main">
        <div class="gestore-container">
            <% if (isLoggato && "GESTORE".equals(ruolo)) { %>
                
                <a href="GestoreServlet" class="gestore-square">
                    <span>CATALOGO GESTORE</span>
                </a>
                
                <a href="prestitiGlobali.jsp" class="gestore-square">
                    <span>PRESTITI GLOBALI</span>
                </a>

            <% } else { %>
                <div class="error-msg">
                    <h2 style="color: white;">Accesso Negato</h2>
                    <p style="color: white;">Non hai i permessi necessari per accedere a questa sezione.</p>
                    <a href="index.jsp" class="btn">Torna alla Home</a>
                </div>
            <% } %>
        </div>
    </main>

</body>
</html>