<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>
<%@ page import="it.bookmarker.model.Recensione" %>

<%
    Libro libro = (Libro) request.getAttribute("libroDettaglio");
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    List<Recensione> elencoRecensioni = (List<Recensione>) request.getAttribute("listaRecensioni");
    
    if (libro == null) { 
        response.sendRedirect("CatalogoModeratoreServlet"); 
        return; 
    }
%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= libro.getTitolo() %> - Moderazione</title>
    
    <link rel="stylesheet" href="css/catalogo.css"> 
    <link rel="stylesheet" href="css/dettaglioModeratore.css"> 
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        @keyframes highlightPulse {
            0% { background-color: #f9f9f9; transform: scale(1); box-shadow: 0 0 0 rgba(0,0,0,0); }
            15% { background-color: #fff3cd; transform: scale(1.02); box-shadow: 0 0 20px rgba(243, 156, 18, 0.5); border-color: #f39c12; }
            80% { background-color: #fff3cd; transform: scale(1.02); border-color: #f39c12; }
            100% { background-color: #f9f9f9; transform: scale(1); box-shadow: 0 0 0 rgba(0,0,0,0); }
        }

        .highlight-target {
            animation: highlightPulse 3s ease-out forwards;
            border: 2px solid #f39c12 !important;
            z-index: 10;
            position: relative;
        }

        .review-card {
            scroll-margin-top: 180px; 
        }

        .review-card.deleted {
            border-left: 4px solid #c0392b;
            background-color: #fff5f5;
            opacity: 0.8;
        }

        .deleted-badge {
            background-color: #c0392b;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            margin-left: 10px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
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
            <span class="user-greeting">Area Moderatore</span>
            <a href="CatalogoModeratoreServlet" class="btn">Torna al Catalogo</a>
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main class="detail-wrapper">
        <div class="white-box">
            
            <div class="product-hero">
                <div class="hero-image-container">
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
                        <% } %>
                    </div>
                    
                    <p class="description-text">
                        <%= (libro.getDescrizione() != null) ? libro.getDescrizione() : "Nessuna descrizione disponibile." %>
                    </p>

                    <button class="btn-black" onclick="scrollToReviews()">Gestisci Recensioni</button>
                </div>
            </div>

            <div class="reviews-section" id="reviewsAnchor">
                <h3 style="margin-bottom: 20px; border-bottom: 2px solid #ddd; padding-bottom: 10px;">
                    Moderazione Recensioni
                </h3>
                
                <% 
                if (elencoRecensioni == null || elencoRecensioni.isEmpty()) { 
                %>
                    <div style="padding: 20px; background: #f9f9f9; border-radius: 6px; color: #777; font-style: italic;">
                        Nessuna recensione presente per questo libro.
                    </div>
                <% 
                } else {
                    for (Recensione rec : elencoRecensioni) {
                        boolean isEliminata = rec.isEliminata();
                        boolean isHidden = !rec.isVisibile(); 
                        
                        String cardClass = "review-card";
                        if (isEliminata) {
                            cardClass += " deleted";
                        } else if (isHidden) {
                            cardClass += " hidden";
                        }
                %>
                
                <div id="review-<%= rec.getId() %>" class="<%= cardClass %>">
                    
                    <div class="review-header">
                        <span>
                            <i class="fa-solid fa-user" style="margin-right: 8px; color:#888;"></i>
                            <%= rec.getNomeUtenteDisplay() %>
                            
                            <% if (isEliminata) { %>
                                <span class="deleted-badge"><i class="fa-solid fa-trash-can"></i> Eliminata</span>
                            <% } else if (isHidden) { %>
                                <span class="hidden-badge"><i class="fa-solid fa-eye-slash"></i> Nascosta</span>
                            <% } %>
                        </span>
                        
                        <div class="review-actions">
                            <small style="font-weight: normal; color: #999;">
                                <%= rec.getDataInserimento() %>
                            </small>

                            <% if (!isEliminata) { %>
                                <form action="GestioneRecensioniServlet" method="post">
                                    <input type="hidden" name="idRecensione" value="<%= rec.getId() %>">
                                    <input type="hidden" name="idLibro" value="<%= libro.getId() %>">
                                    
                                    <% if (isHidden) { %>
                                        <input type="hidden" name="azione" value="mostra">
                                        <button type="submit" class="btn-toggle-vis" title="Rendi visibile">
                                            <i class="fa-solid fa-eye"></i> Mostra
                                        </button>
                                    <% } else { %>
                                        <input type="hidden" name="azione" value="nascondi">
                                        <button type="submit" class="btn-toggle-vis" title="Nascondi agli utenti">
                                            <i class="fa-solid fa-eye-slash"></i> Nascondi
                                        </button>
                                    <% } %>
                                </form>

                                <form id="form-del-<%= rec.getId() %>" action="GestioneRecensioniServlet" method="post">
                                    <input type="hidden" name="azione" value="rimuovi">
                                    <input type="hidden" name="idRecensione" value="<%= rec.getId() %>">
                                    <input type="hidden" name="idLibro" value="<%= libro.getId() %>">
                                    
                                    <button type="button" class="btn-delete-review" title="Elimina Recensione" 
                                            onclick="apriModalElimina('form-del-<%= rec.getId() %>')">
                                        <i class="fa-solid fa-trash"></i> Elimina
                                    </button>
                                </form>
                            <% } %>
                        </div>
                    </div>

                    <div class="review-stars" style="color: #f1c40f; font-size: 0.9rem; margin: 8px 0;">
                        <% 
                        int voto = rec.getVoto();
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
                    }
                } 
                %>
            </div>
        </div>
    </main>

    <div id="deleteModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon">
                <i class="fa-solid fa-triangle-exclamation"></i>
            </div>
            <h3 class="modal-title">Elimina Recensione</h3>
            <p class="modal-text">Sei sicuro di voler eliminare questa recensione? L'operazione non è reversibile.</p>
            <div class="modal-buttons">
                <button class="btn-modal btn-cancel" onclick="chiudiModal()">Annulla</button>
                <button id="confirmBtn" class="btn-modal btn-confirm-delete" onclick="confermaEliminazione()">Sì, Elimina</button>
            </div>
        </div>
    </div>

    <script>
    
    document.addEventListener("DOMContentLoaded", function() {
        
        if (window.location.hash) {
            const idTarget = window.location.hash.substring(1); 
            const element = document.getElementById(idTarget);
            
            if (element) {
                
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
                
                
                element.classList.add('highlight-target');
                
                
                setTimeout(() => {
                    element.classList.remove('highlight-target');
                }, 3000);
            }
        }
    });

    function scrollToReviews() {
        const reviewsSection = document.getElementById('reviewsAnchor');
        if (reviewsSection) {
            reviewsSection.scrollIntoView({ behavior: 'smooth' });
        }
    }

    let formDaInviareId = null; 

    function apriModalElimina(formId) {
        formDaInviareId = formId; 
        document.getElementById('deleteModal').style.display = 'flex'; 
    }

    function chiudiModal() {
        document.getElementById('deleteModal').style.display = 'none';
        formDaInviareId = null;
    }

    function confermaEliminazione() {
        if (formDaInviareId) {
            document.getElementById(formDaInviareId).submit(); 
        }
    }

    window.onclick = function(event) {
        if (event.target == document.getElementById('deleteModal')) {
            chiudiModal();
        }
    }
    </script>
</body>
</html>