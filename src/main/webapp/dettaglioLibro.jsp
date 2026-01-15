<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>
<%@ page import="it.bookmarker.model.Recensione" %> 

<%
    Libro libro = (Libro) request.getAttribute("libroDettaglio");
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    List<Recensione> elencoRecensioni = (List<Recensione>) request.getAttribute("listaRecensioni");
    
    if (libro == null) { 
        response.sendRedirect("LibriServlet"); 
        return; 
    }
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
    
    <style>
        /* CSS PER IL MODALE DI CONFERMA */
        .modal-overlay {
            display: none; /* Nascosto di default */
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.6); /* Sfondo scuro trasparente */
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-box {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            width: 90%;
            max-width: 400px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            animation: fadeIn 0.3s ease;
        }

        .modal-title {
            font-size: 1.2rem;
            margin-bottom: 15px;
            color: #333;
            font-weight: bold;
        }

        .modal-text {
            margin-bottom: 25px;
            color: #666;
            font-size: 0.95rem;
        }

        .modal-buttons {
            display: flex;
            justify-content: space-between;
            gap: 10px;
        }

        .btn-modal-cancel {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            flex: 1;
        }

        .btn-modal-confirm {
            background-color: #27ae60;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            flex: 1;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
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
                        
                        <% if(libro.getDisponibilita() > 0) { %>
                            <span style="color: green; font-weight:bold; font-size:1.1rem; display:block; margin-bottom: 15px;">
                                <i class="fa-solid fa-check"></i> Disponibile (<%= libro.getDisponibilita() %> copie)
                            </span>

                            <% if (nomeUtente != null) { %>
                                <form id="bookingForm" action="PrenotaServlet" method="post" style="margin-bottom: 20px; background: #f8f9fa; padding: 15px; border-radius: 5px;">
                                    <input type="hidden" name="idLibro" value="<%= libro.getId() %>">
                                    
                                    <label for="dataRitiro" style="display:block; margin-bottom:5px; font-weight:bold;">Seleziona data ritiro:</label>
                                    <input type="date" id="dataRitiro" name="dataRitiro" required style="padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px; width: 100%;">
                                    
                                    <button type="button" class="btn-prenota" onclick="apriModal()" style="width: 100%; background-color: #27ae60; color: white; padding: 10px; border: none; border-radius: 4px; cursor: pointer;">
                                        Prenota
                                    </button>
                                </form>
                            <% } else { %>
                                <div style="margin-bottom: 20px;">
                                    <a href="login.jsp" class="btn-prenota-disabled" style="background-color: #95a5a6; color: white; padding: 10px 20px; border-radius: 4px; text-decoration: none;">Accedi per prenotare</a>
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

                    <% String msg = request.getParameter("msg"); 
                       String err = request.getParameter("error");
                       if ("prenotazione_ok".equals(msg)) { %>
                        <div style="background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                            <i class="fa-solid fa-check-circle"></i> Prenotazione inviata con successo!
                        </div>
                    <% } else if ("db_error".equals(err)) { %>
                        <div style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                            <i class="fa-solid fa-triangle-exclamation"></i> Errore durante la prenotazione. Riprova.
                        </div>
                    <% } %>
                    
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
                            <small style="font-weight: normal; color: #999;">
                                <%= rec.getDataInserimento() %>
                            </small>
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
                Vuoi confermare la prenotazione del libro per il giorno <br>
                <strong id="modalDateDisplay" style="color:#27ae60; font-size: 1.1em;"></strong>?
            </div>
            <div class="modal-buttons">
                <button class="btn-modal-cancel" onclick="chiudiModal()">Annulla</button>
                <button class="btn-modal-confirm" onclick="confermaInvio()">Conferma</button>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            // Impostazione data minima e massima (1 settimana)
            const dateInput = document.getElementById('dataRitiro');
            if (dateInput) {
                const today = new Date();
                const nextWeek = new Date();
                nextWeek.setDate(today.getDate() + 7);

                const formatDate = (date) => {
                    return date.toISOString().split('T')[0];
                };

                dateInput.setAttribute('min', formatDate(today));
                dateInput.setAttribute('max', formatDate(nextWeek));
                dateInput.value = formatDate(today);
            }
            
            // Gestione preferiti
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
            const dateInput = document.getElementById('dataRitiro');
            const dateDisplay = document.getElementById('modalDateDisplay');
            
            if (!dateInput.value) {
                alert("Seleziona una data valida.");
                return;
            }

            //Copia la data selezionata nel testo del modale
            //Formattiamo la data per renderla leggibile gg/mm/yyyy
            const partiData = dateInput.value.split('-'); 
            const dataFormattata = partiData[2] + '/' + partiData[1] + '/' + partiData[0];
            
            dateDisplay.textContent = dataFormattata;

            // Mostra il modale
            document.getElementById('confirmModal').style.display = 'flex';
        }

        function chiudiModal() {
            document.getElementById('confirmModal').style.display = 'none';
        }

        function confermaInvio() {
            //Invia il form vero e proprio
            document.getElementById('bookingForm').submit();
        }

        // Chiudi modale se si clicca fuori dal box
        window.onclick = function(event) {
            const modal = document.getElementById('confirmModal');
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }
    </script>
</body>
</html>