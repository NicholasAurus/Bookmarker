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
                        
                        <div class="filter-group">
                            <label for="sortOrder">Ordina per:</label>
                            <select id="sortOrder" class="filter-select" onchange="applicaFiltri()">
                                <option value="default">Default</option>
                                <option value="votoDec">Valutazione (Migliori prima)</option>
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
                 data-disponibile="<%= disponibile ? "si" : "no" %>"
                 data-media="<%= libro.getMediaVoti() %>">
                 
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
                    
                    <div style="color: #f1c40f; margin-bottom: 10px;">
                       <% 
                          int stellePiene = (int) Math.round(libro.getMediaVoti());
                          for(int i=0; i<5; i++) {
                              if(i < stellePiene) { %> <i class="fa-solid fa-star"></i> <% } 
                              else { %> <i class="fa-regular fa-star" style="color:#ccc;"></i> <% }
                          }
                       %>
                       <span style="color:#777; font-size:0.8rem; margin-left:5px;">
                           (<%= (libro.getMediaVoti() > 0) ? String.format("%.1f", libro.getMediaVoti()) : "N/A" %>)
                       </span>
                   </div>

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

            
            cards.forEach((card, index) => {
                
                card.setAttribute('data-original-index', index);

                
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
        const selectSort = document.getElementById('sortOrder');

        function applicaFiltri() {
            const searchTerm = searchInput.value.toLowerCase();
            const selectedGenre = selectGenere.value;
            const selectedDisp = selectDisp.value;
            const sortMode = selectSort.value;

            const container = document.getElementById('containerLibri');
    
            let cards = Array.from(document.querySelectorAll('.search-item'));

          
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

            
            if (sortMode === 'votoDec') {
                
                cards.sort((a, b) => {
                    const votoA = parseFloat(a.getAttribute('data-media')) || 0;
                    const votoB = parseFloat(b.getAttribute('data-media')) || 0;
                    return votoB - votoA; 
                });
            } else {
                
                cards.sort((a, b) => {
                    const indexA = parseInt(a.getAttribute('data-original-index'));
                    const indexB = parseInt(b.getAttribute('data-original-index'));
                    return indexA - indexB;
                });
            }
            
            
            cards.forEach(card => container.appendChild(card));
        }
        
        searchInput.addEventListener('keyup', applicaFiltri);

        function resetSearch() {
            searchInput.value = '';
            applicaFiltri();
        }

        function resetFiltri() {
            selectGenere.value = 'all';
            selectDisp.value = 'all';
            selectSort.value = 'default';
            searchInput.value = '';
            applicaFiltri();
        }
    </script>

</body>
</html>