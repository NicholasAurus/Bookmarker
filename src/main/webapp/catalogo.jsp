<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.bookmarker.model.Libro" %>

<!DOCTYPE html>
<html>
<head>
    <title>Catalogo Libri</title>
</head>
<body>
    <h2>Catalogo libri disponibili</h2>

    <ul>
        <% 
           // Recuperiamo la lista che la Servlet ci ha passato
           List<Libro> libri = (List<Libro>) request.getAttribute("elencoLibri");

           if(libri != null) {
               for(Libro l : libri) {
        %>
            <li>
                <%= l.getTitolo() %> - <%= l.getAutore() %> - <%= l.getGenere() %>
            </li>
        <% 
               }
           } else {
        %>
            <p>Nessun libro trovato.</p>
        <% } %>
    </ul>

</body>
</html>