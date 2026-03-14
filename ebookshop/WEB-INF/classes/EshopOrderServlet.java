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

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         PreparedStatement updateBookStmt =
               conn.prepareStatement("UPDATE books SET qty = qty - 1 WHERE id = ?");
         PreparedStatement insertOrderStmt = conn.prepareStatement(
               "INSERT INTO order_records (id, qty_ordered, cust_name, cust_email, cust_phone) "
               + "VALUES (?, ?, ?, ?, ?)");
      ) {
         String[] ids = request.getParameterValues("id");
         String custName = request.getParameter("cust_name");
         String custEmail = request.getParameter("cust_email");
         String custPhone = request.getParameter("cust_phone");

         if (ids != null) {
            int count;
            boolean missingCustomerDetails = custName == null || custName.isBlank()
                  || custEmail == null || custEmail.isBlank()
                  || custPhone == null || custPhone.isBlank();

            if (missingCustomerDetails) {
               out.println("<h3>Please go back and enter all customer details.</h3>");
               out.println("</body></html>");
               out.close();
               return;
            }

            out.println("<h3>Customer Details</h3>");
            out.println("<p>Name: " + escapeHtml(custName) + "</p>");
            out.println("<p>Email: " + escapeHtml(custEmail) + "</p>");
            out.println("<p>Phone: " + escapeHtml(custPhone) + "</p>");

            for (int i = 0; i < ids.length; ++i) {
               String sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + ids[i];
               out.println("<p>" + sqlStr + "</p>");
               updateBookStmt.setInt(1, Integer.parseInt(ids[i]));
               count = updateBookStmt.executeUpdate();
               out.println("<p>" + count + " record updated.</p>");

               sqlStr = "INSERT INTO order_records (id, qty_ordered, cust_name, cust_email, cust_phone) "
                     + "VALUES (" + ids[i] + ", 1, '" + custName + "', '" + custEmail + "', '"
                     + custPhone + "')";
               out.println("<p>" + sqlStr + "</p>");
               insertOrderStmt.setInt(1, Integer.parseInt(ids[i]));
               insertOrderStmt.setInt(2, 1);
               insertOrderStmt.setString(3, custName);
               insertOrderStmt.setString(4, custEmail);
               insertOrderStmt.setString(5, custPhone);
               count = insertOrderStmt.executeUpdate();
               out.println("<p>" + count + " record inserted.</p>");

               out.println("<h3>Your order for book id=" + ids[i]
                     + " has been confirmed.</h3>");
            }
            out.println("<h3>Thank you.</h3>");
         } else {
            out.println("<h3>Please go back and select a book...</h3>");
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
