<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - BookMarker</title>
    
    <link rel="stylesheet" href="css/style.css">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    
</head>
<body>

    <header>
        <div class="header-spacer"></div>

        <a href="index.jsp" class="logo-container">
            <img src="img/logo.png" alt="BookMarker Logo - Home">
        </a>

        <nav class="nav-buttons">
            <a href="registrazione.jsp" class="btn">Registrati</a>
        </nav>
    </header>

    <main>
        <div class="registration-card" id="login-card">
            <h2>Login</h2>
            
         
            <% 
                String regSuccess = request.getParameter("reg");
                if ("success".equals(regSuccess)) {
            %>
                <div class="success-message">Richiesta di registrazione inviata! In attesa di approvazione.</div>
            <% 
                } 
            %>

     
            <% 
                String msg = request.getParameter("msg");
                if ("resetSuccess".equals(msg)) {
            %>
                <div class="success-message">Password reimpostata con successo! Accedi ora.</div>
            <% 
                } 
            %>

         
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
            %>
                <div class="error-message"><%= errorMessage %></div>
            <% 
                } 
            %>
            
            <form action="LoginServlet" method="POST">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="example@mail.com" required>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="password" required>
                </div>

                <div style="text-align: right; margin-top: 5px; margin-bottom: 15px;">
                    <a href="RecuperoPasswordServlet" style="color: #267bbc; font-size: 0.9em; text-decoration: none;">
                        Hai dimenticato la password?
                    </a>
                </div>

                <button type="submit" class="submit-btn">Login</button>
            </form>
            
            <div class="form-links">
                <a href="registrazione.jsp">Non hai un account? Registrati</a>
            </div>
            
        </div>
    </main>

</body>
</html>