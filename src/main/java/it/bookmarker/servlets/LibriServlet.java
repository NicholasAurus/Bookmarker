package it.bookmarker.servlets;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LibriServlet
 */
@WebServlet("/LibriServlet")
public class LibriServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LibriServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		out.println("<h2>Catalogo libri disponibili</h2>");
		out.println("<ul>");
		
		try {
			
			
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/biblioteca?serverTimezone=UTC",
					"root", "Bookmarker09!"
					);
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT titolo, autore, genere FROM libri WHERE disponibilita = TRUE"
					);
			
			while(rs.next()) {
				String titolo = rs.getString("titolo");
				String autore = rs.getString("autore");
				String genere = rs.getString("genere");
				out.println("<li>" + titolo + "-" + autore + "-" + genere + "</li>");
			}
			
			out.println("</ul>");
			
			conn.close();
			
			} catch (Exception e) {
				out.println("<p style='color:red'>Errore DB: " + e.getMessage() + "</p>");
			
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
