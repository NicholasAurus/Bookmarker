<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuova Password</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"><img src="img/logo.png" alt="Logo"></a>
        <nav class="nav-buttons"><a href="login.jsp" class="btn">Login</a></nav>
    </header>

    <main>
        <div class="registration-card">
            <h2>Imposta Password</h2>
            <p style="text-align:center; color:#666;">Identità verificata. Inserisci la nuova password.</p>

            <% String error = (String) request.getAttribute("error"); 
               if(error != null) { %>
                <div class="error-message"><%= error %></div>
            <% } %>

            <form action="RecuperoPasswordServlet" method="post">
                <input type="hidden" name="action" value="resetFinale">
                
                <div class="form-group">
                    <label>Nuova Password</label>
                    <input type="password" name="password" required placeholder="Min 8 caratteri, Maiusc, Num, Simbolo">
                </div>
                
                <div class="form-group">
                    <label>Conferma Password</label>
                    <input type="password" name="conferma_password" required placeholder="Ripeti password">
                </div>

                <button type="submit" class="submit-btn">Cambia Password</button>
            </form>
        </div>
    </main>
</body>
</html>