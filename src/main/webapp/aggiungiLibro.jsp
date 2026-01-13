<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Aggiungi Libro - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
    <style>
        .form-box { max-width: 600px; margin: 40px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        label { display: block; font-weight: bold; margin-bottom: 5px; color: #333; }
        input, textarea, select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; font-size: 1rem; }
        .btn-submit { background-color: #27ae60; color: white; padding: 12px; width: 100%; border: none; border-radius: 4px; cursor: pointer; font-size: 1.1rem; font-weight: bold; }
        .btn-submit:hover { background-color: #219150; }
    </style>
</head>
<body>
    <header style="background-color: rgba(38, 123, 188, 0.79); padding: 20px; text-align: center;">
        <h2 style="color: white; margin: 0;">Nuovo Libro</h2>
    </header>
    
    <div class="form-box">
        <form action="AggiungiLibroServlet" method="post">
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
                <select name="genere">
                    <option value="Fantasy">Fantasy</option>
                    <option value="Horror">Horror</option>
                    <option value="Giallo">Giallo</option>
                    <option value="Romanzo">Romanzo</option>
                    <option value="Fantascienza">Fantascienza</option>
                </select>
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
                <label>URL Immagine Copertina</label>
                <input type="text" name="copertina" placeholder="es: img/mioLibro.jpg">
            </div>
            <div class="form-group">
                <label>Descrizione</label>
                <textarea name="descrizione" rows="4"></textarea>
            </div>
            
            <button type="submit" class="btn-submit">Salva Libro</button>
            <a href="BibliotecarioServlet" style="display:block; text-align:center; margin-top:15px; text-decoration:none; color:#777;">Annulla</a>
        </form>
    </div>
</body>
</html>