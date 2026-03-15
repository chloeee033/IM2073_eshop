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
      out.println("<html lang='en'>");
      out.println("<head>");
      out.println("  <meta charset='UTF-8'>");
      out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
      out.println("  <title>Admin Login - eBookshop</title>");
      out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
      out.println("  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css'/>");
      out.println("  <style>");
      out.println("    body { background-color: #f6f1e7; height: 100vh; display: flex; align-items: center; justify-content: center; font-family: 'Georgia', serif; }");
      out.println("    .login-card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); width: 100%; max-width: 400px; border: 1px solid #d9ccb8; }");
      out.println("    .btn-brown { background-color: #6e4b3a; color: white; border: none; padding: 10px; transition: 0.3s; }");
      out.println("    .btn-brown:hover { background-color: #553a2d; color: #fff4df; }");
      out.println("  </style>");
      out.println("</head>");

      out.println("<body>");
      out.println("<div class='login-card animate__animated animate__fadeInDown'>");
      
      out.println("  <div class='text-end mb-3'>");
      out.println("    <a href='eshophome' class='btn btn-link btn-sm text-decoration-none' style='color: #6e4b3a;'>← Back to Shop</a>");
      out.println("  </div>");
      
      out.println("  <h2 class='text-center mb-4' style='color: #3b2f2f;'>Admin Login</h2>");

      if (error != null) {
         out.println("<div class='alert alert-danger py-2 small'>" + escapeHtml(error) + "</div>");
      }

      out.println("  <form method='post' action='adminlogin'>");
      out.println("    <div class='mb-3'>");
      out.println("      <label class='form-label small'>Username</label>");
      out.println("      <input type='text' name='username' class='form-control' required />");
      out.println("    </div>");
      
      out.println("    <div class='mb-4'>");
      out.println("      <label class='form-label small'>Password</label>");
      out.println("      <input type='password' name='password' class='form-control' required />");
      out.println("    </div>");

      out.println("    <div class='d-grid'>");
      out.println("      <input type='submit' value='Login' class='btn btn-brown' />");
      out.println("    </div>");
      out.println("  </form>");

      out.println("</div>");
      out.println("</body></html>");
      out.close();
   }

}
