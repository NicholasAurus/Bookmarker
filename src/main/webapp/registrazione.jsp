<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione</title>
    
    <link rel="stylesheet" href="css/style.css">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <style>
        .error-message {
            color: #D8000C;
            background-color: #FFD2D2;
            border: 1px solid #D8000C;
            padding: 10px;
            border-radius: 5px;
            text-align: center;
            margin-bottom: 15px;
            font-weight: bold;
        }
    </style>
</head>
<body>

<header>
    <div class="header-spacer"></div>
    <a href="index.jsp" class="logo-container"> 
        <img src="img/logo.png" alt="BookMarker Logo - Home">
    </a>
    <nav class="nav-buttons">
        <a href="login.jsp" class="btn">Login</a>
    </nav>
</header>

    <main>
        <div class="registration-card">
            <h2>Registrati</h2>
            
            <%-- Blocco per mostrare messaggi di errore dalla Servlet --%>
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
            %>
                <div class="error-message"><%= errorMessage %></div>
            <% 
                } 
            %>
            
            <form action="RegistrationServlet" method="POST"> 
                <div class="form-group">
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" placeholder="Nome" required>
                </div>
                <div class="form-group">
                    <label for="cognome">Cognome</label>
                    <input type="text" id="cognome" name="cognome" placeholder="Cognome" required>
                </div>
                <div class="form-group">
                    <label for="tessera">N. Tessera</label>
                    <input type="text" id="tessera" name="tessera" placeholder="000000" required>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="example@mail.com" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="password" required>
                </div>
                <div class="form-group">
                    <label for="confirm-password">Conferma password</label>
                    <input type="password" id="confirm-password" name="confirm-password" placeholder="password" required>
                </div>
                <button type="submit" class="submit-btn">Registrati</button>
            </form>
            
            <div class="form-links">
                <a href="login.jsp">Hai già un account? Accedi</a>
            </div>
        </div>
    </main>

</body>
</html>