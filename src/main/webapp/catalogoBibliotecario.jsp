<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="it.bookmarker.model.Libro" %>

<%
    // Recupero la lista dei libri
    List<Libro> elencoLibri = (List<Libro>) request.getAttribute("elencoLibri");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Catalogo - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
    <link rel="stylesheet" href="css/catalogoBibliotecario.css"> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        <nav class="nav-buttons">
            <span class="user-greeting">Area Bibliotecario</span>
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main>
        <section class="blue-bar-bibliotecario">
            <div class="container-bibliotecario">
                
                <div class="left-panel">
                    <h2 class="section-title">Catalogo bibliotecario</h2>
                    
                    <div class="search-wrapper" style="width: 300px;">
                        <input type="text" id="searchInput" placeholder="Ricerca..." class="search-input">
                        <i class="fa-solid fa-xmark close-icon" onclick="resetSearch()"></i>
                    </div>

                    <div class="filter-wrapper" onclick="toggleFilters(event)">
                        <i class="fa-solid fa-filter filter-icon"></i>
                        <span>Filtri</span>
                        
                        <div class="filter-dropdown" id="filterDropdown" onclick="event.stopPropagation()">
                            
                            <div class="filter-group">
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

                <div class="right-panel">
                    <a href="aggiungiLibro.jsp" class="btn-add-big">
                        Aggiungi libro<br>al catalogo
                    </a>
                </div>

            </div>
        </section>

        <div class="book-container" id="containerLibri">
            <% 
            if (elencoLibri != null && !elencoLibri.isEmpty()) {
                for (Libro libro : elencoLibri) {
                    boolean hasImg = (libro.getCopertina() != null && !libro.getCopertina().isEmpty());
                    boolean disponibile = libro.getDisponibilita() > 0;
            %>

            <div class="book-card-stroke search-item" 
                 data-genere="<%= libro.getGenere() %>" 
                 data-disponibile="<%= disponibile ? "si" : "no" %>">
                
                <div class="book-asset">
                    <% if (hasImg) { %>
                        <img src="<%= libro.getCopertina() %>" alt="Cover">
                    <% } else { %>
                        <i class="fa-regular fa-image"></i>
                    <% } %>
                </div>

                <div class="book-info-middle">
                    <h3 class="book-title"><%= libro.getTitolo() %></h3>
                    <p class="book-author" style="font-weight:bold; color:#555;"><%= libro.getAutore() %></p>
                    <p style="color: #777; margin-bottom: 20px;"><%= libro.getDescrizione() != null ? libro.getDescrizione() : "Descrizione..." %></p>

                    <div class="availability-row">
                        <div class="gray-label">Disponibilità:</div>
                        <% if (disponibile) { %>
                            <span class="avail-value">Disponibile</span>
                        <% } else { %>
                            <span class="avail-value" style="color:#c0392b;">Non disponibile</span>
                        <% } %>
                    </div>

                    <div class="availability-row" style="margin-top: 10px;">
                        <div class="gray-label">Numero di copie:</div>
                        <span class="avail-value"><%= libro.getDisponibilita() %></span>
                    </div>
                </div>

                <div class="book-actions-right">
                    <a href="ModificaLibroServlet?id=<%= libro.getId() %>" class="btn-action-white">
                        Modifica<br>disponibilità
                    </a>

                    <a href="RimuoviLibroServlet?id=<%= libro.getId() %>" class="btn-action-white" onclick="return confirm('Sei sicuro di voler rimuovere questo libro?');">
                        Rimuovi<br>dal catalogo
                    </a>
                </div>

            </div>

            <% 
                } 
            } else {
            %>
                <p style="text-align:center;">Nessun libro trovato.</p>
            <% } %>
        </div>
    </main>

    <script>
        // 1. GESTIONE MENU A TENDINA
        function toggleFilters(event) {
            const menu = document.getElementById('filterDropdown');
            menu.classList.toggle('active');
            event.stopPropagation();
        }

        // Chiudi menu cliccando fuori
        document.addEventListener('click', function(event) {
            const wrapper = document.querySelector('.filter-wrapper');
            const menu = document.getElementById('filterDropdown');
            if (!wrapper.contains(event.target)) {
                menu.classList.remove('active');
            }
        });

        // 2. POPOLAMENTO AUTOMATICO GENERI
        document.addEventListener("DOMContentLoaded", () => {
            const cards = document.querySelectorAll('.search-item');
            const selectGenere = document.getElementById('filterGenere');
            const generiTrovati = new Set(); 

            // Trova tutti i generi unici
            cards.forEach(card => {
                const genere = card.getAttribute('data-genere');
                if (genere) generiTrovati.add(genere);
            });

            // Aggiungi le opzioni alla select
            generiTrovati.forEach(genere => {
                const option = document.createElement('option');
                option.value = genere;
                option.textContent = genere;
                selectGenere.appendChild(option);
            });
        });

        // 3. LOGICA FILTRAGGIO COMPLETO
        const searchInput = document.getElementById('searchInput');
        const selectGenere = document.getElementById('filterGenere');
        const selectDisp = document.getElementById('filterDisp');

        function applicaFiltri() {
            const searchTerm = searchInput.value.toLowerCase();
            const selectedGenre = selectGenere.value;
            const selectedDisp = selectDisp.value; 

            const cards = document.querySelectorAll('.search-item');

            cards.forEach(card => {
                // Dati della card
                const title = card.querySelector('.book-title').innerText.toLowerCase();
                const author = card.querySelector('.book-author').innerText.toLowerCase();
                const cardGenre = card.getAttribute('data-genere');
                const cardDisp = card.getAttribute('data-disponibile');

                // Verifiche
                const matchSearch = title.includes(searchTerm) || author.includes(searchTerm);
                const matchGenre = (selectedGenre === 'all') || (cardGenre === selectedGenre);
                const matchDisp = (selectedDisp === 'all') || (cardDisp === selectedDisp);

                // Mostra o nascondi
                if (matchSearch && matchGenre && matchDisp) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        }

        // Event listener per la barra di ricerca
        searchInput.addEventListener('keyup', applicaFiltri);

        // Reset
        function resetSearch() {
            searchInput.value = '';
            applicaFiltri();
        }

        function resetFiltri() {
            selectGenere.value = 'all';
            selectDisp.value = 'all';
            searchInput.value = '';
            applicaFiltri();
        }
    </script>
</body>
</html>