<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    String successMsg = (String) session.getAttribute("successMessage");
    String errorMsg = (String) session.getAttribute("errorMessage");

    if(successMsg != null) session.removeAttribute("successMessage");
    if(errorMsg != null) session.removeAttribute("errorMessage");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aggiungi Libro - BookMarker</title>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="css/catalogoBibliotecario.css">

    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
        
        .form-box { max-width: 600px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        label { display: block; font-weight: bold; margin-bottom: 5px; color: #333; }
        input, textarea, select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; box-sizing: border-box; }
        .btn-submit { background-color: #27ae60; color: white; padding: 12px; width: 100%; border: none; border-radius: 4px; cursor: pointer; font-size: 1.1rem; font-weight: bold; transition: background 0.3s; }
        .btn-submit:hover { background-color: #219150; }
        
        header { background-color: #267bbc; padding: 20px; text-align: center; color: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        h2 { margin: 0; }
    </style>
</head>
<body>

    <div class="toast-container" id="toastContainer">
        <% if (successMsg != null) { %>
            <div class="toast success">
                <i class="fa-solid fa-circle-check toast-icon"></i>
                <div class="toast-message"><%= successMsg %></div>
                <i class="fa-solid fa-xmark toast-close" onclick="this.parentElement.style.display='none'"></i>
            </div>
        <% } %>

        <% if (errorMsg != null) { %>
            <div class="toast error">
                <i class="fa-solid fa-circle-exclamation toast-icon"></i>
                <div class="toast-message"><%= errorMsg %></div>
                <i class="fa-solid fa-xmark toast-close" onclick="this.parentElement.style.display='none'"></i>
            </div>
        <% } %>
    </div>

    <header>
        <h2>Nuovo Libro</h2>
    </header>
    
    <div class="form-box">
        <form action="AggiungiLibroServlet" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label>Titolo</label>
                <input type="text" name="titolo" required>
            </div>
            <div class="form-group">
                <label>Autore</label>
                <input type="text" name="autore" required>
            </div>
            <div class="form-group">
                <label>Genere</label>
                <input type="text" name="genere" placeholder="Es. Fantasy, Giallo..." required>
            </div>
            <div class="form-group">
                <label>Copie Disponibili</label>
                <input type="number" name="copie" value="1" min="0" required>
            </div>
            <div class="form-group">
                <label>Data Pubblicazione</label>
                <input type="date" name="dataPub" required>
            </div>
            <div class="form-group">
                <label>Carica Copertina (Immagine)</label>
                <input type="file" name="copertina" accept="image/*">
            </div>
            <div class="form-group">
                <label>Descrizione</label>
                <textarea name="descrizione" rows="4"></textarea>
            </div>
            
            <button type="submit" class="btn-submit">Salva Libro</button>
            <a href="CatalogoBibliotecarioServlet" style="display:block; text-align:center; margin-top:15px; text-decoration:none; color:#777;">Annulla</a>
        </form>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", () => {
            const toasts = document.querySelectorAll('.toast');
            if (toasts.length > 0) {
                setTimeout(() => {
                    toasts.forEach(toast => {
                        toast.style.animation = 'fadeOut 1s forwards';
                        setTimeout(() => toast.remove(), 1000);
                    });
                }, 5000);
            }
        });
    </script>
</body>
</html>