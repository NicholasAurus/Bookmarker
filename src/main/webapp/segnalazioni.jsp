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
                <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
            <% } else { %>
                <a href="login.jsp" class="btn">Login</a>
            <% } %>
        </nav>
    </header>

    <main>
        <section class="blue-bar">
            <div class="container-inner">
                <div class="title-wrapper">
                    <h2 class="section-title">Segnalazioni</h2>
                </div>
                
                <div class="search-wrapper">
                    <div class="search-box-inner">
                        <input type="text" id="searchInput" placeholder="Cerca per email o motivo..." class="search-input">
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
                            <label for="filterStato">Stato:</label>
                            <select id="filterStato" class="filter-select" onchange="applicaFiltri()">
                                <option value="all">Tutte</option>
                                <option value="aperta">Aperta</option>
                                <option value="chiusa">Chiusa</option>
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

        <div class="book-container" id="containerSegnalazioni">
            
            <% if (elencoSegnalazioni == null || elencoSegnalazioni.isEmpty()) { %>
                <div style="text-align:center; padding: 50px; background: white; border-radius: 8px;">
                    <i class="fa-solid fa-circle-check" style="font-size: 3rem; color: #27ae60; margin-bottom: 20px;"></i>
                    <p>Non ci sono segnalazioni da gestire.</p>
                </div>
            <% } else { 
                for (Segnalazione seg : elencoSegnalazioni) { 
                    String statoClass = "status-" + seg.getStato().toLowerCase();
            %>

            <div class="book-card-stroke search-item" 
                 data-email="<%= seg.getUtenteEmail().toLowerCase() %>" 
                 data-motivo="<%= seg.getMotivo().toLowerCase() %>"
                 data-stato="<%= seg.getStato().toLowerCase() %>">
                 
                <div class="book-asset">
                    <i class="fa-solid fa-user"></i>
                </div>
                
                <div class="book-content">
                    
                    <h3 class="book-title">Utente: <%= seg.getUtenteEmail() %></h3>
                    
                    <p class="book-author" style="font-weight: bold; color: #555; margin-bottom: 5px;">
                        Segnala Recensione #<%= seg.getRecensioneId() %>
                    </p>
                    
                    <div style="margin-bottom: 10px; color: #777; font-size: 0.9rem;">
                        <i class="fa-regular fa-calendar"></i> <%= seg.getDataSegnalazione() %>
                    </div>

                    <p style="font-size: 0.95rem; color: #333; margin: 10px 0; background: #f9f9f9; padding: 10px; border-radius: 5px;">
                        <em>"<%= seg.getMotivo() %>"</em>
                    </p>
                    
                    <div class="button-group">
                        <span class="btn-neutral">Stato:</span>
                        <span class="status-value <%= statoClass %>"><%= seg.getStato() %></span>

                        <% if ("Aperta".equalsIgnoreCase(seg.getStato())) { %>
                            <div class="action-divider"></div>
                            
                            <form action="RisolviSegnalazioneServlet" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="<%= seg.getId() %>">
                                <button type="submit" class="btn-action btn-resolve" title="Risolvi">
                                    <i class="fa-solid fa-check"></i> Risolvi
                                </button>
                            </form>
                            
                            <form action="IgnoraSegnalazioneServlet" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="<%= seg.getId() %>">
                                <button type="submit" class="btn-action btn-ignore" title="Ignora">
                                    <i class="fa-solid fa-xmark"></i> Ignora
                                </button>
                            </form>
                        <% } %>
                    </div>
                </div>
            </div>

            <% } } %>
            
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

        const searchInput = document.getElementById('searchInput');
        const selectStato = document.getElementById('filterStato');

        function applicaFiltri() {
            const searchTerm = searchInput.value.toLowerCase();
            const selectedStato = selectStato.value.toLowerCase();
            
            let cards = document.querySelectorAll('.search-item');
          
            cards.forEach(card => {
                const email = card.getAttribute('data-email');
                const motivo = card.getAttribute('data-motivo');
                const stato = card.getAttribute('data-stato');

                const matchSearch = email.includes(searchTerm) || motivo.includes(searchTerm);
                
                let matchStato = false;
                if (selectedStato === 'all') {
                    matchStato = true;
                } else if (selectedStato === 'aperta') {
                    matchStato = (stato === 'aperta');
                } else if (selectedStato === 'chiusa') {
                    matchStato = (stato !== 'aperta'); 
                }

                if (matchSearch && matchStato) {
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
            selectStato.value = 'all';
            searchInput.value = '';
            applicaFiltri();
        }
    </script>

</body>
</html>