import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/adminlogin")
public class AdminLoginServlet extends HttpServlet {

   private static final String ADMIN_USERNAME = "admin";
   private static final String ADMIN_PASSWORD = "adminlook";

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
      renderLoginPage(request, response, null);
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      String username = request.getParameter("username");
      String password = request.getParameter("password");

      if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
         HttpSession session = request.getSession(true);
         session.setAttribute("isAdminAuthenticated", Boolean.TRUE);
         response.sendRedirect("adminstats");
         return;
      }

      renderLoginPage(request, response, "Invalid username or password.");
   }

   private void renderLoginPage(HttpServletRequest request, HttpServletResponse response, String error)
               throws IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Admin Login</title></head>");
      out.println("<body>");
      out.println("<div style='text-align: right; margin-bottom: 12px;'>");
      out.println("<a href='eshopquery.html'>Back to Shop</a>");
      out.println("</div>");
      out.println("<h2>Admin Login</h2>");
      if (error != null) {
         out.println("<p style='color: red;'>" + escapeHtml(error) + "</p>");
      }
      out.println("<form method='post' action='adminlogin'>");
      out.println("<p>Username: <input type='text' name='username' required /></p>");
      out.println("<p>Password: <input type='password' name='password' required /></p>");
      out.println("<p><input type='submit' value='Login' /></p>");
      out.println("</form>");
      out.println("</body></html>");
      out.close();
   }
}
