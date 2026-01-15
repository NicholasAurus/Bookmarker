<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="it.bookmarker.model.Prestito" %>
<%@ page import="it.bookmarker.model.Recensione" %>

<%
    List<Prestito> storico = (List<Prestito>) request.getAttribute("elencoStorico");
    // Recupero la mappa delle recensioni
    Map<Integer, Recensione> mappaRecensioni = (Map<Integer, Recensione>) request.getAttribute("mappaRecensioni");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Storico - BookMarker</title>
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
            <div class="container-inner" style="width: 100%; max-width: 1200px;">
                <h2 class="section-title" style="margin: 0; white-space: nowrap;">Storico</h2>
                
                <div class="search-wrapper">
                    <input type="text" id="searchInput" placeholder="Ricerca per titolo..." class="search-input" style="width: 100%;">
                    <i class="fa-solid fa-xmark close-icon" id="resetBtn"></i>
                </div>

                <div class="filter-wrapper" onclick="toggleFilters(event)" style="cursor: pointer;">
                    <i class="fa-solid fa-filter filter-icon"></i>
                    
                    <div class="filter-dropdown" id="filterDropdown" onclick="event.stopPropagation()" style="cursor: default;">
                        <div class="filter-group">
                            <label style="font-weight:bold; color:#333;">Stato:</label>
                            <select id="filterStato" class="filter-select" onchange="applicaFiltriGlobale()">
                                <option value="all">Tutti</option>
                                <option value="richiesto">Richiesti</option>
                                <option value="prenotato">Prenotati</option>
                                <option value="in-corso">In corso</option>
                                <option value="concluso">Conclusi</option>
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
                    
                    String statoDB = (p.getStato() != null) ? p.getStato() : "";
                    String badgeClass = "";
                    String labelStato = "";
                    String filterCategory = ""; 
                    String labelData1 = "";
                    String valData1 = "";
                    String labelData2 = "";
                    String valData2 = "";
                    boolean abilitaRecensione = false;

                    // Logica Stati (Invariata)
                    if (statoDB.equalsIgnoreCase("Richiesto")) {
                        badgeClass = "badge-richiesto"; labelStato = "Richiesto"; filterCategory = "richiesto";
                        labelData1 = "Data Prenotazione:"; valData1 = (p.getDataPrenotazione() != null) ? sdf.format(p.getDataPrenotazione()) : "--/--/----";
                        labelData2 = "Data Ritiro:"; valData2 = "In attesa";
                    } else if (statoDB.equalsIgnoreCase("Prenotato")) {
                        badgeClass = "badge-prenotato"; labelStato = "Prenotato"; filterCategory = "prenotato";
                        labelData1 = "Data Prenotazione:"; valData1 = (p.getDataPrenotazione() != null) ? sdf.format(p.getDataPrenotazione()) : "--/--/----";
                        labelData2 = "Stato Ritiro:"; valData2 = "Pronto al ritiro";
                    } else if (statoDB.equalsIgnoreCase("In Corso")) {
                        badgeClass = "badge-corso"; labelStato = "In Corso"; filterCategory = "in-corso"; 
                        labelData1 = "Data Inizio:"; valData1 = (p.getDataInizio() != null) ? sdf.format(p.getDataInizio()) : "--/--/----";
                        labelData2 = "Scadenza Prevista:"; valData2 = (p.getDataFinePrevista() != null) ? sdf.format(p.getDataFinePrevista()) : "--/--/----";
                    } else { 
                        badgeClass = "badge-restituito"; labelStato = "Restituito"; filterCategory = "concluso"; 
                        labelData1 = "Data Inizio:"; valData1 = (p.getDataInizio() != null) ? sdf.format(p.getDataInizio()) : "--/--/----";
                        labelData2 = "Data Restituzione:"; valData2 = (p.getDataRestituzioneEffettiva() != null) ? sdf.format(p.getDataRestituzioneEffettiva()) : "--/--/----";
                        abilitaRecensione = true;
                    }
            %>
            
            <div class="history-card search-item" data-stato="<%= filterCategory %>">
                <div class="history-asset">
                    <% if(p.getCopertinaLibro() != null) { %>
                        <img src="<%= p.getCopertinaLibro() %>" alt="cover" style="max-width:100%; max-height:100%;">
                    <% } else { %>
                        <i class="fa-regular fa-image" style="font-size: 2rem; color: #ccc;"></i>
                    <% } %>
                </div>

                <div class="history-content">
                    <h3 class="card-title"><%= p.getTitoloLibro() %></h3>
                    
                    <% if (abilitaRecensione) { %>
                        <% if (!p.isRecensito()) { %>
                            <button type="button" class="btn-recensione" 
                                    data-id="<%= p.getLibroId() %>" 
                                    data-titolo="<%= p.getTitoloLibro().replace("\"", "&quot;") %>"
                                    onclick="apriModalRecensione(this)">
                                Lascia Recensione
                            </button>
                        <% } else { 
                            // Recupero la recensione dalla mappa
                            Recensione rec = null;
                            if (mappaRecensioni != null) {
                                rec = mappaRecensioni.get(p.getLibroId());
                            }
                            String testoRec = (rec != null) ? rec.getTesto() : "";
                            int votoRec = (rec != null) ? rec.getVoto() : 0;
                        %>
                            <div style="display:flex; align-items:center; gap:15px;">
                                <button type="button" class="btn-recensione" style="background-color: #3498db;"
                                        data-id="<%= p.getLibroId() %>" 
                                        data-titolo="<%= p.getTitoloLibro().replace("\"", "&quot;") %>"
                                        data-voto="<%= votoRec %>"
                                        data-testo="<%= testoRec.replace("\"", "&quot;") %>"
                                        onclick="apriModalRecensione(this)">
                                    Visualizza/Modifica
                                </button>
                                
                                <button type="button" class="btn-delete-review" 
                                        onclick="apriModalElimina(<%= p.getLibroId() %>)"
                                        title="Elimina la tua recensione"
                                        style="background:none; border:none;">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        <% } %>
                    <% } %>
                </div>

                <div class="history-right">
                    <div class="date-label"><%= labelData1 %> <%= valData1 %></div>
                    <div class="date-label"><%= labelData2 %> <%= valData2 %></div>
                    
                    <div class="<%= badgeClass %>"><%= labelStato %></div>
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

    <div id="reviewModal" class="modal">
        <div class="modal-content">
            <span class="close-modal" onclick="chiudiModalRecensione()">&times;</span>
            <h3 id="modalTitle">Scrivi Recensione</h3>
            <p style="margin-bottom: 20px; font-style: italic; color: #666;">Libro: <span id="modalTitoloLibro" style="font-weight: bold;"></span></p>
            
            <form action="AddRecensioneServlet" method="POST">
                <input type="hidden" id="modalIdLibro" name="idLibro" value="">
                
                <div class="rating-title">Il tuo voto:</div>
                <div class="rating-wrapper">
                    <input type="radio" name="voto" id="star5" value="5" required><label for="star5" title="Eccellente"><i class="fa-solid fa-star"></i></label>
                    <input type="radio" name="voto" id="star4" value="4"><label for="star4" title="Molto buono"><i class="fa-solid fa-star"></i></label>
                    <input type="radio" name="voto" id="star3" value="3"><label for="star3" title="Buono"><i class="fa-solid fa-star"></i></label>
                    <input type="radio" name="voto" id="star2" value="2"><label for="star2" title="Mediocre"><i class="fa-solid fa-star"></i></label>
                    <input type="radio" name="voto" id="star1" value="1"><label for="star1" title="Scarso"><i class="fa-solid fa-star"></i></label>
                </div>

                <div class="modal-form-group">
                    <label for="testoRecensione">La tua recensione (min. 20 caratteri)</label>
                    <textarea id="testoRecensione" name="testo" required minlength="20" placeholder="Scrivi qui cosa ne pensi..."></textarea>
                </div>
                
                <button type="submit" class="btn-submit-review">Salva Recensione</button>
            </form>
        </div>
    </div>

    <div id="deleteModal" class="modal">
        <div class="modal-content" style="max-width: 400px; text-align: center;">
            <span class="close-modal" onclick="chiudiModalElimina()">&times;</span>
            <h3 style="color: #c0392b;">Elimina Recensione</h3>
            <p>Sei sicuro di voler eliminare definitivamente la tua recensione per questo libro?</p>
            <div style="display: flex; gap: 10px; justify-content: center; margin-top: 20px;">
                <button type="button" class="btn-neutral" onclick="chiudiModalElimina()">Annulla</button>
                <a id="btnConfirmDelete" href="#" class="btn-submit-review" style="background-color: #c0392b; text-decoration: none; padding: 10px 20px; display: inline-block;">Elimina</a>
            </div>
        </div>
    </div>

    <script>
        function apriModalRecensione(btn) {
            const idLibro = btn.getAttribute('data-id');
            const titoloLibro = btn.getAttribute('data-titolo');
            const votoEsistente = btn.getAttribute('data-voto');
            const testoEsistente = btn.getAttribute('data-testo');

            document.getElementById('modalIdLibro').value = idLibro;
            document.getElementById('modalTitoloLibro').innerText = titoloLibro;
            
            const titleElem = document.getElementById('modalTitle');
            const textArea = document.getElementById('testoRecensione');
            
            // Reset stelle
            const radios = document.getElementsByName('voto');
            for(let i=0; i<radios.length; i++) radios[i].checked = false;

            if (votoEsistente && testoEsistente) {
                // MODALITÀ MODIFICA
                titleElem.innerText = "Modifica Recensione";
                textArea.value = testoEsistente;
                
                const radioDaSelezionare = document.getElementById('star' + votoEsistente);
                if (radioDaSelezionare) radioDaSelezionare.checked = true;
            } else {
                // MODALITÀ NUOVA
                titleElem.innerText = "Scrivi Recensione";
                textArea.value = "";
            }

            document.getElementById('reviewModal').style.display = "block";
        }

        function chiudiModalRecensione() {
            document.getElementById('reviewModal').style.display = "none";
        }

        function apriModalElimina(idLibro) {
            const deleteLink = document.getElementById('btnConfirmDelete');
            deleteLink.href = "RimuoviRecensioneServlet?idLibro=" + idLibro;
            document.getElementById('deleteModal').style.display = "block";
        }

        function chiudiModalElimina() {
            document.getElementById('deleteModal').style.display = "none";
        }

        window.onclick = function(event) {
            const modalRev = document.getElementById('reviewModal');
            const modalDel = document.getElementById('deleteModal');
            if (event.target == modalRev) {
                modalRev.style.display = "none";
            }
            if (event.target == modalDel) {
                modalDel.style.display = "none";
            }
        }

        function toggleFilters(event) {
            const dropdown = document.getElementById('filterDropdown');
            dropdown.classList.toggle('active');
            event.stopPropagation();
        }

        document.addEventListener('click', function(event) {
            const dropdown = document.getElementById('filterDropdown');
            const filterWrapper = document.querySelector('.filter-wrapper');
            if (filterWrapper && !filterWrapper.contains(event.target)) {
                dropdown.classList.remove('active');
            }
        });

        function applicaFiltriGlobale() {
            const inputTesto = document.getElementById('searchInput').value.toLowerCase();
            const statoSelezionato = document.getElementById('filterStato').value;
            
            const cards = document.querySelectorAll('.search-item');
            const resetBtn = document.getElementById('resetBtn');
            const noResultsMsg = document.getElementById('noResults');
            
            let visibleCount = 0;

            resetBtn.style.display = (inputTesto.length > 0) ? 'block' : 'none';

            cards.forEach(card => {
                const titolo = card.querySelector('.card-title').innerText.toLowerCase();
                const statoCard = card.getAttribute('data-stato');

                const matchTesto = titolo.includes(inputTesto);
                const matchStato = (statoSelezionato === 'all') || (statoSelezionato === statoCard);

                if (matchTesto && matchStato) {
                    card.style.display = "flex"; 
                    visibleCount++;
                } else {
                    card.style.display = "none";
                }
            });

            if (noResultsMsg) {
                noResultsMsg.style.display = (visibleCount === 0 && cards.length > 0) ? 'block' : 'none';
            }
        }

        function resetFiltri() {
            document.getElementById('searchInput').value = '';
            document.getElementById('filterStato').value = 'all';
            applicaFiltriGlobale();
            document.getElementById('filterDropdown').classList.remove('active');
        }

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