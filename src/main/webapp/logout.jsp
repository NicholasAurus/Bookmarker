
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // distrugge la sessione corrente 
    session.invalidate();
    
    // torna alla pagina principale
    response.sendRedirect("index.jsp");
%>