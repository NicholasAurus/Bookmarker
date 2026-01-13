<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<%
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    boolean isLoggato = (nomeUtente != null);
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Utenti - BookMarker</title>
    
    <link rel="stylesheet" href="css/catalogo.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        /* --- STILI ESISTENTI (Tabella e Layout) --- */
        .admin-container {
            max-width: 1100px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .styled-table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
            font-size: 0.95em;
            font-family: sans-serif;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
            border-radius: 5px 5px 0 0;
            overflow: hidden;
        }

        .styled-table thead tr {
            background-color: #267bbc; 
            color: #ffffff;
            text-align: left;
        }

        .styled-table th, .styled-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #dddddd;
        }

        .styled-table tbody tr:nth-of-type(even) { background-color: #f3f3f3; }
        .styled-table tbody tr:hover { background-color: #eaf6ff; }
        .styled-table tbody tr:last-of-type { border-bottom: 2px solid #267bbc; }

        .btn-action {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            font-weight: bold;
            color: white;
            text-transform: uppercase;
            transition: opacity 0.3s ease;
        }
        .btn-action:hover { opacity: 0.8; }
        .btn-green { background-color: #27ae60; }
        .btn-red { background-color: #c0392b; margin-left: 5px; }
        
        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 1.1rem;
        }

        /* --- NUOVI STILI PER LA MODALE (POPUP) --- */
        .modal-overlay {
            display: none; /* Nascosto di default */
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5); /* Sfondo scuro semitrasparente */
            z-index: 1000;
            justify-content: center;
            align-items: center;
            animation: fadeIn 0.3s;
        }

        .modal-box {
            background: white;
            padding: 30px;
            border-radius: 8px;
            width: 400px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            animation: slideUp 0.3s;
        }

        .modal-icon {
            font-size: 3rem;
            margin-bottom: 15px;
        }

        .modal-title {
            font-size: 1.2rem;
            margin-bottom: 10px;
            color: #333;
        }

        .modal-text {
            color: #666;
            margin-bottom: 25px;
        }

        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
        }

        .btn-modal {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            font-size: 14px;
        }

        .btn-cancel { background-color: #e0e0e0; color: #333; }
        .btn-confirm { background-color: #267bbc; color: white; }

        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes slideUp { from { transform: translateY(20px); } to { transform: translateY(0); } }
    </style>
</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        
        <nav class="nav-buttons">
            <% if (isLoggato) { %>
                <span class="user-greeting">Ciao, <b><%= nomeUtente %></b></span>
                <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
            <% } else { %>
                <a href="login.jsp" class="btn">Login</a>
            <% } %>
        </nav>
    </header>

    <main>
        <section class="blue-bar">
            <div class="container-inner">
                <h2 class="section-title">Amministrazione: Approvazione Utenti</h2>
                <a href="index.jsp" style="color: white; text-decoration: underline; font-size: 0.9rem;">
                    <i class="fa-solid fa-arrow-left"></i> Torna alla Home
                </a>
            </div>
        </section>

        <div class="admin-container">
            
            <c:if test="${empty listaUtenti}">
                <div class="empty-msg">
                    <i class="fa-regular fa-folder-open" style="font-size: 3rem; color: #ccc; margin-bottom: 10px; display:block;"></i>
                    Nessuna richiesta di registrazione in sospeso al momento.
                </div>
            </c:if>

            <c:if test="${not empty listaUtenti}">
                <table class="styled-table">
                    <thead>
                        <tr>
                            <th>Data Reg.</th>
                            <th>Nome</th>
                            <th>Cognome</th>
                            <th>Email</th>
                            <th>Codice Fiscale</th>
                            <th style="text-align: center;">Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${listaUtenti}" var="u" varStatus="loop">
                            <tr>
                                <td>${u.dataRegistrazione}</td>
                                <td>${u.nome}</td>
                                <td>${u.cognome}</td>
                                <td>${u.email}</td>
                                <td><c:out value="${u.codiceFiscale}" default="-" /></td>
                                <td style="text-align: center; white-space: nowrap;">
                                    
                                    <form id="form-accetta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                        <input type="hidden" name="emailUtente" value="${u.email}">
                                        <input type="hidden" name="azione" value="accetta">
                                    </form>

                                    <form id="form-rifiuta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                        <input type="hidden" name="emailUtente" value="${u.email}">
                                        <input type="hidden" name="azione" value="rifiuta">
                                    </form>

                                    <button type="button" class="btn-action btn-green" 
                                            onclick="apriModal('accetta', '${u.nome} ${u.cognome}', 'form-accetta-${loop.index}')">
                                        <i class="fa-solid fa-check"></i> Accetta
                                    </button>

                                    <button type="button" class="btn-action btn-red" 
                                            onclick="apriModal('rifiuta', '${u.nome} ${u.cognome}', 'form-rifiuta-${loop.index}')">
                                        <i class="fa-solid fa-trash"></i> Rifiuta
                                    </button>

                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </main>

    <div id="confirmationModal" class="modal-overlay">
        <div class="modal-box">
            <div id="modalIcon" class="modal-icon"></div>
            <h3 id="modalTitle" class="modal-title">Titolo</h3>
            <p id="modalText" class="modal-text">Messaggio di conferma</p>
            
            <div class="modal-buttons">
                <button class="btn-modal btn-cancel" onclick="chiudiModal()">Annulla</button>
                <button id="confirmBtn" class="btn-modal btn-confirm">Conferma</button>
            </div>
        </div>
    </div>

    <script>
        let formDaInviareId = null; // Variabile per ricordarsi quale form inviare

        function apriModal(tipo, nomeUtente, formId) {
            const modal = document.getElementById('confirmationModal');
            const titolo = document.getElementById('modalTitle');
            const testo = document.getElementById('modalText');
            const icona = document.getElementById('modalIcon');
            const btnConferma = document.getElementById('confirmBtn');

            // Salviamo l'ID del form che dobbiamo inviare se l'utente dice SI
            formDaInviareId = formId;

            if (tipo === 'accetta') {
                titolo.innerText = "Accetta Utente";
                testo.innerText = "Sei sicuro di voler attivare l'account di " + nomeUtente + "?";
                icona.innerHTML = '<i class="fa-solid fa-user-check" style="color: #27ae60;"></i>';
                btnConferma.style.backgroundColor = "#27ae60"; // Verde
                btnConferma.innerText = "Sì, Attiva";
            } else {
                titolo.innerText = "Rifiuta Utente";
                testo.innerText = "Attenzione: Stai per rifiutare l'iscrizione di " + nomeUtente + ".";
                icona.innerHTML = '<i class="fa-solid fa-triangle-exclamation" style="color: #c0392b;"></i>';
                btnConferma.style.backgroundColor = "#c0392b"; // Rosso
                btnConferma.innerText = "Sì, Rifiuta";
            }

            modal.style.display = 'flex'; // Mostra la modale
        }

        function chiudiModal() {
            document.getElementById('confirmationModal').style.display = 'none';
            formDaInviareId = null;
        }

        // Quando si clicca "Conferma" dentro la modale
        document.getElementById('confirmBtn').addEventListener('click', function() {
            if (formDaInviareId) {
                document.getElementById(formDaInviareId).submit();
            }
        });

        // Chiude la modale se clicchi fuori dal box
        window.onclick = function(event) {
            const modal = document.getElementById('confirmationModal');
            if (event.target == modal) {
                chiudiModal();
            }
        }
    </script>

</body>
</html>