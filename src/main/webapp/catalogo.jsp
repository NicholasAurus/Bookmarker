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
                    <i class="fa-solid fa-xmark close-icon" onclick="document.getElementById('searchInput').value=''; filtraLibri();"></i>
                </div>

                <div class="filter-wrapper">
                    <i class="fa-solid fa-filter filter-icon"></i>
                    <span>Filtri</span>
                </div>
            </div>
        </section>

        <div class="book-container">
            
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

            <div class="book-card-stroke search-item">
                <div class="book-asset">
                    <% if (hasImg) { %>
                        <img src="<%= imgPath %>" alt="Copertina" style="max-width:100%; max-height:100%;">
                    <% } else { %>
                        <i class="fa-regular fa-image"></i>
                    <% } %>
                </div>
                
                <div class="book-content">
                    <h3 class="book-title"><%= libro.getTitolo() %></h3>
                    <p class="book-author" style="font-weight: bold; color: #555; margin-bottom: 5px;">
                        <%= libro.getAutore() %>
                    </p>
                    
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
        const searchInput = document.getElementById('searchInput');
        
        searchInput.addEventListener('keyup', function() {
            const term = searchInput.value.toLowerCase();
            const cards = document.querySelectorAll('.search-item');

            cards.forEach(card => {
                const title = card.querySelector('.book-title').innerText.toLowerCase();
                const author = card.querySelector('.book-author').innerText.toLowerCase();
                
                if (title.includes(term) || author.includes(term)) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    </script>

</body>
</html>