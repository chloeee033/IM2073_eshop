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
      out.println("<html>");
      out.println("<head><title>Order Response</title></head>");
      out.println("<body>");

      try (Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx")) {
         String[] ids = request.getParameterValues("id");
         String custName = request.getParameter("cust_name");
         String custEmail = request.getParameter("cust_email");
         String custPhone = request.getParameter("cust_phone");

         if (ids == null) {
            out.println("<h3>Please go back and select a book...</h3>");
            out.println("</body></html>");
            out.close();
            return;
         }

         boolean missingCustomerDetails = custName == null || custName.isBlank()
               || custEmail == null || custEmail.isBlank()
               || custPhone == null || custPhone.isBlank();

         if (missingCustomerDetails) {
            out.println("<h3>Please go back and enter all customer details.</h3>");
            out.println("</body></html>");
            out.close();
            return;
         }

         conn.setAutoCommit(false);

         try (
            PreparedStatement selectCustomerStmt =
                  conn.prepareStatement("SELECT customer_id FROM customers WHERE cust_email = ?");
            PreparedStatement updateCustomerStmt = conn.prepareStatement(
                  "UPDATE customers SET cust_name = ?, cust_phone = ? WHERE customer_id = ?");
            PreparedStatement insertCustomerStmt = conn.prepareStatement(
                  "INSERT INTO customers (cust_name, cust_email, cust_phone) VALUES (?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertOrderStmt = conn.prepareStatement(
                  "INSERT INTO orders (customer_id, status) VALUES (?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
            PreparedStatement selectBookStmt = conn.prepareStatement(
                  "SELECT title, price, qty FROM books WHERE id = ?");
            PreparedStatement insertOrderItemStmt = conn.prepareStatement(
                  "INSERT INTO order_items (order_id, book_id, qty_ordered, price_at_order) "
                  + "VALUES (?, ?, ?, ?)");
            PreparedStatement updateBookStmt =
                  conn.prepareStatement("UPDATE books SET qty = qty - 1 WHERE id = ? AND qty > 0")
         ) {
            int customerId;
            int orderId;

            selectCustomerStmt.setString(1, custEmail);
            try (ResultSet customerRset = selectCustomerStmt.executeQuery()) {
               if (customerRset.next()) {
                  customerId = customerRset.getInt("customer_id");
                  updateCustomerStmt.setString(1, custName);
                  updateCustomerStmt.setString(2, custPhone);
                  updateCustomerStmt.setInt(3, customerId);
                  updateCustomerStmt.executeUpdate();
               } else {
                  insertCustomerStmt.setString(1, custName);
                  insertCustomerStmt.setString(2, custEmail);
                  insertCustomerStmt.setString(3, custPhone);
                  insertCustomerStmt.executeUpdate();
                  try (ResultSet generatedKeys = insertCustomerStmt.getGeneratedKeys()) {
                     if (!generatedKeys.next()) {
                        throw new SQLException("Unable to create customer record.");
                     }
                     customerId = generatedKeys.getInt(1);
                  }
               }
            }

            insertOrderStmt.setInt(1, customerId);
            insertOrderStmt.setString(2, "PLACED");
            insertOrderStmt.executeUpdate();
            try (ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys()) {
               if (!generatedKeys.next()) {
                  throw new SQLException("Unable to create order record.");
               }
               orderId = generatedKeys.getInt(1);
            }

            out.println("<h3>Customer Details</h3>");
            out.println("<p>Name: " + escapeHtml(custName) + "</p>");
            out.println("<p>Email: " + escapeHtml(custEmail) + "</p>");
            out.println("<p>Phone: " + escapeHtml(custPhone) + "</p>");
            out.println("<p>Customer ID: " + customerId + "</p>");
            out.println("<p>Order ID: " + orderId + "</p>");

            for (String idValue : ids) {
               int bookId = Integer.parseInt(idValue);
               String title;
               double price;
               int qty;

               selectBookStmt.setInt(1, bookId);
               try (ResultSet bookRset = selectBookStmt.executeQuery()) {
                  if (!bookRset.next()) {
                     throw new SQLException("Book id " + bookId + " does not exist.");
                  }
                  title = bookRset.getString("title");
                  price = bookRset.getDouble("price");
                  qty = bookRset.getInt("qty");
               }

               if (qty <= 0) {
                  throw new SQLException("Book id " + bookId + " is out of stock.");
               }

               String sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + bookId;
               out.println("<p>" + sqlStr + "</p>");
               updateBookStmt.setInt(1, bookId);
               int count = updateBookStmt.executeUpdate();
               out.println("<p>" + count + " record updated.</p>");

               sqlStr = "INSERT INTO order_items (order_id, book_id, qty_ordered, price_at_order) "
                     + "VALUES (" + orderId + ", " + bookId + ", 1, " + price + ")";
               out.println("<p>" + sqlStr + "</p>");
               insertOrderItemStmt.setInt(1, orderId);
               insertOrderItemStmt.setInt(2, bookId);
               insertOrderItemStmt.setInt(3, 1);
               insertOrderItemStmt.setDouble(4, price);
               count = insertOrderItemStmt.executeUpdate();
               out.println("<p>" + count + " order item inserted.</p>");

               out.println("<h3>Your order for book id=" + bookId + " (" + escapeHtml(title)
                     + ") has been confirmed.</h3>");
            }

            conn.commit();
            out.println("<h3>Thank you.</h3>");
         } catch (SQLException ex) {
            conn.rollback();
            throw ex;
         }
      } catch (SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      out.println("</body></html>");
      out.close();
   }
}
