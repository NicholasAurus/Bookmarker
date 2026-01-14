<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.bookmarker.model.Libro" %>
<%
    Libro l = (Libro) request.getAttribute("libroDaModificare");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Modifica Disponibilità - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
    <style>
        .form-box { max-width: 500px; margin: 60px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); text-align: center; }
        .book-title { font-size: 1.5rem; color: #333; margin-bottom: 20px; }
        .input-qty { padding: 10px; font-size: 1.2rem; width: 80px; text-align: center; border: 2px solid #3498db; border-radius: 4px; }
        .btn-save { background-color: #3498db; color: white; border: none; padding: 10px 20px; font-size: 1rem; border-radius: 4px; cursor: pointer; margin-top: 20px; }
    </style>
</head>
<body>
    <header style="background-color: rgba(38, 123, 188, 0.79); padding: 20px; text-align: center;">
        <h2 style="color: white; margin: 0;">Gestione Magazzino</h2>
    </header>

    <div class="form-box">
        <% if(l != null) { %>
            <p style="color:#777; margin-bottom: 5px;">Stai modificando:</p>
            <h3 class="book-title"><%= l.getTitolo() %></h3>
            
            <form action="ModificaLibroServlet" method="post">
                <input type="hidden" name="idLibro" value="<%= l.getId() %>">
                
                <label style="display:block; margin-bottom:10px; font-weight:bold;">Numero di Copie:</label>
                <input type="number" name="copie" value="<%= l.getDisponibilita() %>" min="0" class="input-qty">
                
                <br>
                <button type="submit" class="btn-save">Aggiorna Quantità</button>
            </form>
        <% } else { %>
            <p>Errore: Libro non trovato.</p>
        <% } %>
        
        <br>
        <a href="CatalogoBibliotecarioServlet" style="text-decoration:none; color:#777;">Annulla</a>
    </div>
</body>
</html>