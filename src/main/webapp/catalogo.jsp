<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="it.bookmarker.model.Libro" %>

<%
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    boolean isLoggato = (nomeUtente != null);

    List<Libro> elencoLibri = (List<Libro>) request.getAttribute("elencoLibri");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catalogo - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
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
                <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
            <% } else { %>
                <a href="registrazione.jsp" class="btn">Registrati</a>
                <a href="login.jsp" class="btn">Login</a>
            <% } %>
        </nav>
    </header>

    <main>
        <section class="blue-bar">
            <div class="container-inner">
                <h2 class="section-title">Catalogo</h2>
                
                <div class="search-wrapper">
                    <input type="text" id="searchInput" placeholder="Cerca per titolo o autore..." class="search-input">
                    <i class="fa-solid fa-xmark close-icon" onclick="resetSearch()"></i>
                </div>

                <div class="filter-wrapper" onclick="toggleFilters(event)">
                    <i class="fa-solid fa-filter filter-icon"></i>
                    <span>Filtri</span>
                    
                    <div class="filter-dropdown" id="filterDropdown" onclick="event.stopPropagation()"> <div class="filter-group">
                            <label for="filterGenere">Genere:</label>
                            <select id="filterGenere" class="filter-select" onchange="applicaFiltri()">
                                <option value="all">Tutti</option>
                                </select>
                        </div>

                        <div class="filter-group">
                            <label for="filterDisp">Disponibilità:</label>
                            <select id="filterDisp" class="filter-select" onchange="applicaFiltri()">
                                <option value="all">Tutti</option>
                                <option value="si">Solo Disponibili</option>
                                <option value="no">Non Disponibili</option>
                            </select>
                        </div>
                        
                        <div style="text-align: right; margin-top: 15px;">
    <small style="color: white; background-color: #c0392b; padding: 6px 12px; border-radius: 4px; cursor: pointer; display: inline-block;" onclick="resetFiltri()">
        Resetta filtri
    </small>
