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
      out.println("<html lang='en'>");
      out.println("<head>");
      out.println("  <meta charset='UTF-8'>");
      out.println("  <title>Your Selection - eBookshop</title>");
      // 1. 引入资源
      out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
      out.println("  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css'/>");
      // 2. 统一全站 CSS
      out.println("  <style>");
      out.println("    body { background-color: #f6f1e7; font-family: 'Georgia', serif; color: #3b2f2f; padding: 20px; }");
      out.println("    .main-container { background: white; padding: 30px; border-radius: 12px; border: 1px solid #d9ccb8; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }");
      out.println("    .table { background: white; }");
      out.println("    .table thead { background-color: #6e4b3a; color: white; }");
      out.println("    .book-img { transition: transform 0.2s; border-radius: 4px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
      out.println("    .book-img:hover { transform: scale(1.1); }");
      out.println("    .btn-brown { background-color: #6e4b3a; color: white; border: none; padding: 12px 30px; }");
      out.println("    .btn-brown:hover { background-color: #553a2d; color: #fff4df; }");
      out.println("    .customer-card { background-color: #fffaf0; border: 1px dashed #d4c2aa; padding: 20px; border-radius: 8px; }");
      out.println("  </style>");
      out.println("</head>");

      out.println("<body>");
      out.println("<div class='container main-container animate__animated animate__fadeIn'>");

      // 顶部导航
      out.println("<div class='d-flex justify-content-between mb-4 border-bottom pb-3'>");
      out.println("  <a href='eshophome' class='btn btn-outline-dark btn-sm'>← Back to Home</a>");
      out.println("  <h2 class='m-0 fw-bold'>Your Book Selection</h2>");
      out.println("  <a href='adminlogin' class='btn btn-outline-secondary btn-sm'>Admin Login</a>");
      out.println("</div>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         Statement stmt = conn.createStatement();
      ) {
         String[] authors = request.getParameterValues("author");
         String sortOrder = request.getParameter("sort");
         
         if (authors == null) {
            out.println("<div class='alert alert-warning animate__animated animate__shakeX'>No author selected. Please go back to select author(s).</div>");
            out.println("</div></body></html>");
            return;
         }

         // SQL 逻辑保持不变...
         if (!"desc".equalsIgnoreCase(sortOrder)) sortOrder = "asc"; else sortOrder = "desc";
         String sqlStr = "SELECT * FROM books WHERE author IN (";
         for (int i = 0; i < authors.length; ++i) {
            sqlStr += "'" + authors[i] + "'" + (i < authors.length - 1 ? ", " : "");
         }
         sqlStr += ") AND qty > 0 ORDER BY price " + sortOrder.toUpperCase();

         ResultSet rset = stmt.executeQuery(sqlStr);

         out.println("<form method='post' action='eshoporder'>");
         
         // 3. 美化表格
         out.println("<div class='table-responsive mb-5'>");
         out.println("<table class='table table-hover align-middle'>");
         out.println("<thead><tr><th>Select</th><th>Cover</th><th>Author</th><th>Title</th><th>Price</th></tr></thead>");
         out.println("<tbody>");
         
         int count = 0;
         while (rset.next()) {
            out.println("<tr>");
            out.println("<td><input type='checkbox' class='form-check-input' name='id' value='" + escapeHtml(rset.getString("id")) + "' /></td>");
            out.println("<td><img class='book-img' src='" + escapeHtml(rset.getString("image_path")) + "' width='60' height='80'></td>");
            out.println("<td>" + escapeHtml(rset.getString("author")) + "</td>");
            out.println("<td class='fw-bold'>" + escapeHtml(rset.getString("title")) + "</td>");
            out.println("<td class='text-danger fw-bold'>$" + escapeHtml(rset.getString("price")) + "</td>");
            out.println("</tr>");
            count++;
         }
         out.println("</tbody></table>");
         out.println("<p class='text-muted small'>Found " + count + " match(es) for your selection.</p>");
         out.println("</div>");

         // 4. 美化结账表单
         out.println("<div class='customer-card shadow-sm mb-4'>");
         out.println("  <h4 class='mb-4'><i class='bi bi-person-fill'></i> Shipping Details</h4>");
         out.println("  <div class='row'>");
         out.println("    <div class='col-md-4 mb-3'><label class='form-label'>Full Name</label><input type='text' name='cust_name' class='form-control' required /></div>");
         out.println("    <div class='col-md-4 mb-3'><label class='form-label'>Email Address</label><input type='email' name='cust_email' class='form-control' required /></div>");
         out.println("    <div class='col-md-4 mb-3'><label class='form-label'>Phone Number</label><input type='text' name='cust_phone' class='form-control' required /></div>");
         out.println("  </div>");
         out.println("</div>");

         out.println("<div class='text-center mt-4'>");
         out.println("  <button type='submit' class='btn btn-brown btn-lg rounded-pill animate__animated animate__pulse animate__infinite'>CONFIRM & PLACE ORDER</button>");
         out.println("</div>");
         
         out.println("</form>");
      } catch (SQLException ex) {
         out.println("<div class='alert alert-danger'>Error: " + ex.getMessage() + "</div>");
      }

      out.println("</div><footer class='text-center mt-5 text-muted small'>&copy; 2026 e-Bookshop Experience</footer></body></html>");
      out.close();

      /*response.setContentType("text/html");
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
      out.close();*/
   }
}
