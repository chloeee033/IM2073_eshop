// To save as "ebookshop/WEB-INF/classes/EshopOrderServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshoporder")
public class EshopOrderServlet extends HttpServlet {

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
      processOrder(request, response);
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      processOrder(request, response);
   }

   private void processOrder(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html lang='en'>");
      out.println("<head>");
      out.println("  <meta charset='UTF-8'>");
      out.println("  <title>Order Confirmed - eBookshop</title>");
      out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
      out.println("  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css'/>");
      out.println("  <style>");
      out.println("    body { background-color: #f6f1e7; font-family: 'Georgia', serif; padding: 40px 20px; color: #3b2f2f; }");
      out.println("    .receipt-card { background: white; max-width: 600px; margin: 0 auto; padding: 40px; box-shadow: 0 10px 30px rgba(0,0,0,0.08); border-top: 8px solid #6e4b3a; }");
      out.println("    .success-icon { font-size: 60px; color: #28a745; margin-bottom: 10px; }");
      out.println("    .btn-brown { background-color: #6e4b3a; color: white; border: none; }");
      out.println("    .btn-brown:hover { background-color: #553a2d; color: #fff4df; }");
      out.println("  </style>");
      out.println("</head>");
      out.println("<body>");

      out.println("<div class='receipt-card animate__animated animate__zoomIn'>");

      try (Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx")) {
         
         String[] ids = request.getParameterValues("id");
         String custName = request.getParameter("cust_name");
         String custEmail = request.getParameter("cust_email");
         String custPhone = request.getParameter("cust_phone");

         // 1. 基础检查
         if (ids == null || custName == null || custName.isBlank()) {
            out.println("<div class='text-center'><h3>Invalid request. Please try again.</h3>");
            out.println("<a href='eshophome' class='btn btn-outline-dark mt-3'>Back to Shop</a></div>");
            out.println("</div></body></html>");
            return;
         }

         conn.setAutoCommit(false);

         try (
            PreparedStatement selectCustomerStmt = conn.prepareStatement("SELECT customer_id FROM customers WHERE cust_email = ?");
            PreparedStatement updateCustomerStmt = conn.prepareStatement("UPDATE customers SET cust_name = ?, cust_phone = ? WHERE customer_id = ?");
            PreparedStatement insertCustomerStmt = conn.prepareStatement("INSERT INTO customers (cust_name, cust_email, cust_phone) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertOrderStmt = conn.prepareStatement("INSERT INTO orders (customer_id, status) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement selectBookStmt = conn.prepareStatement("SELECT title, price, qty FROM books WHERE id = ?");
            PreparedStatement insertOrderItemStmt = conn.prepareStatement("INSERT INTO order_items (order_id, book_id, qty_ordered, price_at_order) VALUES (?, ?, ?, ?)");
            PreparedStatement updateBookStmt = conn.prepareStatement("UPDATE books SET qty = qty - 1 WHERE id = ? AND qty > 0")
         ) {
            int customerId;
            int orderId;

            // 2. 处理客户信息
            selectCustomerStmt.setString(1, custEmail);
            try (ResultSet rset = selectCustomerStmt.executeQuery()) {
               if (rset.next()) {
                  customerId = rset.getInt("customer_id");
                  updateCustomerStmt.setString(1, custName);
                  updateCustomerStmt.setString(2, custPhone);
                  updateCustomerStmt.setInt(3, customerId);
                  updateCustomerStmt.executeUpdate();
               } else {
                  insertCustomerStmt.setString(1, custName);
                  insertCustomerStmt.setString(2, custEmail);
                  insertCustomerStmt.setString(3, custPhone);
                  insertCustomerStmt.executeUpdate();
                  try (ResultSet keys = insertCustomerStmt.getGeneratedKeys()) {
                     keys.next(); customerId = keys.getInt(1);
                  }
               }
            }

            // 3. 创建订单
            insertOrderStmt.setInt(1, customerId);
            insertOrderStmt.setString(2, "PLACED");
            insertOrderStmt.executeUpdate();
            try (ResultSet keys = insertOrderStmt.getGeneratedKeys()) {
               keys.next(); orderId = keys.getInt(1);
            }

            // 4. 处理库存和订单项 (删除了所有 SQL 打印)
            for (String idValue : ids) {
               int bookId = Integer.parseInt(idValue);
               double price = 0;
               selectBookStmt.setInt(1, bookId);
               try (ResultSet rset = selectBookStmt.executeQuery()) {
                  if (rset.next()) price = rset.getDouble("price");
               }
               updateBookStmt.setInt(1, bookId);
               updateBookStmt.executeUpdate();
               insertOrderItemStmt.setInt(1, orderId);
               insertOrderItemStmt.setInt(2, bookId);
               insertOrderItemStmt.setInt(3, 1);
               insertOrderItemStmt.setDouble(4, price);
               insertOrderItemStmt.executeUpdate();
            }

            conn.commit();

            // 5. 渲染精美的收据 (这里是最终显示的唯一内容)
            out.println("<div class='text-center mb-4'>");
            out.println("  <div class='success-icon animate__animated animate__bounceIn'>✓</div>");
            out.println("  <h2 class='fw-bold'>Order Confirmed!</h2>");
            out.println("  <p class='text-muted'>Thank you for your purchase, " + escapeHtml(custName) + ".</p>");
            out.println("</div>");

            out.println("<div class='row mb-4 px-2'>");
            out.println("  <div class='col-6 small text-uppercase fw-bold'>Order ID: #" + orderId + "</div>");
            out.println("  <div class='col-6 text-end small text-uppercase fw-bold'>Date: 2026-03-15</div>");
            out.println("</div>");

            out.println("<div class='mb-4 bg-light p-3 border-start border-4 border-brown' style='border-color: #6e4b3a !important;'>");
            out.println("  <h6 class='fw-bold mb-2'>Shipping To:</h6>");
            out.println("  <p class='mb-0'>" + escapeHtml(custName) + "</p>");
            out.println("  <p class='mb-0 text-muted small'>" + escapeHtml(custEmail) + "</p>");
            out.println("  <p class='mb-0 text-muted small'>" + escapeHtml(custPhone) + "</p>");
            out.println("</div>");

            // 只有这一组按钮，放在卡片内部
            out.println("<div class='d-flex justify-content-center gap-3 mt-4'>");
            out.println("  <button onclick='window.print()' class='btn btn-brown px-4 py-2 fw-bold shadow-sm'>Print Receipt</button>");
            out.println("  <a href='eshophome' class='btn btn-outline-dark px-4 py-2 fw-bold shadow-sm'>Shop More</a>");
            out.println("</div>");

         } catch (SQLException ex) {
            conn.rollback();
            throw ex;
         }
      } catch (SQLException ex) {
         out.println("<div class='alert alert-danger'><h4>Order Failed</h4><p>" + escapeHtml(ex.getMessage()) + "</p></div>");
      }

      out.println("</div>"); // 闭合 receipt-card
      out.println("</body></html>");
      out.close();
   }
}
