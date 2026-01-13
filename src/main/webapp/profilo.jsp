<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.bookmarker.model.Utente" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // Recuperiamo l'oggetto utente passato dalla Servlet
    Utente u = (Utente) request.getAttribute("datiUtente");
    
    // Controllo di sicurezza: se non c'è l'utente, vai al login
    if (u == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Il mio Profilo - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css"> 
    <link rel="stylesheet" href="css/profilo.css">  
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        <nav class="nav-buttons">
            <a href="LibriServlet" class="btn">Catalogo</a>
            <a href="StoricoServlet" class="btn">Storico</a>
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main>
        <section class="blue-bar" style="display: flex; justify-content: center;">
            <div class="container-inner" style="max-width: 800px; width: 100%;">
                <h2 class="section-title">Il mio Profilo</h2>
            </div>
        </section>

        <div class="profile-container">
            <div class="profile-card">
                
                <div class="profile-header-bg">
                    <div class="profile-avatar">
                        <i class="fa-solid fa-user"></i>
                    </div>
                </div>

                <div class="profile-body">
                    <h1 class="profile-name"><%= u.getNome() %> <%= (u.getCognome() != null) ? u.getCognome() : "" %></h1>
                    <span class="profile-role"><%= (u.getRuolo() != null) ? u.getRuolo() : "Lettore" %></span>

                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Email</span>
                            <span class="info-value"><%= u.getEmail() %></span>
                        </div>

                        <div class="info-item">
                            <span class="info-label">Codice Fiscale</span>
                            <span class="info-value">
                                <%= (u.getCodiceFiscale() != null) ? u.getCodiceFiscale().toUpperCase() : "Non inserito" %>
                            </span>
                        </div>

                        <div class="info-item">
                            <span class="info-label">Data Registrazione</span>
                            <span class="info-value">
                                <% 
                                    if (u.getDataRegistrazione() != null) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        out.print(sdf.format(u.getDataRegistrazione()));
                                    } else {
                                        out.print("Data non disponibile");
                                    }
                                %>
                            </span>
                        </div>
                        
                        <div class="info-item">
                            <span class="info-label">Stato Account</span>
                            <span class="info-value" style="color:green;">
                                <i class="fa-solid fa-circle-check"></i> Attivo
                            </span>
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>
    </main>

</body>
</html>