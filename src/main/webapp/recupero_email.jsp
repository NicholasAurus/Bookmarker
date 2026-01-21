<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Recupero Password</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
</head>
<body>
    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"><img src="img/logo.png" alt="Logo"></a>
        <nav class="nav-buttons"><a href="login.jsp" class="btn">Login</a></nav>
    </header>

    <main>
        <div class="registration-card">
            <h2>Recupero Password</h2>
            <p style="text-align:center; margin-bottom:20px; color:#666;">Inserisci la tua email per iniziare.</p>

            <% String error = (String) request.getAttribute("error"); 
               if(error != null) { %>
                <div class="error-message"><%= error %></div>
            <% } %>

            <form action="RecuperoPasswordServlet" method="post">
                <input type="hidden" name="action" value="cercaEmail">
                
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" required placeholder="La tua email...">
                </div>

                <button type="submit" class="submit-btn">Avanti</button>
            </form>
            <div class="form-links"><a href="login.jsp">Torna al login</a></div>
        </div>
    </main>
</body>
</html>