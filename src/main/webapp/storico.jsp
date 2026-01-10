<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="it.bookmarker.model.Prestito" %>

<%
    List<Prestito> storico = (List<Prestito>) request.getAttribute("elencoStorico");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Storico - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
    <link rel="stylesheet" href="css/storico.css">
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
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main>
        <section class="blue-bar" style="display: flex; justify-content: center;">
            <div class="container-inner" style="max-width: 1200px; display: flex; align-items: center;">
                <h2 class="section-title" style="margin-right: 30px;">Storico</h2>
                
                <div class="search-wrapper" style="flex: 1; margin-right: 20px;">
                    <input type="text" placeholder="Ricerca..." class="search-input" style="width: 100%;">
                    <i class="fa-solid fa-xmark close-icon"></i>
                </div>

                <div class="filter-wrapper">
                    <i class="fa-solid fa-filter filter-icon"></i>
                    </div>
                <div style="margin-left: 20px; font-weight: bold; color: #333;">Filtri Attivi</div>
            </div>
        </section>

        <div class="book-container">
            <% 
            if (storico == null || storico.isEmpty()) { 
            %>
                <div style="text-align:center; padding: 40px; background: white; border-radius: 8px;">
                    <p>Non hai ancora effettuato prestiti.</p>
                </div>
            <% 
            } else {
                for (Prestito p : storico) {
                    // Logica stato: Se data restituzione effettiva è NULL -> In corso
                    boolean inCorso = (p.getDataRestituzioneEffettiva() == null);
                    
                    String dataInizioStr = (p.getDataInizio() != null) ? sdf.format(p.getDataInizio()) : "--/--/----";
                    String dataRestituzioneStr = (!inCorso) ? sdf.format(p.getDataRestituzioneEffettiva()) : "In uso";
            %>
            
            <div class="history-card">
                <div class="history-asset">
                    <% if(p.getCopertinaLibro() != null) { %>
                        <img src="<%= p.getCopertinaLibro() %>" alt="cover" style="max-width:100%; max-height:100%;">
                    <% } else { %>
                        <i class="fa-regular fa-image" style="font-size: 2rem; color: #ccc;"></i>
                    <% } %>
                </div>

                <div class="history-content">
                    <h3><%= p.getTitoloLibro() %></h3>
                   

                    <% if (!p.isRecensito()) { %>
                        <a href="scriviRecensione.jsp?idLibro=<%= p.getLibroId() %>" class="btn-recensione">
                            Recensione:
                        </a>
                    <% } else { %>
                        <span class="recensione-presente"><i class="fa-solid fa-check"></i> Recensito</span>
                    <% } %>
                </div>

                <div class="history-right">
                    <div class="date-label">Data Prestito : <%= dataInizioStr %></div>
                    <div class="date-label">Data Restituzione: <%= dataRestituzioneStr %></div>
                    
                    <% if (inCorso) { %>
                        <div class="status-badge bg-green">In corso</div>
                    <% } else { %>
                        <div class="status-badge bg-blue">Concluso</div>
                    <% } %>
                </div>
            </div>

            <% 
                } 
            } 
            %>
        </div>
    </main>
</body>
</html>