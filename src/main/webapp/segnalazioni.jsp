<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Segnalazione" %>

<%
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    boolean isLoggato = (nomeUtente != null);
   
    List<Segnalazione> elencoSegnalazioni = (List<Segnalazione>) request.getAttribute("elencoSegnalazioni");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Segnalazioni - BookMarker</title>
    <link rel="stylesheet" href="css/segnalazioni.css">
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

    <main>
        <section class="blue-bar">
            <div class="container-inner">
                <div class="title-group">
                    <h2 class="section-title">Gestione Segnalazioni</h2>
                    <span class="count-badge"><%= (elencoSegnalazioni != null) ? elencoSegnalazioni.size() : 0 %> totali</span>
                </div>
                <div class="header-actions">
                    <div class="search-box">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" placeholder="Filtra segnalazioni...">
                    
                   
               
            </div>
        </section>

        <div class="content-wrapper">
            <% if (elencoSegnalazioni == null || elencoSegnalazioni.isEmpty()) { %>
                <div class="empty-msg">
                    <i class="fa-solid fa-circle-check"></i>
                    <p>Ottimo lavoro! Non ci sono segnalazioni da gestire.</p>
                </div>
            <% } else { 
                for (Segnalazione seg : elencoSegnalazioni) { %>
                <div class="card-segnalazione">
                    <div class="card-left">
                        <div class="user-info">
                            <div class="user-icon"><i class="fa-solid fa-user"></i></div>
                            <div class="user-text">
                                <strong>Utente ID: <%= seg.getUtenteId() %></strong>
                                <span>Segnala Recensione #<%= seg.getRecensioneId() %></span>
                            </div>
                        </div>
                        <div class="reason-box">
                            <label>Motivo della segnalazione:</label>
                            <p><%= seg.getMotivo() %></p>
                            <small>Inviata il: <%= seg.getDataSegnalazione() %></small>
                        </div>
                    </div>
                    <div class="card-right">
                        <div class="status-tag status-<%= seg.getStato().toLowerCase() %>">
                            <%= seg.getStato() %>
                        </div>
                        
                        <div class="action-buttons">
                            <form action="RisolviSegnalazioneServlet" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="<%= seg.getId() %>">
                                <button type="submit" class="btn-resolve" title="Risolvi">
                                    <i class="fa-solid fa-check"></i> Risolvi
                                </button>
                            </form>
                            
                            <form action="IgnoraSegnalazioneServlet" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="<%= seg.getId() %>">
                                <button type="submit" class="btn-ignore" title="Ignora">
                                    <i class="fa-solid fa-xmark"></i> Ignora
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            <% } } %>
        </div>
    </main>

</body>
</html>