</div>
                    </div>
                </div>
            </div>
        </section>

        <div class="book-container" id="containerLibri">
            
            <% 
            if (elencoLibri == null) { 
            %>
                <div style="text-align:center; padding: 50px; background: white; border-radius: 8px;">
                    <h3>Attenzione</h3>
                    <p>Devi passare dalla Servlet per vedere i libri.</p>
                    <a href="LibriServlet" class="btn-neutral">Vai al Catalogo Corretto</a>
                </div>
            <% 
            } else if (elencoLibri.isEmpty()) { 
            %>
                <div style="text-align:center; padding: 20px; background: white;">
                    <p>Nessun libro presente nel catalogo.</p>
                </div>
            <% 
            } else {
                for (Libro libro : elencoLibri) {
                    
                    boolean disponibile = libro.getDisponibilita() > 0;
                    String classeStato = disponibile ? "status-value" : "status-value status-red";
                    String testoStato = "";
                    
                    if (disponibile) {
                        testoStato = "Disponibile (" + libro.getDisponibilita() + ")";
                    } else {
                        if (libro.getDataRientro() != null) {
                            testoStato = "Rientra il " + sdf.format(libro.getDataRientro());
                        } else {
                            testoStato = "Non disponibile";
                        }
                    }
                    
                    String imgPath = libro.getCopertina();
                    boolean hasImg = (imgPath != null && !imgPath.isEmpty());
            %>

            <div class="book-card-stroke search-item" 
                 data-genere="<%= libro.getGenere() %>" 
                 data-disponibile="<%= disponibile ? "si" : "no" %>">
                 
                <div class="book-asset">
                    <% if (hasImg) { %>
                        <img src="<%= imgPath %>" alt="Copertina" style="max-width:100%; max-height:100%;">
                    <% } else { %>
                        <i class="fa-regular fa-image"></i>
                    <% } %>
                </div>
                
                <div class="book-content">
                    
                    <a href="DettaglioLibroServlet?id=<%= libro.getId() %>" style="text-decoration: none; color: inherit;">
                        <h3 class="book-title"><%= libro.getTitolo() %></h3>
                    </a>
                    
                    <p class="book-author" style="font-weight: bold; color: #555; margin-bottom: 5px;">
                        <%= libro.getAutore() %>
                    </p>
                    
                    <p style="font-size: 0.9rem; color: #888; margin: 0;">Genere: <span class="book-genre"><%= libro.getGenere() %></span></p>

                    <p><%= libro.getDescrizione() != null ? libro.getDescrizione() : "Nessuna descrizione." %></p>
                    
                    <div class="button-group">
                        <span class="btn-neutral">Stato:</span>
                        <span class="<%= classeStato %>"><%= testoStato %></span>
                    </div>
                </div>
            </div>

            <% 
                } 
            } 
            %>
            
        </div>
    </main>

    <script>
        // --- 1. GESTIONE MENU A TENDINA ---
        function toggleFilters(event) {
            const menu = document.getElementById('filterDropdown');
            menu.classList.toggle('active');
        }

        // Chiude il menu se clicchi fuori
        document.addEventListener('click', function(event) {
            const wrapper = document.querySelector('.filter-wrapper');
            const menu = document.getElementById('filterDropdown');
            if (!wrapper.contains(event.target)) {
                menu.classList.remove('active');
            }
        });

        // --- 2. POPOLAMENTO AUTOMATICO GENERI ---
        document.addEventListener("DOMContentLoaded", () => {
            const cards = document.querySelectorAll('.search-item');
            const selectGenere = document.getElementById('filterGenere');
            const generiTrovati = new Set(); // Set evita i duplicati

            // Scansiona tutti i libri e trova i generi unici
            cards.forEach(card => {
                const genere = card.getAttribute('data-genere');
                if (genere) {
                    generiTrovati.add(genere);
                }
            });

            // Aggiungi le opzioni alla select
            generiTrovati.forEach(genere => {
                const option = document.createElement('option');
                option.value = genere;
                option.textContent = genere;
                selectGenere.appendChild(option);
            });
        });

        // --- 3. LOGICA DI FILTRAGGIO E RICERCA ---
        const searchInput = document.getElementById('searchInput');
        const selectGenere = document.getElementById('filterGenere');
        const selectDisp = document.getElementById('filterDisp');

        function applicaFiltri() {
            const searchTerm = searchInput.value.toLowerCase();
            const selectedGenre = selectGenere.value; // Es: 'Fantasy', 'Horror', 'all'
            const selectedDisp = selectDisp.value;    // Es: 'si', 'no', 'all'

            const cards = document.querySelectorAll('.search-item');

            cards.forEach(card => {
                // Recuperiamo i dati dalla card
                const title = card.querySelector('.book-title').innerText.toLowerCase();
                const author = card.querySelector('.book-author').innerText.toLowerCase();
                const cardGenre = card.getAttribute('data-genere');
                const cardDisp = card.getAttribute('data-disponibile');

                // 1. Controllo Ricerca
                const matchSearch = title.includes(searchTerm) || author.includes(searchTerm);

                // 2. Controllo Genere
                const matchGenre = (selectedGenre === 'all') || (cardGenre === selectedGenre);

                // 3. Controllo Disponibilità
                const matchDisp = (selectedDisp === 'all') || (cardDisp === selectedDisp);

                // SE rispetta TUTTI i criteri -> Mostra, ALTRIMENTI -> Nascondi
                if (matchSearch && matchGenre && matchDisp) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        }
        
        // Collega la funzione agli eventi
        searchInput.addEventListener('keyup', applicaFiltri);

        function resetSearch() {
            searchInput.value = '';
            applicaFiltri();
        }

        function resetFiltri() {
            selectGenere.value = 'all';
            selectDisp.value = 'all';
            applicaFiltri();
        }
    </script>

</body>
</html>