// To save as "ebookshop/WEB-INF/classes/EshopQueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshopquery")
public class EshopQueryServlet extends HttpServlet {

   private static String escapeHtml(String input) {
      if (input == null) {
         return "";
      }
      return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Order Form</title></head>");
      out.println("<body>");
      out.println("<div style='display: flex; justify-content: space-between; margin-bottom: 16px;'>");
      out.println("<a href='eshophome'>Back to Home</a>");
      out.println("<a href='adminlogin'>Admin Login</a>");
      out.println("</div>");
      out.println("<h2>Yet Another e-Bookshop</h2>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         Statement stmt = conn.createStatement();
      ) {
         String[] authors = request.getParameterValues("author");
         String sortOrder = request.getParameter("sort");
         if (authors == null) {
            out.println("<h3>No author selected. Please go back to select author(s).</h3>");
            out.println("</body></html>");
            return;
         }
         if (!"desc".equalsIgnoreCase(sortOrder)) {
            sortOrder = "asc";
         } else {
            sortOrder = "desc";
         }

         String sqlStr = "SELECT * FROM books WHERE author IN (";
         for (int i = 0; i < authors.length; ++i) {
            if (i < authors.length - 1) {
               sqlStr += "'" + authors[i] + "', ";
            } else {
               sqlStr += "'" + authors[i] + "'";
            }
         }
         sqlStr += ") AND qty > 0 ORDER BY price " + sortOrder.toUpperCase()
               + ", author ASC, title ASC";

         out.println("<p>Your SQL statement is: " + escapeHtml(sqlStr) + "</p>");
         ResultSet rset = stmt.executeQuery(sqlStr);

         out.println("<form method='post' action='eshoporder'>");
         out.println("<table border='1' cellpadding='6' cellspacing='0'>");
         out.println("<tr>");
         out.println("<th>Select</th>");
         out.println("<th>Cover</th>");
         out.println("<th>Book ID</th>");
         out.println("<th>Author</th>");
         out.println("<th>Title</th>");
         out.println("<th>Price</th>");
         out.println("</tr>");
         int count = 0;
         while (rset.next()) {
            out.println("<tr>");
            out.println("<td><input type='checkbox' name='id' value='"
                  + escapeHtml(rset.getString("id")) + "' /></td>");
            out.println("<td><img src='" + escapeHtml(rset.getString("image_path"))
                  + "' alt='" + escapeHtml(rset.getString("title"))
                  + "' width='72' height='96' /></td>");
            out.println("<td>" + escapeHtml(rset.getString("id")) + "</td>");
            out.println("<td>" + escapeHtml(rset.getString("author")) + "</td>");
            out.println("<td>" + escapeHtml(rset.getString("title")) + "</td>");
            out.println("<td>$" + escapeHtml(rset.getString("price")) + "</td>");
            out.println("</tr>");
            count++;
         }
         out.println("</table>");
         out.println("<br />");
         out.println("<fieldset>");
         out.println("<legend>Customer Details</legend>");
         out.println("<p>Name: <input type='text' name='cust_name' required /></p>");
         out.println("<p>Email: <input type='email' name='cust_email' required /></p>");
         out.println("<p>Phone: <input type='text' name='cust_phone' required /></p>");
         out.println("</fieldset>");
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
