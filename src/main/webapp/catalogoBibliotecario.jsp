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

    <style>
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            z-index: 2000;
            justify-content: center; align-items: center;
            animation: fadeIn 0.3s;
        }
        .modal-box {
            background: white; padding: 30px; border-radius: 8px;
            width: 400px; text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3); animation: slideUp 0.3s;
        }
        .modal-icon { font-size: 3rem; margin-bottom: 15px; }
        .modal-title { font-size: 1.2rem; margin-bottom: 10px; color: #333; }
        .modal-text { color: #666; margin-bottom: 25px; }
        .modal-buttons { display: flex; justify-content: center; gap: 15px; }
        .btn-modal { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 14px; }
        .btn-cancel { background-color: #e0e0e0; color: #333; }
        .btn-confirm { background-color: #c0392b; color: white; }
        .btn-save { background-color: #27ae60; color: white; } /* Verde per salvare */

        /* Stili specifici per l'input modifica */
        .modal-input-group { margin: 20px 0; text-align: left; }
        .modal-label { display: block; margin-bottom: 8px; font-weight: bold; color: #333; font-size: 0.9rem;}
        .modal-input { width: 100%; padding: 10px; font-size: 1.2rem; border: 1px solid #ccc; border-radius: 5px; text-align: center; }
        .book-title-preview { font-style: italic; color: #267bbc; margin-bottom: 15px; font-weight: 600; font-size: 1.1rem;}

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes slideUp { from { transform: translateY(20px); } to { transform: translateY(0); } }
    </style>
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
                    // Escaping del titolo per evitare problemi col JS
                    String titoloSafe = libro.getTitolo().replace("'", "\\'");
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
                    
                    <button type="button" class="btn-action-edit" 
                            style="cursor: pointer; width: 100%; border: none;"
                            onclick="apriModalModifica('<%= libro.getId() %>', '<%= titoloSafe %>', '<%= libro.getDisponibilita() %>')">
                        <i class="fa-solid fa-pen-to-square"></i> Modifica
                    </button>

                    <button type="button" class="btn-action-delete" 
                            style="cursor: pointer; width: 100%; border: none;"
                            onclick="apriModalEliminazione('<%= libro.getId() %>')">
                        <i class="fa-solid fa-trash"></i> Rimuovi
                    </button>
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


    <div id="deleteModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon">
                <i class="fa-solid fa-triangle-exclamation" style="color: #c0392b;"></i>
            </div>
            <h3 class="modal-title">Elimina Libro</h3>
            <p class="modal-text">Sei sicuro di voler rimuovere definitivamente questo libro dal catalogo?</p>
            <div class="modal-buttons">
                <button class="btn-modal btn-cancel" onclick="chiudiModal('deleteModal')">Annulla</button>
                <button id="confirmDeleteBtn" class="btn-modal btn-confirm">Rimuovi</button>
            </div>
        </div>
    </div>


    <div id="editModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon">
                <i class="fa-solid fa-boxes-stacked" style="color: #267bbc;"></i>
            </div>
            <h3 class="modal-title">Aggiorna Copie</h3>
            
            <p id="editBookTitle" class="book-title-preview">Titolo Libro</p>

            <form action="ModificaLibroServlet" method="post">
                <input type="hidden" name="id" id="editBookId">
                <input type="hidden" name="azione" value="aggiornaQuantita">

                <div class="modal-input-group">
                    <label class="modal-label">Numero di Copie:</label>
                    <input type="number" name="quantita" id="editBookQty" class="modal-input" min="0" required>
                </div>

                <div class="modal-buttons">
                    <button type="button" class="btn-modal btn-cancel" onclick="chiudiModal('editModal')">Annulla</button>
                    <button type="submit" class="btn-modal btn-save">Salva Modifiche</button>
                </div>
            </form>
        </div>
    </div>


    <script>
        
        let urlCancellazione = "";

        function apriModalEliminazione(idLibro) {
            urlCancellazione = "RimuoviLibroServlet?id=" + idLibro;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        
        function apriModalModifica(id, titolo, quantitaAttuale) {
          
            document.getElementById('editBookId').value = id;
            document.getElementById('editBookTitle').innerText = titolo;
            document.getElementById('editBookQty').value = quantitaAttuale;
            
            
            document.getElementById('editModal').style.display = 'flex';
        }

        
        function chiudiModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
            if(modalId === 'deleteModal') urlCancellazione = "";
        }

        
        document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
            if (urlCancellazione) {
                window.location.href = urlCancellazione;
            }
        });

      
        window.onclick = function(event) {
            if (event.target.classList.contains('modal-overlay')) {
                event.target.style.display = 'none';
            }
            
         
            const wrapper = document.querySelector('.filter-wrapper');
            const menu = document.getElementById('filterDropdown');
            if (wrapper && !wrapper.contains(event.target) && menu && menu.classList.contains('active')) {
                menu.classList.remove('active');
            }
        }

     
        function toggleFilters(event) {
            const menu = document.getElementById('filterDropdown');
            menu.classList.toggle('active');
            event.stopPropagation();
        }

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
                option.value = genere; option.textContent = genere;
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

        function resetSearch() { searchInput.value = ''; applicaFiltri(); }
        function resetFiltri() { selectGenere.value = 'all'; selectDisp.value = 'all'; searchInput.value = ''; applicaFiltri(); }
    </script>
</body>
</html>