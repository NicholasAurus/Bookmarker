<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Recuperiamo l'utente dalla sessione
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    boolean isLoggato = (nomeUtente != null);

    // CONTROLLO SICUREZZA: Se l'utente non è loggato, non può stare qui -> Redirect al Login
    if (!isLoggato) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookMarker - Area Utente</title>
    
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        /* Sovrascriviamo lo sfondo solo per questa pagina per avere l'effetto "libri" */
        body {
            background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://images.unsplash.com/photo-1507842217121-9e96e474c113?q=80&w=1920&auto=format&fit=crop');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            min-height: 100vh;
        }

        /* Colore specifico per i bottoni dell'area utente (Blu come richiesto) */
        .icon-area-utente {
            background-color: #2980b9 !important; /* Blu */
            border: 2px solid #1f618d; /* Bordo leggermente più scuro */
        }
    </style>
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo - Home">
        </a>
        
        <nav class="nav-buttons">
            <span class="user-greeting">Ciao, <b><%= nomeUtente %></b></span>
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main>
        <div class="main-links">
            
            <a href="storico.jsp" class="main-link">
                <div class="icon-container icon-area-utente">
                    <i class="fa-solid fa-book-open" style="font-size: 4rem; color: white;"></i>
                </div>
                <p>STORICO <br> PRESTITI</p>
            </a>

            <a href="profilo.jsp" class="main-link">
                <div class="icon-container icon-area-utente">
                    <i class="fa-solid fa-address-card" style="font-size: 4rem; color: white;"></i>
                </div>
                <p>INFORMAZIONI <br> PERSONALI</p>
            </a>

        </div>
    </main>

</body>
</html>