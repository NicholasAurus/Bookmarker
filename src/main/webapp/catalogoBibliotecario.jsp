<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="it.bookmarker.model.Libro" %>

<%
    List<Libro> elencoLibri = (List<Libro>) request.getAttribute("elencoLibri");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Catalogo - BookMarker</title>
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
        <section class="blue-bar">
            <div class="container-inner">
                <div class="title-wrapper">
                    <h2 class="section-title">Catalogo</h2>
                    <a href="aggiungiLibro.jsp" class="btn-add-small" title="Aggiungi Nuovo Libro">
                        <i class="fa-solid fa-plus"></i> Aggiungi
                    </a>
                </div>
                
                <div class="search-wrapper">
                    <div class="search-box-inner">
                        <input type="text" id="searchInput" placeholder="Cerca per titolo o autore..." class="search-input">
                        <i class="fa-solid fa-xmark close-icon" onclick="resetSearch()"></i>
                    </div>
                </div>

                <div class="filter-wrapper" onclick="toggleFilters(event)">
                    <div class="filter-trigger">
                        <i class="fa-solid fa-filter filter-icon"></i>
                        <span>Filtri</span>
                    </div>
                    
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

                <div class="book-content">
                    <h3 class="book-title"><%= libro.getTitolo() %></h3>
                    <p class="book-author" style="font-weight:bold; color:#555;"><%= libro.getAutore() %></p>
                    <p style="color: #777; font-size: 0.9rem; margin-bottom: 15px;"><%= libro.getDescrizione() != null ? libro.getDescrizione() : "Nessuna descrizione." %></p>

                    <div class="status-row">
                        <span class="label-gray">Copie:</span> 
                        <strong><%= libro.getDisponibilita() %></strong>
                        <span style="margin: 0 10px; color: #ccc;">|</span>
                        <span class="label-gray">Stato:</span>
                        <% if (disponibile) { %>
                            <span class="status-ok">Disponibile</span>
                        <% } else { %>
                            <span class="status-ko">Non disponibile</span>
                        <% } %>
                    </div>
                </div>

                <div class="book-actions-right">
                    <a href="ModificaLibroServlet?id=<%= libro.getId() %>" class="btn-action-edit">
                        <i class="fa-solid fa-pen-to-square"></i> Modifica
                    </a>

                    <a href="RimuoviLibroServlet?id=<%= libro.getId() %>" class="btn-action-delete" onclick="return confirm('Sei sicuro di voler rimuovere questo libro?');">
                        <i class="fa-solid fa-trash"></i> Rimuovi
                    </a>
                </div>
            </div>

            <% 
                } 
            } else {
            %>
                <div style="text-align:center; padding: 50px; background: white; border-radius: 8px;">
                    <p>Nessun libro trovato nel catalogo.</p>
                </div>
            <% } %>
        </div>
    </main>

    <script>
        function toggleFilters(event) {
            const menu = document.getElementById('filterDropdown');
            menu.classList.toggle('active');
            event.stopPropagation();
        }

        document.addEventListener('click', function(event) {
            const wrapper = document.querySelector('.filter-wrapper');
            const menu = document.getElementById('filterDropdown');
            if (wrapper && !wrapper.contains(event.target)) {
                menu.classList.remove('active');
            }
        });

        document.addEventListener("DOMContentLoaded", () => {
            const cards = document.querySelectorAll('.search-item');
            const selectGenere = document.getElementById('filterGenere');
            const generiTrovati = new Set(); 

            cards.forEach(card => {
                const genere = card.getAttribute('data-genere');
                if (genere) generiTrovati.add(genere);
            });

            generiTrovati.forEach(genere => {
                const option = document.createElement('option');
                option.value = genere;
                option.textContent = genere;
                selectGenere.appendChild(option);
            });
        });

        const searchInput = document.getElementById('searchInput');
        const selectGenere = document.getElementById('filterGenere');
        const selectDisp = document.getElementById('filterDisp');

        function applicaFiltri() {
            const searchTerm = searchInput.value.toLowerCase();
            const selectedGenre = selectGenere.value;
            const selectedDisp = selectDisp.value; 

            const cards = document.querySelectorAll('.search-item');

            cards.forEach(card => {
                const title = card.querySelector('.book-title').innerText.toLowerCase();
                const author = card.querySelector('.book-author').innerText.toLowerCase();
                const cardGenre = card.getAttribute('data-genere');
                const cardDisp = card.getAttribute('data-disponibile');

                const matchSearch = title.includes(searchTerm) || author.includes(searchTerm);
                const matchGenre = (selectedGenre === 'all') || (cardGenre === selectedGenre);
                const matchDisp = (selectedDisp === 'all') || (cardDisp === selectedDisp);

                if (matchSearch && matchGenre && matchDisp) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        }

        searchInput.addEventListener('keyup', applicaFiltri);

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