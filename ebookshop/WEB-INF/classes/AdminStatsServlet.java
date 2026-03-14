import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/adminstats")
public class AdminStatsServlet extends HttpServlet {

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
      HttpSession session = request.getSession(false);
      if (session == null || !Boolean.TRUE.equals(session.getAttribute("isAdminAuthenticated"))) {
         response.sendRedirect("adminlogin");
         return;
      }

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Admin Statistics</title></head>");
      out.println("<body>");
      out.println("<div style='display: flex; justify-content: space-between; margin-bottom: 16px;'>");
      out.println("<a href='eshopquery.html'>Back to Shop</a>");
      out.println("<strong>Admin Statistics</strong>");
      out.println("<span>Logged in as admin</span>");
      out.println("</div>");
      out.println("<div style='display: flex; gap: 24px; align-items: flex-start;'>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         Statement stmt = conn.createStatement();
      ) {
         out.println("<div style='flex: 1;'>");
         out.println("<h3>Top 5 Customers</h3>");
         out.println("<table border='1' cellpadding='6' cellspacing='0'>");
         out.println("<tr><th>Name</th><th>Email</th><th>Phone</th><th>Total Spent</th></tr>");

         String customerSql =
               "SELECT c.cust_name, c.cust_email, c.cust_phone, "
               + "SUM(oi.qty_ordered * oi.price_at_order) AS total_spent "
               + "FROM customers c "
               + "JOIN orders o ON c.customer_id = o.customer_id "
               + "JOIN order_items oi ON o.order_id = oi.order_id "
               + "GROUP BY c.customer_id, c.cust_name, c.cust_email, c.cust_phone "
               + "ORDER BY total_spent DESC, c.cust_name ASC "
               + "LIMIT 5";
         try (ResultSet customerRset = stmt.executeQuery(customerSql)) {
            boolean hasRows = false;
            while (customerRset.next()) {
               hasRows = true;
               out.println("<tr>");
               out.println("<td>" + escapeHtml(customerRset.getString("cust_name")) + "</td>");
               out.println("<td>" + escapeHtml(customerRset.getString("cust_email")) + "</td>");
               out.println("<td>" + escapeHtml(customerRset.getString("cust_phone")) + "</td>");
               out.println("<td>$" + customerRset.getBigDecimal("total_spent") + "</td>");
               out.println("</tr>");
            }
            if (!hasRows) {
               out.println("<tr><td colspan='4'>No customer orders yet.</td></tr>");
            }
         }
         out.println("</table>");
         out.println("</div>");

         out.println("<div style='flex: 1;'>");
         out.println("<h3>Top 5 Books</h3>");
         out.println("<table border='1' cellpadding='6' cellspacing='0'>");
         out.println("<tr><th>Title</th><th>Author</th><th>Total Sold</th><th>Revenue</th></tr>");

         String bookSql =
               "SELECT b.title, b.author, SUM(oi.qty_ordered) AS total_sold, "
               + "SUM(oi.qty_ordered * oi.price_at_order) AS revenue "
               + "FROM books b "
               + "JOIN order_items oi ON b.id = oi.book_id "
               + "GROUP BY b.id, b.title, b.author "
               + "ORDER BY total_sold DESC, revenue DESC, b.title ASC "
               + "LIMIT 5";
         try (ResultSet bookRset = stmt.executeQuery(bookSql)) {
            boolean hasRows = false;
            while (bookRset.next()) {
               hasRows = true;
               out.println("<tr>");
               out.println("<td>" + escapeHtml(bookRset.getString("title")) + "</td>");
               out.println("<td>" + escapeHtml(bookRset.getString("author")) + "</td>");
               out.println("<td>" + bookRset.getInt("total_sold") + "</td>");
               out.println("<td>$" + bookRset.getBigDecimal("revenue") + "</td>");
               out.println("</tr>");
            }
            if (!hasRows) {
               out.println("<tr><td colspan='4'>No book sales yet.</td></tr>");
            }
         }
         out.println("</table>");
         out.println("</div>");
      } catch (SQLException ex) {
         out.println("<p>Error: " + escapeHtml(ex.getMessage()) + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      out.println("</div>");
      out.println("</body></html>");
      out.close();
   }
}
