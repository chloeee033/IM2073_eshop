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
      out.println("<html lang='en'>");
      out.println("<head>");
      out.println("  <meta charset='UTF-8'>");
      out.println("  <title>Admin Dashboard - eBookshop</title>");
      // 1. 引入 Bootstrap 和 动画库
      out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
      out.println("  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css'/>");
      // 2. 统一 CSS 样式
      out.println("  <style>");
      out.println("    body { background-color: #f6f1e7; font-family: 'Georgia', serif; padding: 30px; color: #3b2f2f; }");
      out.println("    .dashboard-container { background: white; padding: 30px; border-radius: 15px; border: 1px solid #d9ccb8; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }");
      out.println("    .stat-card { border: none; border-radius: 10px; transition: transform 0.3s; box-shadow: 0 4px 10px rgba(0,0,0,0.03); }");
      out.println("    .stat-card:hover { transform: translateY(-5px); }");
      out.println("    .table thead { background-color: #6e4b3a; color: #fff4df; }");
      out.println("    .badge-price { background-color: #6e4b3a; color: white; }");
      out.println("  </style>");
      out.println("</head>");

      out.println("<body>");
      out.println("<div class='container-fluid dashboard-container animate__animated animate__fadeIn'>");
      
      // 顶部导航栏
      out.println("<div class='d-flex justify-content-between align-items: center; border-bottom pb-3 mb-4'>");
      out.println("  <a href='eshophome' class='btn btn-outline-secondary btn-sm'>← Back to Shop</a>");
      out.println("  <h2 class='m-0 fw-bold'>Admin Statistics</h2>");
      out.println("  <span class='badge rounded-pill bg-success p-2'>● Logged in as Admin</span>");
      out.println("</div>");

      out.println("<div class='row g-4'>"); // 使用 Bootstrap 的网格系统布局

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         Statement stmt = conn.createStatement();
      ) {
         // --- 左侧：Top 5 Customers ---
         out.println("<div class='col-lg-6'>");
         out.println("  <div class='card stat-card h-100'>");
         out.println("    <div class='card-header bg-white fw-bold'> Top 5 Customers</div>");
         out.println("    <div class='card-body'>");
         out.println("      <table class='table table-hover'>");
         out.println("        <thead><tr><th>Name</th><th>Email</th><th>Total Spent</th></tr></thead>");
         out.println("        <tbody>");

         String customerSql = "SELECT c.cust_name, c.cust_email, SUM(oi.qty_ordered * oi.price_at_order) AS total_spent "
                            + "FROM customers c JOIN orders o ON c.customer_id = o.customer_id "
                            + "JOIN order_items oi ON o.order_id = oi.order_id "
                            + "GROUP BY c.customer_id ORDER BY total_spent DESC LIMIT 5";

         try (ResultSet customerRset = stmt.executeQuery(customerSql)) {
            while (customerRset.next()) {
               out.println("<tr>");
               out.println("  <td>" + escapeHtml(customerRset.getString("cust_name")) + "</td>");
               out.println("  <td class='small'>" + escapeHtml(customerRset.getString("cust_email")) + "</td>");
               out.println("  <td><span class='badge badge-price'>$" + customerRset.getBigDecimal("total_spent") + "</span></td>");
               out.println("</tr>");
            }
         }
         out.println("        </tbody></table>");
         out.println("    </div></div></div>");

         // --- 右侧：Top 5 Books ---
         out.println("<div class='col-lg-6'>");
         out.println("  <div class='card stat-card h-100'>");
         out.println("    <div class='card-header bg-white fw-bold'> Top Selling Books</div>");
         out.println("    <div class='card-body'>");
         out.println("      <table class='table table-hover'>");
         out.println("        <thead><tr><th>Title</th><th>Sold</th><th>Revenue</th></tr></thead>");
         out.println("        <tbody>");

         String bookSql = "SELECT b.title, SUM(oi.qty_ordered) AS total_sold, SUM(oi.qty_ordered * oi.price_at_order) AS revenue "
                        + "FROM books b JOIN order_items oi ON b.id = oi.book_id "
                        + "GROUP BY b.id ORDER BY total_sold DESC LIMIT 5";

         try (ResultSet bookRset = stmt.executeQuery(bookSql)) {
            while (bookRset.next()) {
               out.println("<tr>");
               out.println("  <td>" + escapeHtml(bookRset.getString("title")) + "</td>");
               out.println("  <td>" + bookRset.getInt("total_sold") + "</td>");
               out.println("  <td class='text-success fw-bold'>$" + bookRset.getBigDecimal("revenue") + "</td>");
               out.println("</tr>");
            }
         }
         out.println("        </tbody></table>");
         out.println("    </div></div></div>");

      } catch (SQLException ex) {
         out.println("<div class='alert alert-danger'>Error: " + escapeHtml(ex.getMessage()) + "</div>");
      }

      out.println("</div>"); // 结束 row
      out.println("<p class='text-center text-muted mt-5 small'>End of Statistics Report</p>");
      out.println("</div>"); // 结束 container
      out.println("</body></html>");
      
      /*response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Admin Statistics</title></head>");
      out.println("<body>");
      out.println("<div style='display: flex; justify-content: space-between; margin-bottom: 16px;'>");
      out.println("<a href='eshophome'>Back to Shop</a>");
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
      out.close();*/
   }
}
