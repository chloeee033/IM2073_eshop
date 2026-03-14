import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshophome")
public class EshopHomeServlet extends HttpServlet {

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
      out.println("<head><title>Yet Another e-Bookshop</title></head>");
      out.println("<body style='font-family: Georgia, serif; margin: 24px; background: #f6f1e7;'>");
      out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>");
      out.println("<div>");
      out.println("<h1 style='margin: 0; color: #3b2f2f;'>Yet Another e-Bookshop</h1>");
      out.println("<p style='margin: 6px 0 0; color: #6b5d54;'>Browse every title first, then filter the buying list.</p>");
      out.println("</div>");
      out.println("<a href='adminlogin' style='text-decoration: none;'>");
      out.println("<button type='button' style='padding: 10px 16px; border: 1px solid #6e4b3a; background: #fff4df; cursor: pointer;'>Admin Login</button>");
      out.println("</a>");
      out.println("</div>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         PreparedStatement authorStmt =
               conn.prepareStatement("SELECT DISTINCT author FROM books ORDER BY author ASC");
         PreparedStatement bookStmt =
               conn.prepareStatement("SELECT * FROM books ORDER BY author ASC, title ASC");
      ) {
         out.println("<form method='get' action='eshopquery' style='background: #fffaf0; border: 1px solid #d4c2aa; padding: 16px; margin-bottom: 24px;'>");
         out.println("<h3 style='margin-top: 0;'>Filter Purchase List</h3>");
         out.println("<div style='margin-bottom: 12px;'>");
         out.println("<strong>Choose author:</strong><br /><br />");
         try (ResultSet authorRset = authorStmt.executeQuery()) {
            while (authorRset.next()) {
               String author = authorRset.getString("author");
               out.println("<label style='display: inline-block; margin-right: 12px; margin-bottom: 8px;'>");
               out.println("<input type='checkbox' name='author' value='" + escapeHtml(author) + "' /> "
                     + escapeHtml(author));
               out.println("</label>");
            }
         }
         out.println("</div>");
         out.println("<div style='margin-bottom: 12px;'>");
         out.println("<strong>Sort by price:</strong><br /><br />");
         out.println("<label style='margin-right: 12px;'><input type='radio' name='sort' value='asc' checked /> Low to High</label>");
         out.println("<label><input type='radio' name='sort' value='desc' /> High to Low</label>");
         out.println("</div>");
         out.println("<input type='submit' value='Search and Buy' style='padding: 10px 16px; border: 1px solid #6e4b3a; background: #6e4b3a; color: white; cursor: pointer;' />");
         out.println("</form>");

         out.println("<div style='display: grid; grid-template-columns: repeat(auto-fill, minmax(210px, 1fr)); gap: 18px;'>");
         try (ResultSet bookRset = bookStmt.executeQuery()) {
            while (bookRset.next()) {
               out.println("<div style='background: white; border: 1px solid #d9ccb8; padding: 14px; box-shadow: 0 4px 14px rgba(60, 40, 20, 0.08);'>");
               out.println("<img src='" + escapeHtml(bookRset.getString("image_path")) + "' alt='"
                     + escapeHtml(bookRset.getString("title"))
                     + "' style='width: 100%; height: 260px; object-fit: cover; border: 1px solid #e4d7c5;' />");
               out.println("<h3 style='margin: 12px 0 8px; color: #3b2f2f; font-size: 18px;'>"
                     + escapeHtml(bookRset.getString("title")) + "</h3>");
               out.println("<p style='margin: 0 0 6px; color: #6b5d54;'><strong>Author:</strong> "
                     + escapeHtml(bookRset.getString("author")) + "</p>");
               out.println("<p style='margin: 0 0 6px; color: #6b5d54;'><strong>Price:</strong> $"
                     + escapeHtml(bookRset.getString("price")) + "</p>");
               out.println("<p style='margin: 0; color: #6b5d54;'><strong>Stock:</strong> "
                     + escapeHtml(bookRset.getString("qty")) + "</p>");
               out.println("</div>");
            }
         }
         out.println("</div>");
      } catch (SQLException ex) {
         out.println("<p>Error: " + escapeHtml(ex.getMessage()) + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      out.println("</body></html>");
      out.close();
   }
}
