<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.bookmarker.model.Libro" %>
<%
    // Recuperiamo il libro passato dalla Servlet
    Libro libro = (Libro) request.getAttribute("libroDettaglio");
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    
    // Se per qualche motivo il libro è null, torniamo al catalogo
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
                           <span style="color: green; font-weight:bold; font-size:1.1rem;">
                               <i class="fa-solid fa-check"></i> Disponibile (<%= libro.getDisponibilita() %> copie)
                           </span>
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
                
                <div class="review-card">
                    <div class="review-header">
                        <span>Utente Mario Rossi</span>
                        <i class="fa-solid fa-chevron-down"></i>
                    </div>
                    <div class="review-body">
                        Un libro davvero interessante, lo consiglio a tutti gli amanti del genere. 
                        La trama è avvincente e i personaggi sono ben costruiti.
                    </div>
                </div>

                <div class="review-card">
                    <div class="review-header">
                        <span>Utente Giulia Bianchi</span>
                        <i class="fa-solid fa-chevron-down"></i>
                    </div>
                    <div class="review-body">
                        La trama è un po' lenta all'inizio, ma poi si riprende. 
                        Comunque una lettura piacevole per il weekend.
                    </div>
                </div>
                
                <div class="review-card">
                    <div class="review-header">
                        <span>Utente Luca Verdi</span>
                        <i class="fa-solid fa-chevron-down"></i>
                    </div>
                    <div class="review-body">
                        Non mi ha convinto del tutto il finale, mi aspettavo qualcosa di diverso.
                    </div>
                </div>

            </div>
            
        </div>
    </main>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            
            // 1. LOGICA CUORE (PREFERITI)
            const heartBtn = document.querySelector('.favorite-btn');
            
            if (heartBtn) {
                heartBtn.addEventListener('click', function() {
                    // Alterna la classe 'liked' per cambiare colore e stile
                    this.classList.toggle('liked');
                    
                    // Seleziona l'icona dentro il bottone
                    const icon = this.querySelector('i');
                    
                    // Cambia l'icona da vuota (fa-regular) a piena (fa-solid) e viceversa
                    if (this.classList.contains('liked')) {
                        icon.classList.remove('fa-regular');
                        icon.classList.add('fa-solid');
                    } else {
                        icon.classList.remove('fa-solid');
                        icon.classList.add('fa-regular');
                    }
                });
            }

            // 2. LOGICA ACCORDION RECENSIONI (Apri/Chiudi)
            const reviews = document.querySelectorAll('.review-card');

            reviews.forEach(card => {
                // Troviamo l'header (la parte cliccabile)
                const header = card.querySelector('.review-header');
                
                header.addEventListener('click', () => {
                    // Aggiunge o toglie la classe 'open' alla card intera
                    // Il CSS gestirà la rotazione della freccia e la comparsa del testo
                    card.classList.toggle('open');
                });
            });
        });

        // 3. FUNZIONE SCROLL TO REVIEWS
        function scrollToReviews() {
            const reviewsSection = document.getElementById('reviewsAnchor');
            if (reviewsSection) {
                reviewsSection.scrollIntoView({ behavior: 'smooth' });
            }
        }
    </script>

</body>
</html>