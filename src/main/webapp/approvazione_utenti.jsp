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
    
    <link rel="stylesheet" href="css/approvazione_utenti.css">
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
        let formDaInviareId = null; 

        function apriModal(tipo, nomeUtente, formId) {
            const modal = document.getElementById('confirmationModal');
            const titolo = document.getElementById('modalTitle');
            const testo = document.getElementById('modalText');
            const icona = document.getElementById('modalIcon');
            const btnConferma = document.getElementById('confirmBtn');

            formDaInviareId = formId;

            if (tipo === 'accetta') {
                titolo.innerText = "Accetta Utente";
                testo.innerText = "Sei sicuro di voler attivare l'account di " + nomeUtente + "?";
                icona.innerHTML = '<i class="fa-solid fa-user-check" style="color: #27ae60;"></i>';
                btnConferma.style.backgroundColor = "#27ae60"; 
                btnConferma.innerText = "Sì, Attiva";
            } else {
                titolo.innerText = "Rifiuta Utente";
                testo.innerText = "Attenzione: Stai per rifiutare l'iscrizione di " + nomeUtente + ".";
                icona.innerHTML = '<i class="fa-solid fa-triangle-exclamation" style="color: #c0392b;"></i>';
                btnConferma.style.backgroundColor = "#c0392b"; 
                btnConferma.innerText = "Sì, Rifiuta";
            }

            modal.style.display = 'flex'; 
        }

        function chiudiModal() {
            document.getElementById('confirmationModal').style.display = 'none';
            formDaInviareId = null;
        }

        document.getElementById('confirmBtn').addEventListener('click', function() {
            if (formDaInviareId) {
                document.getElementById(formDaInviareId).submit();
            }
        });

        window.onclick = function(event) {
            const modal = document.getElementById('confirmationModal');
            if (event.target == modal) {
                chiudiModal();
            }
        }
    </script>

</body>
</html>