<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>
<%@ page import="it.bookmarker.model.Recensione" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Libro libro = (Libro) request.getAttribute("libroDettaglio");
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    List<Recensione> elencoRecensioni = (List<Recensione>) request.getAttribute("listaRecensioni");
    
    String errorePrenotazione = (String) session.getAttribute("errorePrenotazione");
    if (errorePrenotazione != null) {
        session.removeAttribute("errorePrenotazione");
    }
    
    if (libro == null) { 
        response.sendRedirect("LibriServlet"); 
        return; 
    }
    
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatterVisivo = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= libro.getTitolo() %> - Dettaglio</title>
    
    <link rel="stylesheet" href="css/catalogo.css"> 
    <link rel="stylesheet" href="css/dettaglio.css"> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        <nav class="nav-buttons">
            <a href="LibriServlet" class="btn">Torna al Catalogo</a>
            <% if (nomeUtente != null) { %>
                <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
            <% } else { %>
                <a href="login.jsp" class="btn">Login</a>
            <% } %>
        </nav>
    </header>

    <main class="detail-wrapper">
        <div class="white-box">
            <div class="product-hero">
                <div class="hero-image-container">
               <%--Funzionalità dei preferiti ancora non implementata, priority LOW --%>
                    <button class="favorite-btn" title="Aggiungi ai preferiti">
                        <i class="fa-regular fa-heart"></i>
                    </button>
                    <% if (libro.getCopertina() != null && !libro.getCopertina().isEmpty()) { %>
                        <img src="<%= libro.getCopertina() %>" alt="<%= libro.getTitolo() %>">
                    <% } else { %>
                        <i class="fa-regular fa-image placeholder-icon"></i>
                    <% } %>
                </div>

                <div class="hero-details">
                    <h1><%= libro.getTitolo() %></h1>
                    
                    <div class="detail-meta">
                        <strong>Autore:</strong> <%= libro.getAutore() %><br>
                        <strong>Genere:</strong> <%= libro.getGenere() %><br>
                        <strong>Data Pubblicazione:</strong> <%= libro.getDataPubblicazione() %><br><br>
                        
                        <% String msg = request.getParameter("msg"); 
                           String err = request.getParameter("error"); %>

                        <% if ("prenotazione_ok".equals(msg)) { %>
                            <div style="background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; margin-bottom: 15px; border-left: 5px solid #28a745;">
                                <i class="fa-solid fa-check-circle"></i> Prenotazione inviata con successo!
                            </div>
                        <% } else if (errorePrenotazione != null) { %>
                            <div style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px; border-left: 5px solid #dc3545;">
                                <i class="fa-solid fa-circle-exclamation"></i> <%= errorePrenotazione %>
                            </div>
                        <% } else if ("db_error".equals(err)) { %>
                            <div style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px; border-left: 5px solid #dc3545;">
                                <i class="fa-solid fa-triangle-exclamation"></i> Errore. Riprova più tardi.
                            </div>
                        <% } %>

                        <% if(libro.getDisponibilita() > 0) { %>
                            <span style="color: green; font-weight:bold; font-size:1.1rem; display:block; margin-bottom: 15px;">
                                <i class="fa-solid fa-check"></i> Disponibile (<%= libro.getDisponibilita() %> copie)
                            </span>

                            <% if (nomeUtente != null) { %>
                                <form id="bookingForm" action="PrenotaServlet" method="post" style="margin-bottom: 20px; background: #f8f9fa; padding: 15px; border-radius: 5px; border: 1px solid #e9ecef;">
                                    <input type="hidden" name="idLibro" value="<%= libro.getId() %>">
                                    
                                    <label for="dataRitiro" style="display:block; margin-bottom:5px; font-weight:bold; color: #333;">Quando vuoi ritirarlo?</label>
                                    
                                    <select id="dataRitiro" name="dataRitiro" class="custom-select">
                                        <option value="" disabled selected>-- Seleziona una data --</option>
                                        
                                        <option value="<%= today.toString() %>">
                                            Oggi (<%= today.format(formatterVisivo) %>)
                                        </option>
                                        
                                        <option value="<%= today.plusDays(1).toString() %>">
                                            Domani (<%= today.plusDays(1).format(formatterVisivo) %>)
                                        </option>
                                        
                                        <option value="<%= today.plusDays(2).toString() %>">
                                            Dopodomani (<%= today.plusDays(2).format(formatterVisivo) %>)
                                        </option>
                                    </select>
                                    
                                    <div id="msgErroreData" class="error-msg-data">
                                        <i class="fa-solid fa-circle-exclamation"></i> Selezionare una data
                                    </div>
                                    
                                    <button type="button" class="btn-prenota" onclick="apriModal()" style="width: 100%; background-color: #27ae60; color: white; padding: 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; font-weight: bold; transition: background 0.3s;">
                                        Prenota Ora
                                    </button>
                                </form>
                            <% } else { %>
                                <div style="margin-bottom: 20px;">
                                    <a href="login.jsp" class="btn-prenota-disabled" style="background-color: #95a5a6; color: white; padding: 10px 20px; border-radius: 4px; text-decoration: none; display:inline-block;">Accedi per prenotare</a>
                                </div>
                            <% } %>

                        <% } else { %>
                            <span style="color: #c0392b; font-weight:bold; font-size:1.1rem;">
                                <i class="fa-solid fa-xmark"></i> Non disponibile
                            </span>
                            <% if(libro.getDataRientro() != null) { %>
                                <br><small style="color:#555">Rientro previsto: <%= libro.getDataRientro() %></small>
                            <% } %>
                        <% } %>
                    </div>
                    
                    <p class="description-text">
                        <%= (libro.getDescrizione() != null) ? libro.getDescrizione() : "Nessuna descrizione disponibile per questo libro." %>
                    </p>

                    <button class="btn-black" onclick="scrollToReviews()">Visualizza recensioni</button>
                </div>
            </div>

            <div class="reviews-section" id="reviewsAnchor">
                <h3 style="margin-bottom: 20px;">Recensioni</h3>
                <% if (elencoRecensioni == null || elencoRecensioni.isEmpty()) { %>
                    <div style="padding: 20px; background: #f9f9f9; border-radius: 6px; color: #777; font-style: italic;">
                        Non ci sono ancora recensioni per questo libro.
                    </div>
                <% } else {
                    for (Recensione rec : elencoRecensioni) { %>
                    <div class="review-card">
                        <div class="review-header">
                            <span>
                                <i class="fa-solid fa-user" style="margin-right: 8px; color:#888;"></i>
                                <%= rec.getNomeUtenteDisplay() %>
                            </span>
                            
                            <div class="review-meta-right">
                                <small style="font-weight: normal; color: #999;">
                                    <%= rec.getDataInserimento() %>
                                </small>
                                
                                <% if (nomeUtente != null) { %>
                                    <button type="button" class="btn-report" onclick="apriModalSegnalazione('<%= rec.getId() %>')">
                                        <i class="fa-solid fa-flag"></i> Segnala
                                    </button>
                                <% } %>
                            </div>
                        </div>
                        
                        <div class="review-stars" style="color: #f1c40f; font-size: 0.9rem; margin: 8px 0;">
                            <% int voto = rec.getVoto();
                               for (int i = 0; i < 5; i++) {
                                   if (i < voto) { %>
                                    <i class="fa-solid fa-star"></i>
                                   <% } else { %>
                                    <i class="fa-regular fa-star" style="color: #ddd;"></i>
                                   <% }
                               } %>
                        </div>
                        <div class="review-body">
                            <%= rec.getTesto() %>
                        </div>
                    </div>
                <% } } %>
            </div>
        </div>
    </main>

    <div id="confirmModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-title">Conferma Prenotazione</div>
            <div class="modal-text">
                Vuoi confermare la prenotazione del libro per il giorno: <br>
                <strong id="modalDateDisplay" style="color:#27ae60; font-size: 1.2em;"></strong>?
            </div>
            <div class="modal-buttons">
                <button class="btn-modal-cancel" onclick="chiudiModal()">Annulla</button>
                <button class="btn-modal-confirm" onclick="confermaInvio()">Conferma</button>
            </div>
        </div>
    </div>

    <div id="reportModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-title" style="color:#e67e22;"><i class="fa-solid fa-triangle-exclamation"></i> Segnala Recensione</div>
            <div class="modal-text">
                Descrivi il motivo della segnalazione. Il testo deve contenere almeno <strong>20 caratteri</strong>.
            </div>
            
            <form id="formSegnalazione" action="SegnalaRecensioneServlet" method="post" onsubmit="return validaSegnalazione()">
                <input type="hidden" name="idLibro" value="<%= libro.getId() %>">
                <input type="hidden" id="reportRecensioneId" name="idRecensione" value="">
                
                <textarea name="motivo" id="motivoSegnalazione" class="modal-textarea" placeholder="Esempio: Contenuto offensivo, spoiler non segnalato..."></textarea>
                <div id="motivoError" class="error-text">Il motivo è troppo breve (min. 20 caratteri).</div>

                <div class="modal-buttons">
                    <button type="button" class="btn-modal-cancel" onclick="chiudiModalSegnalazione()">Annulla</button>
                    <button type="submit" class="btn-modal-submit">Invia Segnalazione</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            const heartBtn = document.querySelector('.favorite-btn');
            if (heartBtn) {
                heartBtn.addEventListener('click', function() {
                    this.classList.toggle('liked');
                    const icon = this.querySelector('i');
                    if (this.classList.contains('liked')) {
                        icon.classList.remove('fa-regular');
                        icon.classList.add('fa-solid');
                    } else {
                        icon.classList.remove('fa-solid');
                        icon.classList.add('fa-regular');
                    }
                });
            }
        });

        function scrollToReviews() {
            const reviewsSection = document.getElementById('reviewsAnchor');
            if (reviewsSection) {
                reviewsSection.scrollIntoView({ behavior: 'smooth' });
            }
        }

        function apriModal() {
            const dateSelect = document.getElementById('dataRitiro');
            const dateDisplay = document.getElementById('modalDateDisplay');
            const msgErrore = document.getElementById('msgErroreData');
            
            if (!dateSelect.value) {
                msgErrore.style.display = 'block';
                dateSelect.classList.add('input-error-border');
                return;
            }

            msgErrore.style.display = 'none';
            dateSelect.classList.remove('input-error-border');

            const selectedText = dateSelect.options[dateSelect.selectedIndex].text;
            
            dateDisplay.textContent = selectedText;
            document.getElementById('confirmModal').style.display = 'flex';
        }

        function chiudiModal() {
            document.getElementById('confirmModal').style.display = 'none';
        }

        function confermaInvio() {
            document.getElementById('bookingForm').submit();
        }

        function apriModalSegnalazione(idRecensione) {
            document.getElementById('motivoSegnalazione').value = "";
            document.getElementById('motivoError').style.display = "none";
            document.getElementById('reportRecensioneId').value = idRecensione;
            document.getElementById('reportModal').style.display = 'flex';
        }

        function chiudiModalSegnalazione() {
            document.getElementById('reportModal').style.display = 'none';
        }

        function validaSegnalazione() {
            const testo = document.getElementById('motivoSegnalazione').value.trim();
            const errorMsg = document.getElementById('motivoError');
            if (testo.length < 20) {
                errorMsg.style.display = "block";
                return false;
            }
            errorMsg.style.display = "none";
            return true;
        }

        window.onclick = function(event) {
            const modalPrenotazione = document.getElementById('confirmModal');
            const modalSegnalazione = document.getElementById('reportModal');
            if (event.target == modalPrenotazione) {
                modalPrenotazione.style.display = "none";
            }
            if (event.target == modalSegnalazione) {
                chiudiModalSegnalazione();
            }
        }
    </script>
</body>
</html>