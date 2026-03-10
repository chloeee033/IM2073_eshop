// To save as "ebookshop/WEB-INF/classes/EshopQueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshopquery")
public class EshopQueryServlet extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Order Form</title></head>");
      out.println("<body>");
      out.println("<h2>Yet Another e-Bookshop</h2>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         Statement stmt = conn.createStatement();
      ) {
         String[] authors = request.getParameterValues("author");
         if (authors == null) {
            out.println("<h3>No author selected. Please go back to select author(s).</h3>");
            out.println("</body></html>");
            return;
         }

         String sqlStr = "SELECT * FROM books WHERE author IN (";
         for (int i = 0; i < authors.length; ++i) {
            if (i < authors.length - 1) {
               sqlStr += "'" + authors[i] + "', ";
            } else {
               sqlStr += "'" + authors[i] + "'";
            }
         }
         sqlStr += ") AND qty > 0 ORDER BY author ASC, title ASC";

         out.println("<p>Your SQL statement is: " + sqlStr + "</p>");
         ResultSet rset = stmt.executeQuery(sqlStr);

         out.println("<form method='get' action='eshoporder'>");
         int count = 0;
         while (rset.next()) {
            out.println("<p><input type='checkbox' name='id' value='"
                  + rset.getString("id") + "' />"
                  + rset.getString("author") + ", "
                  + rset.getString("title") + ", $"
                  + rset.getString("price") + "</p>");
            count++;
         }
         out.println("<p><input type='submit' value='ORDER' /></p>");
         out.println("</form>");
         out.println("<p>==== " + count + " records found =====</p>");
      } catch (SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      out.println("</body></html>");
      out.close();
   }
}
