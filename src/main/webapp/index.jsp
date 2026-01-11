<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    
    
    //recuperiamo l'utente e il ruolo dalla sessione
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    String ruolo = (String) session.getAttribute("ruoloUtente");
    
    
    boolean isLoggato = (nomeUtente != null);
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookMarker</title>
    
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
            <% if (isLoggato) { %>
                <span class="user-greeting">Ciao, <b><%= nomeUtente %></b></span>
                
                <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
                
            <% } else { %>
                <a href="registrazione.jsp" class="btn">Registrati</a>
                <a href="login.jsp" class="btn">Login</a>
            <% } %>
        </nav>
    </header>

    <main>
        <div class="main-links">
            
            <% 
            
            if (isLoggato && "GESTORE".equals(ruolo)) { 
            %>
                
                <a href="GestoreServlet" class="main-link">
                    <div class="icon-container" style="background-color: #d35400;"> <i class="fa-solid fa-book-open-reader" style="font-size: 4rem; color: white;"></i>
                    </div>
                    <p>CATALOGO GESTORE</p>
                </a>
                
                <a href="prestitiGlobali.jsp" class="main-link">
                    <div class="icon-container" style="background-color: #e67e22;"> <i class="fa-solid fa-list-check" style="font-size: 4rem; color: white;"></i>
                    </div>
                    <p>PRESTITI GLOBALI</p>
                </a>

            <% 
            
            } else if (isLoggato && "MODERATORE".equals(ruolo)) { 
            %>

                <a href="listaRecensioni.jsp" class="main-link">
                    <div class="icon-container" style="background-color: #8e44ad;"> <i class="fa-solid fa-comments" style="font-size: 4rem; color: white;"></i>
                    </div>
                    <p>LISTA RECENSIONI</p>
                </a>

                <a href="segnalazioni.jsp" class="main-link">
                    <div class="icon-container" style="background-color: #9b59b6;"> <i class="fa-solid fa-circle-exclamation" style="font-size: 4rem; color: white;"></i>
                    </div>
                    <p>SEGNALAZIONI</p>
                </a>

            <% 
            
            } else { 
            %>

                <a href="LibriServlet" class="main-link" id="catalogo">
                    <div class="icon-container">
                        <img src="img/catalogo.png" alt="Icona Catalogo Libri">
                    </div>
                    <p>CATALOGO</p>
                </a>
                
                <a href="<%= isLoggato ? "areaUtente.jsp" : "login.jsp" %>" class="main-link" id="area-utente">
                    <div class="icon-container">
                        <img src="img/areautente.png" alt="Icona Area Utente">
                    </div>
                    <p>AREA UTENTE</p>
                </a>

            <% } %>

        </div>
    </main>

</body>
</html>