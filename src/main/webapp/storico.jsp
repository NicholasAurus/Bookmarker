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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <style>
        /* CSS SPECIFICO PER LO STORICO (Replica lo screen) */
        
        .history-card {
            display: flex;
            background: white;
            padding: 20px;
            border-radius: 6px; /* Bordi leggermente stondati come nello screen */
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            align-items: center;
            gap: 20px;
            margin-bottom: 20px;
        }

        /* Immagine libro (quadrata grigia nello screen) */
        .history-asset {
            width: 100px;
            height: 100px;
            background: #eee;
            border-radius: 4px;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .history-content {
            flex-grow: 1;
        }

        .history-content h3 {
            margin: 0 0 5px 0;
            font-size: 1.2rem;
            color: #333;
        }

        .desc-short {
            color: #888;
            font-size: 0.9rem;
            margin-bottom: 15px;
            display: block;
        }

        /* Bottone Recensione Grigio */
        .btn-recensione {
            background-color: #e0e0e0;
            border: 1px solid #ccc;
            color: #333;
            padding: 6px 15px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 0.9rem;
            display: inline-block;
            cursor: pointer;
        }
        .btn-recensione:hover {
            background-color: #d0d0d0;
        }
        
        /* Quando la recensione c'è già */
        .recensione-presente {
            color: #27ae60;
            font-weight: bold;
            font-size: 0.9rem;
            padding: 6px 0;
            display: inline-block;
        }

        /* Colonna Destra: Date e Badge */
        .history-right {
            text-align: right;
            min-width: 220px;
            display: flex;
            flex-direction: column;
            gap: 8px;
            justify-content: center;
        }

        .date-label {
            font-size: 0.85rem;
            color: #333;
        }

        /* Badges */
        .status-badge {
            padding: 8px 0;
            width: 120px; /* Larghezza fissa come nello screen */
            text-align: center;
            color: white;
            font-weight: bold;
            border-radius: 4px;
            margin-left: auto; /* Allinea a destra */
            margin-top: 10px;
        }
        
        .bg-green { background-color: #6fdc8c; color: #000; } /* Verde chiaro "In corso" */
        .bg-blue { background-color: #1f6fb3; } /* Blu scuro "Concluso" */

        @media (max-width: 768px) {
            .history-card { flex-direction: column; text-align: center; }
            .history-right { text-align: center; margin-left: 0; align-items: center; }
            .status-badge { margin-left: 0; }
        }
    </style>
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