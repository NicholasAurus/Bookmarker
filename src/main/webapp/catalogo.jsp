<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>

<%
    
    List<Libro> libri = (List<Libro>) request.getAttribute("elencoLibri");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Catalogo Libri - BookMarker</title>
    <link rel="stylesheet" href="css/style.css"> <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        
        .catalogo-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            padding: 20px;
        }
        .card-libro {
            border: 1px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            background: white;
            display: flex;
            flex-direction: column;
        }
        .card-libro img {
            width: 100%;
            height: 350px;
            object-fit: cover; 
        }
        .card-body {
            padding: 15px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .card-titolo { font-size: 1.2rem; font-weight: bold; margin-bottom: 5px; }
        .card-autore { color: #555; font-style: italic; margin-bottom: 10px; }
        .card-stato { margin-top: 10px; font-weight: bold; font-size: 0.9rem; }
        
        .btn-prenota {
            display: block;
            width: 100%;
            padding: 10px;
            background-color: #27ae60;
            color: white;
            text-align: center;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 15px;
            font-weight: bold;
        }
        .btn-prenota:hover { background-color: #219150; }
        
        .btn-disabled {
            display: block;
            width: 100%;
            padding: 10px;
            background-color: #ccc;
            color: #666;
            text-align: center;
            border-radius: 5px;
            margin-top: 15px;
            cursor: not-allowed;
        }
    </style>
</head>
<body>

    

    <main>
        <h1 style="text-align: center; margin-top: 20px;">Catalogo Completo</h1>

        <div class="catalogo-grid">
            <% 
            if (libri != null && !libri.isEmpty()) {
                for (Libro l : libri) {
            %>
                <div class="card-libro">
                    
                    <img src="img/<%= (l.getCopertina() != null) ? l.getCopertina() : "default_book.png" %>" 
                         alt="Copertina di <%= l.getTitolo() %>">

                    <div class="card-body">
                        <div>
                            <div class="card-titolo"><%= l.getTitolo() %></div>
                            <div class="card-autore"><%= l.getAutore() %></div>
                            <small><%= l.getGenere() %> - <%= l.getDataPubblicazione() %></small>
                            
                            <p style="font-size: 0.9em; margin-top: 10px; color: #666;">
                                <%= (l.getDescrizione() != null && l.getDescrizione().length() > 100) 
                                    ? l.getDescrizione().substring(0, 100) + "..." 
                                    : l.getDescrizione() %>
                            </p>
                        </div>

                        <div>
                            <hr style="border: 0; border-top: 1px solid #eee; margin: 10px 0;">
                            
                            <div class="card-stato">
                                <%= l.getMessaggioStato() %>
                            </div>

                            <% if (l.getDisponibilita() > 0) { %>
                                <a href="PrenotaServlet?idLibro=<%= l.getId() %>" class="btn-prenota">
                                    <i class="fas fa-bookmark"></i> Prenota Ora
                                </a>
                            <% } else { %>
                                <div class="btn-disabled">
                                    <i class="fas fa-lock"></i> Non Disponibile
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% 
                } 
            } else { 
            %>
                <p style="text-align:center; width:100%;">Nessun libro trovato nel catalogo.</p>
            <% } %>
        </div>
    </main>

</body>
</html>