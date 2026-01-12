<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>
<%@ page import="it.bookmarker.model.Recensione" %> 

<%
    // Recuperiamo il libro passato dalla Servlet
    Libro libro = (Libro) request.getAttribute("libroDettaglio");
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    
    // Recuperiamo la lista delle recensioni passata dalla Servlet
    List<Recensione> elencoRecensioni = (List<Recensione>) request.getAttribute("listaRecensioni");
    
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
                
                <% 
                if (elencoRecensioni == null || elencoRecensioni.isEmpty()) { 
                %>
                    <div style="padding: 20px; background: #f9f9f9; border-radius: 6px; color: #777; font-style: italic;">
                        Non ci sono ancora recensioni per questo libro. Sii il primo a scriverne una!
                    </div>
                <% 
                } else {
                    
                    for (Recensione rec : elencoRecensioni) {
                %>
                
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
                        <% 
                        int voto = rec.getVoto(); // Assume che Recensione abbia il metodo getVoto() che ritorna int
                        for (int i = 0; i < 5; i++) {
                            if (i < voto) { 
                        %>
                            <i class="fa-solid fa-star"></i>
                        <%  } else { %>
                            <i class="fa-regular fa-star" style="color: #ddd;"></i>
                        <%  }
                        }
                        %>
                    </div>
                    
                    <div class="review-body">
                        <%= rec.getTesto() %>
                    </div>
                </div>

                <% 
                    } // Fine for
                } // Fine else
                %>

            </div>
            
        </div>
    </main>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            
            // Gestione bottone Cuore (Preferiti)
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

        // Funzione per scrollare alle recensioni
        function scrollToReviews() {
            const reviewsSection = document.getElementById('reviewsAnchor');
            if (reviewsSection) {
                reviewsSection.scrollIntoView({ behavior: 'smooth' });
            }
        }
    </script>

</body>
</html>