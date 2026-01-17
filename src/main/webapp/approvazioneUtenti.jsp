<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String nomeUtente = (String) session.getAttribute("utenteLoggato");
    boolean isLoggato = (nomeUtente != null);
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Utenti - BookMarker</title>
    
    <link rel="stylesheet" href="css/catalogo.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="css/approvazioneUtenti.css">


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
                <h2 class="section-title">Gestione Utenti</h2>
            </div>
        </section>

        <div class="admin-container">
            
            <div class="tab-container">
                <button class="tab-button ${activeTab == 'utenti' ? 'active' : ''}" onclick="cambiaTab('utenti')">
                    <i class="fa-solid fa-users"></i> Registrazioni Utenti
                </button>
                <button class="tab-button ${activeTab == 'prestiti' ? 'active' : ''}" onclick="cambiaTab('prestiti')">
                    <i class="fa-solid fa-book-bookmark"></i> Richieste Prestiti
                </button>
            </div>

            <div id="tabUtenti" class="tab-content ${activeTab == 'utenti' ? 'active' : ''}">
                <h3 class="section-header">Registrazioni in Attesa</h3>
                
                <c:if test="${empty listaUtenti}">
                    <div class="empty-msg">Nessuna richiesta di registrazione in sospeso.</div>
                </c:if>

                <c:if test="${not empty listaUtenti}">
                    <table class="styled-table">
                        <thead>
                            <tr>
                                <th>Data Reg.</th>
                                <th>Nome</th>
                                <th>Email</th>
                                <th>Codice Fiscale</th>
                                <th style="text-align: center;">Azioni</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listaUtenti}" var="u" varStatus="loop">
                                <tr>
                                    <td>${u.dataRegistrazione}</td>
                                    <td>${u.nome} ${u.cognome}</td>
                                    <td>${u.email}</td>
                                    <td><c:out value="${u.codiceFiscale}" default="-" /></td>
                                    <td style="text-align: center; white-space: nowrap;">
                                        <form id="form-user-accetta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="tipoOperazione" value="utente">
                                            <input type="hidden" name="emailUtente" value="${u.email}">
                                            <input type="hidden" name="azione" value="accetta">
                                        </form>
                                        <form id="form-user-rifiuta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="tipoOperazione" value="utente">
                                            <input type="hidden" name="emailUtente" value="${u.email}">
                                            <input type="hidden" name="azione" value="rifiuta">
                                        </form>
                                        <button type="button" class="btn-action btn-green" onclick="apriModal('utente', 'accetta', '${u.nome}', 'form-user-accetta-${loop.index}')"><i class="fa-solid fa-check"></i> Accetta</button>
                                        <button type="button" class="btn-action btn-red" onclick="apriModal('utente', 'rifiuta', '${u.nome}', 'form-user-rifiuta-${loop.index}')"><i class="fa-solid fa-trash"></i> Rifiuta</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

            <div id="tabPrestiti" class="tab-content ${activeTab == 'prestiti' ? 'active' : ''}">
                <h3 class="section-header">Richieste Prestiti</h3>

                <c:if test="${empty listaPrestiti}">
                    <div class="empty-msg">Nessuna richiesta di prestito da approvare.</div>
                </c:if>

                <c:if test="${not empty listaPrestiti}">
                    <table class="styled-table">
                        <thead>
                            <tr>
                                <th>Email Utente</th>
                                <th>Libro</th>
                                <th>Data Ritiro Richiesta</th>
                                <th style="text-align: center;">Azioni</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listaPrestiti}" var="p" varStatus="loop">
                                <tr>
                                    <td>${p.utenteEmail}</td>
                                    <td>${p.titoloLibro}</td>
                                    <td style="font-weight:bold; color:#2c3e50;">
                                        ${p.dataPrenotazione}
                                    </td>
                                    <td style="text-align: center; white-space: nowrap;">
                                        <form id="form-loan-accetta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="tipoOperazione" value="prestito">
                                            <input type="hidden" name="idPrestito" value="${p.id}">
                                            <input type="hidden" name="azione" value="accetta">
                                        </form>
                                        <form id="form-loan-rifiuta-${loop.index}" action="GestioneUtentiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="tipoOperazione" value="prestito">
                                            <input type="hidden" name="idPrestito" value="${p.id}">
                                            <input type="hidden" name="azione" value="rifiuta">
                                        </form>
                                        <button type="button" class="btn-action btn-green" onclick="apriModal('prestito', 'accetta', 'libro #${p.libroId} per ${p.utenteEmail}', 'form-loan-accetta-${loop.index}')"><i class="fa-solid fa-check"></i> Conferma</button>
                                        <button type="button" class="btn-action btn-red" onclick="apriModal('prestito', 'rifiuta', 'libro #${p.libroId} per ${p.utenteEmail}', 'form-loan-rifiuta-${loop.index}')"><i class="fa-solid fa-xmark"></i> Rifiuta</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

        </div>
    </main>

    <div id="confirmationModal" class="modal-overlay">
        <div class="modal-box">
            <div id="modalIcon" class="modal-icon"></div>
            <h3 id="modalTitle" class="modal-title">Titolo</h3>
            <p id="modalText" class="modal-text">Messaggio di conferma</p>
            <div class="modal-buttons">
                <button class="btn-modal btn-cancel" onclick="chiudiModal('confirmationModal')">Annulla</button>
                <button id="confirmBtn" class="btn-modal btn-confirm">Conferma</button>
            </div>
        </div>
    </div>

    <div id="errorModal" class="modal-overlay">
        <div class="modal-box">
            <div class="modal-icon"><i class="fa-solid fa-circle-xmark" style="color: #c0392b;"></i></div>
            <h3 class="modal-title">Errore</h3>
            <p id="errorText" class="modal-text">Si è verificato un errore.</p>
            <div class="modal-buttons" style="justify-content: center;">
                <button class="btn-modal btn-cancel" onclick="chiudiModal('errorModal')">Chiudi</button>
            </div>
        </div>
    </div>

    <script>
        function cambiaTab(tabName) {
            const url = new URL(window.location);
            url.searchParams.set('tab', tabName);
            window.history.pushState({}, '', url);

            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active', 'active-show'));
            document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));

            document.getElementById('tab' + capitalizeFirstLetter(tabName)).classList.add('active');
            event.currentTarget.classList.add('active');
        }

        function capitalizeFirstLetter(string) {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }

        function initializeTabs() {
            const activeTab = "${activeTab}";
            if(activeTab === 'prestiti') {
                 document.getElementById('tabPrestiti').style.display = 'block';
                 document.getElementById('tabUtenti').style.display = 'none';
            } else {
                 document.getElementById('tabUtenti').style.display = 'block';
                 document.getElementById('tabPrestiti').style.display = 'none';
            }
        }

        let formDaInviareId = null; 
        
        function apriModal(tipo, azione, infoOggetto, formId) {
            const modal = document.getElementById('confirmationModal');
            const titolo = document.getElementById('modalTitle');
            const testo = document.getElementById('modalText');
            const icona = document.getElementById('modalIcon');
            const btnConferma = document.getElementById('confirmBtn');
            formDaInviareId = formId;

            if (azione === 'accetta') {
                icona.innerHTML = '<i class="fa-solid fa-circle-check" style="color: #27ae60;"></i>';
                btnConferma.style.backgroundColor = "#27ae60"; 
                btnConferma.innerText = "Sì, Conferma";
            } else {
                icona.innerHTML = '<i class="fa-solid fa-triangle-exclamation" style="color: #c0392b;"></i>';
                btnConferma.style.backgroundColor = "#c0392b"; 
                btnConferma.innerText = "Sì, Rifiuta";
            }

            if (tipo === 'utente') {
                titolo.innerText = (azione === 'accetta') ? "Attiva Account" : "Rifiuta Iscrizione";
                testo.innerText = (azione === 'accetta') ? "Vuoi attivare l'account di " + infoOggetto + "?" : "Vuoi rifiutare la registrazione di " + infoOggetto + "?";
            } else {
                titolo.innerText = (azione === 'accetta') ? "Approva Prestito" : "Rifiuta Prestito";
                testo.innerText = (azione === 'accetta') ? "Vuoi confermare il prestito del " + infoOggetto + "?" : "Vuoi rifiutare la richiesta del " + infoOggetto + "?";
            }
            modal.style.display = 'flex'; 
        }

        function apriErrorModal(messaggio) {
            const modal = document.getElementById('errorModal');
            const testo = document.getElementById('errorText');
            testo.innerText = messaggio;
            modal.style.display = 'flex';
        }

        function chiudiModal(modalId) { 
            document.getElementById(modalId).style.display = 'none'; 
            if(modalId === 'confirmationModal') formDaInviareId = null; 
        }

        document.getElementById('confirmBtn').addEventListener('click', function() { 
            if (formDaInviareId) document.getElementById(formDaInviareId).submit(); 
        });
        
        window.onclick = function(event) { 
            if (event.target == document.getElementById('confirmationModal')) chiudiModal('confirmationModal');
            if (event.target == document.getElementById('errorModal')) chiudiModal('errorModal');
        }

        <c:if test="${not empty errore}">
            window.addEventListener('load', function() {
                apriErrorModal("${errore}");
            });
        </c:if>

        // Gestione cambio tab visivo senza ricaricare, ma aggiornando URL
        function cambiaTab(tabName) {
            // Aggiorna URL senza ricaricare
            const url = new URL(window.location);
            url.searchParams.set('tab', tabName);
            window.history.pushState({}, '', url);

            // Nascondi tutto
            document.getElementById('tabUtenti').style.display = 'none';
            document.getElementById('tabPrestiti').style.display = 'none';
            
            // Rimuovi active dai bottoni
            const buttons = document.getElementsByClassName('tab-button');
            for(let btn of buttons) btn.classList.remove('active');

            // Mostra tab corretta
            if(tabName === 'prestiti') {
                document.getElementById('tabPrestiti').style.display = 'block';
                document.getElementById('tabPrestiti').classList.add('active');
            } else {
                document.getElementById('tabUtenti').style.display = 'block';
                document.getElementById('tabUtenti').classList.add('active');
            }
            
            // Attiva bottone cliccato
            event.currentTarget.classList.add('active');
        }
    </script>
</body>
</html>