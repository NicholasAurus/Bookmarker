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
    <link rel="stylesheet" href="css/storico.css"> <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
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
                    <input type="text" id="searchInput" placeholder="Ricerca per titolo..." class="search-input" style="width: 100%;">
                    <i class="fa-solid fa-xmark close-icon" id="resetBtn"></i>
                </div>

                <div class="filter-wrapper" onclick="toggleFilters(event)">
                    <i class="fa-solid fa-filter filter-icon"></i>
                    
                    <div class="filter-dropdown" id="filterDropdown" onclick="event.stopPropagation()">
                        <div class="filter-group">
                            <label style="font-weight:bold; color:#333;">Stato:</label>
                            <select id="filterStato" class="filter-select" onchange="applicaFiltriGlobale()">
                                <option value="all">Tutti</option>
                                <option value="in-corso">In corso</option>
                                <option value="concluso">Concluso</option>
                            </select>
                        </div>
                        
                        <div style="text-align: right; margin-top: 15px;">
                            <small style="color: white; background-color: #c0392b; padding: 6px 12px; border-radius: 4px; cursor: pointer; display: inline-block;" onclick="resetFiltri()">
                                Resetta filtri
                            </small>
                        </div>
                    </div>
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
                    // Logica stato
                    boolean inCorso = (p.getDataRestituzioneEffettiva() == null);
                    String dataInizioStr = (p.getDataInizio() != null) ? sdf.format(p.getDataInizio()) : "--/--/----";
                    String dataRestituzioneStr = (!inCorso) ? sdf.format(p.getDataRestituzioneEffettiva()) : "In uso";
            %>
            
            <div class="history-card search-item" data-stato="<%= inCorso ? "in-corso" : "concluso" %>">
                <div class="history-asset">
                    <% if(p.getCopertinaLibro() != null) { %>
                        <img src="<%= p.getCopertinaLibro() %>" alt="cover" style="max-width:100%; max-height:100%;">
                    <% } else { %>
                        <i class="fa-regular fa-image" style="font-size: 2rem; color: #ccc;"></i>
                    <% } %>
                </div>

                <div class="history-content">
                    <h3 class="card-title"><%= p.getTitoloLibro() %></h3>
                    
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
            
            <div id="noResults" style="display:none; text-align:center; padding:20px; width:100%; color:#555;">
                Nessun prestito corrisponde alla tua ricerca.
            </div>
        </div>
    </main>

    <script>
        // 1. Funzione per aprire/chiudere il menu filtri
        function toggleFilters(event) {
            const dropdown = document.getElementById('filterDropdown');
            dropdown.classList.toggle('active');
            event.stopPropagation(); // Evita conflitti
        }

        // Chiude il menu se clicchi fuori
        document.addEventListener('click', function(event) {
            const dropdown = document.getElementById('filterDropdown');
            const filterWrapper = document.querySelector('.filter-wrapper');
            if (filterWrapper && !filterWrapper.contains(event.target)) {
                dropdown.classList.remove('active');
            }
        });

        // 2. LOGICA DI FILTRAGGIO (Ricerca + Stato)
        function applicaFiltriGlobale() {
            // Prendo i valori attuali
            const inputTesto = document.getElementById('searchInput').value.toLowerCase();
            const statoSelezionato = document.getElementById('filterStato').value; // 'all', 'in-corso', 'concluso'
            
            // Elementi del DOM
            const cards = document.querySelectorAll('.search-item');
            const resetBtn = document.getElementById('resetBtn');
            const noResultsMsg = document.getElementById('noResults');
            
            let visibleCount = 0;

            // Mostra la X se c'è testo
            resetBtn.style.display = (inputTesto.length > 0) ? 'block' : 'none';

            cards.forEach(card => {
                // Recupero i dati dalla card
                const titolo = card.querySelector('.card-title').innerText.toLowerCase();
                const statoCard = card.getAttribute('data-stato');

                // Verifico le condizioni
                const matchTesto = titolo.includes(inputTesto);
                const matchStato = (statoSelezionato === 'all') || (statoSelezionato === statoCard);

                // Se ENTRAMBE sono vere, mostro la card
                if (matchTesto && matchStato) {
                    card.style.display = "flex"; 
                    visibleCount++;
                } else {
                    card.style.display = "none";
                }
            });

            // Gestione messaggio "Nessun risultato"
            if (noResultsMsg) {
                noResultsMsg.style.display = (visibleCount === 0 && cards.length > 0) ? 'block' : 'none';
            }
        }

        // 3. Reset dei filtri
        function resetFiltri() {
            document.getElementById('searchInput').value = '';
            document.getElementById('filterStato').value = 'all';
            applicaFiltriGlobale();
            document.getElementById('filterDropdown').classList.remove('active');
        }

        // 4. Event Listeners
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', applicaFiltriGlobale);
        }

        const resetBtn = document.getElementById('resetBtn');
        if (resetBtn) {
            resetBtn.addEventListener('click', function() {
                 document.getElementById('searchInput').value = '';
                 applicaFiltriGlobale();
            });
        }
    </script>
</body>
</html>