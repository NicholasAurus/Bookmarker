<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% String nomeUtente = (String) session.getAttribute("utenteLoggato"); %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Prestiti - BookMarker</title>
    <link rel="stylesheet" href="css/catalogo.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="css/gestionePrestiti.css">

</head>
<body>

    <header>
        <div class="header-spacer"></div>
        <a href="index.jsp" class="logo-container"> 
            <img src="img/logo.png" alt="BookMarker Logo">
        </a>
        <nav class="nav-buttons">
            <span class="user-greeting">Area Bibliotecario</span>
            <a href="logout.jsp" class="btn" style="background-color: #c0392b; color: white;">Logout</a>
        </nav>
    </header>

    <main>
        <section class="blue-bar">
            <div class="container-inner">
                <h2 class="section-title">Gestione Prestiti Globali</h2>
            </div>
        </section>

        <div class="admin-container">
            
            <div class="tab-container">
                <button class="tab-button ${activeTab == 'prenotati' ? 'active' : ''}" onclick="cambiaTab('prenotati')">
                    <i class="fa-solid fa-hand-holding-hand"></i> Da Ritirare
                </button>
                <button class="tab-button ${activeTab == 'attivi' ? 'active' : ''}" onclick="cambiaTab('attivi')">
                    <i class="fa-solid fa-book-open-reader"></i> In Corso
                </button>
                <button class="tab-button ${activeTab == 'restituiti' ? 'active' : ''}" onclick="cambiaTab('restituiti')">
                    <i class="fa-solid fa-clock-rotate-left"></i> Storico Restituzioni
                </button>
            </div>

            <div id="tabPrenotati" class="tab-content ${activeTab == 'prenotati' ? 'active' : ''}">
                <h3 class="section-header">Libri in attesa di ritiro</h3>
                <c:if test="${empty listaPrenotati}">
                    <div class="empty-msg">Nessuna prenotazione in attesa.</div>
                </c:if>
                <c:if test="${not empty listaPrenotati}">
                    <table class="styled-table table-orange">
                        <thead>
                            <tr>
                                <th>Utente</th>
                                <th>Libro</th>
                                <th>Scadenza Prenotazione</th>
                                <th style="text-align: center;">Azioni</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listaPrenotati}" var="p" varStatus="loop">
                                <tr>
                                    <td>${p.utenteEmail}</td>
                                    <td>${p.titoloLibro}</td>
                                    
                                    <td style="font-weight: bold; color: #d35400;">
                                        ${p.dataPrenotazione}
                                    </td>
                                    
                                    <td style="text-align: center; white-space: nowrap;">
                                        <form id="form-ritiro-${loop.index}" action="GestionePrestitiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="idPrestito" value="${p.id}">
                                            <input type="hidden" name="azione" value="ritiro">
                                        </form>
                                        <form id="form-annulla-${loop.index}" action="GestionePrestitiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="idPrestito" value="${p.id}">
                                            <input type="hidden" name="azione" value="annulla">
                                        </form>
                                        <button type="button" class="btn-action btn-green" onclick="apriModal('ritiro', '${p.utenteEmail}', 'form-ritiro-${loop.index}')"><i class="fa-solid fa-check"></i> Ritiro</button>
                                        <button type="button" class="btn-action btn-red" onclick="apriModal('annulla', '${p.utenteEmail}', 'form-annulla-${loop.index}')"><i class="fa-solid fa-ban"></i> Annulla</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

            <div id="tabAttivi" class="tab-content ${activeTab == 'attivi' ? 'active' : ''}">
                <h3 class="section-header">Prestiti Attivi (Libri consegnati)</h3>
                <c:if test="${empty listaAttivi}">
                    <div class="empty-msg">Nessun prestito attivo al momento.</div>
                </c:if>
                <c:if test="${not empty listaAttivi}">
                    <table class="styled-table">
                        <thead>
                            <tr>
                                <th>Utente</th>
                                <th>Libro</th>
                                <th>Data Ritiro</th>
                                <th>Da Restituire Entro</th>
                                <th style="text-align: center;">Azioni</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listaAttivi}" var="p" varStatus="loop">
                                <tr>
                                    <td>${p.utenteEmail}</td>
                                    <td>${p.titoloLibro}</td>
                                    <td>${p.dataInizio}</td>
                                    <td style="font-weight:bold; color: #c0392b;">${p.dataFinePrevista}</td>
                                    <td style="text-align: center; white-space: nowrap;">
                                        <form id="form-rest-${loop.index}" action="GestionePrestitiServlet" method="post" style="display:none;">
                                            <input type="hidden" name="idPrestito" value="${p.id}">
                                            <input type="hidden" name="azione" value="restituzione">
                                        </form>
                                        <button type="button" class="btn-action btn-blue" onclick="apriModal('restituzione', '${p.utenteEmail}', 'form-rest-${loop.index}')"><i class="fa-solid fa-rotate-left"></i> Restituzione</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

            <div id="tabRestituiti" class="tab-content ${activeTab == 'restituiti' ? 'active' : ''}">
                <h3 class="section-header">Storico Restituzioni</h3>
                <c:if test="${empty listaRestituiti}">
                    <div class="empty-msg">Nessun prestito concluso nello storico.</div>
                </c:if>
                <c:if test="${not empty listaRestituiti}">
                    <table class="styled-table table-gray">
                        <thead>
                            <tr>
                                <th>Utente</th>
                                <th>Libro</th>
                                <th>Data Inizio</th>
                                <th>Restituito il</th>
                                <th style="text-align: center;">Stato</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listaRestituiti}" var="p">
                                <tr>
                                    <td>${p.utenteEmail}</td>
                                    <td>${p.titoloLibro}</td>
                                    <td>${p.dataInizio}</td>
                                    <td style="font-weight:bold;">${p.dataRestituzioneEffettiva}</td>
                                    <td style="text-align: center;">
                                        <span style="background-color:#7f8c8d; color:white; padding:4px 8px; border-radius:4px; font-size:0.8rem;">CONCLUSO</span>
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
            <p id="modalText" class="modal-text">Testo</p>
            <div class="modal-buttons">
                <button class="btn-modal btn-cancel" onclick="chiudiModal()">Chiudi</button>
                <button id="confirmBtn" class="btn-modal btn-confirm">Conferma</button>
            </div>
        </div>
    </div>

    <script>
        function cambiaTab(tabName) {
            const url = new URL(window.location);
            url.searchParams.set('tab', tabName);
            window.history.pushState({}, '', url);

            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-button').forEach(el => el.classList.remove('active'));

            document.getElementById('tab' + capitalizeFirstLetter(tabName)).classList.add('active');
            event.currentTarget.classList.add('active');
        }

        function capitalizeFirstLetter(string) {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }

        let formDaInviareId = null; 

        function apriModal(azione, info, formId) {
            const modal = document.getElementById('confirmationModal');
            const titolo = document.getElementById('modalTitle');
            const testo = document.getElementById('modalText');
            const icona = document.getElementById('modalIcon');
            const btnConferma = document.getElementById('confirmBtn');

            formDaInviareId = formId;

            if (azione === 'ritiro') {
                titolo.innerText = "Conferma Ritiro";
                testo.innerText = "L'utente " + info + " ha ritirato il libro? Il prestito diventerà ATTIVO.";
                icona.innerHTML = '<i class="fa-solid fa-handshake" style="color: #27ae60;"></i>';
                btnConferma.style.backgroundColor = "#27ae60"; 
                btnConferma.innerText = "Sì, Conferma";
            } else if (azione === 'annulla') {
                titolo.innerText = "Annulla Prenotazione";
                testo.innerText = "Annullare la prenotazione di " + info + "?";
                icona.innerHTML = '<i class="fa-solid fa-ban" style="color: #c0392b;"></i>';
                btnConferma.style.backgroundColor = "#c0392b"; 
                btnConferma.innerText = "Sì, Annulla";
            } else if (azione === 'restituzione') {
                titolo.innerText = "Conferma Restituzione";
                testo.innerText = "L'utente " + info + " ha riportato il libro?";
                icona.innerHTML = '<i class="fa-solid fa-book-open" style="color: #3498db;"></i>';
                btnConferma.style.backgroundColor = "#3498db"; 
                btnConferma.innerText = "Sì, Restituito";
            }
            modal.style.display = 'flex'; 
        }

        function chiudiModal() { document.getElementById('confirmationModal').style.display = 'none'; }
        document.getElementById('confirmBtn').addEventListener('click', function() { if (formDaInviareId) document.getElementById(formDaInviareId).submit(); });
        window.onclick = function(event) { if (event.target == document.getElementById('confirmationModal')) chiudiModal(); }
    </script>
</body>
</html>