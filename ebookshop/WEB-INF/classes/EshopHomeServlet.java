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
      out.println("<html lang='en'>");
      out.println("<head>");
      out.println("  <meta charset='UTF-8'>");
      out.println("  <title>Yet Another e-Bookshop</title>");
      out.println("  <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
      out.println("  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css'/>");
      
      out.println("  <style>");
      out.println("    body { background: #f6f1e7; font-family: 'Georgia', serif; color: #3b2f2f; padding-bottom: 50px; }");
      
      // 标题样式
      out.println("    .main-title { font-weight: 800; color: #3b2f2f; text-shadow: 3px 3px 4px rgba(110, 75, 58, 0.6); letter-spacing: -1px; }");
      out.println("    .title-underline { height: 4px; width: 625px; background-color: #6e4b3a; margin-top: 8px; border-radius: 2px; }");

      // 书本卡片样式
      out.println("    .book-card { transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); border: 1px solid #d9ccb8; background: white; }");
      out.println("    .book-card:hover { transform: translateY(-8px); box-shadow: 0 12px 24px rgba(60, 40, 20, 0.15) !important; }");
      
      // 按钮美化
      out.println("    .btn-brown { background-color: #6e4b3a; color: white; border: none; transition: 0.3s; }");
      out.println("    .btn-brown:hover { background-color: #553a2d; color: #fff4df; transform: scale(1.05); }");
      
      // 筛选区域样式
      out.println("    .filter-section { background: #fffaf0; border: 1px solid #d4c2aa; padding: 25px; border-radius: 8px; }");
      out.println("  </style>"); 

      out.println("</head>");
      out.println("<body>");
      out.println("<div class='container mt-5'>");

      out.println("<div class='d-flex justify-content-between align-items-center mb-5 animate__animated animate__fadeInDown'>");
      out.println("  <div>");
      out.println("    <h1 class='display-4 main-title'>Yet Another e-Bookshop</h1>");
      out.println("    <div class='title-underline'></div>"); 
      out.println("    <p class='lead text-muted mt-3' style='font-style: italic;'>Curated collection of digital wisdom.</p>");

      out.println("  </div>");
      out.println("  <a href='adminlogin' class='btn btn-outline-dark px-4 py-2 fw-bold' style='border-radius: 0;'>Admin Access</a>");
      out.println("</div>");

      try (
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");
         PreparedStatement authorStmt = conn.prepareStatement("SELECT DISTINCT author FROM books ORDER BY author ASC");
         PreparedStatement bookStmt = conn.prepareStatement("SELECT * FROM books ORDER BY author ASC, title ASC");
      ) {
         out.println("<form method='get' action='eshopquery' class='filter-section mb-5 shadow-sm animate__animated animate__fadeIn'>");
         out.println("  <h4 class='mb-4'><i class='bi bi-filter'></i> Filter Collections</h4>");
         
         out.println("  <div class='mb-4'>");
         out.println("    <p class='fw-bold mb-3 text-secondary'>Choose Authors:</p>");
         try (ResultSet authorRset = authorStmt.executeQuery()) {
            while (authorRset.next()) {
               String author = authorRset.getString("author");
               out.println("<div class='form-check form-check-inline me-4 mb-2'>");
               out.println("  <input class='form-check-input' type='checkbox' name='author' value='" + escapeHtml(author) + "' id='check-"+author.hashCode()+"'>");
               out.println("  <label class='form-check-label' for='check-"+author.hashCode()+"'>" + escapeHtml(author) + "</label>");
               out.println("</div>");
            }
         }
         out.println("  </div>");

         out.println("<div class='row align-items-center'>");
         out.println("<div class='col-md-6'>");
         out.println("<span class='fw-bold me-3 text-secondary'>Sort Price:</span>");
         out.println("<div class='form-check form-check-inline'>");
         out.println("        <input class='form-check-input' type='radio' name='sort' value='asc' checked> Low to High");
         out.println("      </div>");
         out.println("      <div class='form-check form-check-inline'>");
         out.println("        <input class='form-check-input' type='radio' name='sort' value='desc'> High to Low");
         out.println("      </div>");
         out.println("    </div>");
         out.println("    <div class='col-md-6 text-md-end mt-3 mt-md-0'>");
         out.println("      <button type='submit' class='btn btn-brown btn-lg px-5 shadow-sm'>Apply Filters & Shop</button>");
         out.println("    </div>");
         out.println("  </div>");
         out.println("</form>");

         out.println("<div class='row g-4'>");
         try (ResultSet bookRset = bookStmt.executeQuery()) {
            int delay = 0;
            while (bookRset.next()) {
               String animationClass = "animate__animated animate__fadeInUp";
               out.println("<div class='col-sm-6 col-md-4 col-lg-3 " + animationClass + "' style='animation-delay: " + (delay * 0.1) + "s;'>");
               out.println("<div class='card h-100 book-card shadow-sm p-3'>");
               out.println("<img src='" + escapeHtml(bookRset.getString("image_path")) + "' class='card-img-top shadow-sm' style='height: 280px; object-fit: cover; border-radius: 4px;'>");
               out.println("    <div class='card-body px-0 pb-0'>");
               out.println("      <h5 class='card-title fw-bold mb-2' style='font-size: 1.1rem; color: #3b2f2f;'>" + escapeHtml(bookRset.getString("title")) + "</h5>");
               out.println("      <p class='card-text text-muted mb-1 small'>By " + escapeHtml(bookRset.getString("author")) + "</p>");
               out.println("      <div class='d-flex justify-content-between align-items-center mt-3'>");
               out.println("        <span class='fs-5 fw-bold text-danger'>$" + escapeHtml(bookRset.getString("price")) + "</span>");
               out.println("        <span class='badge bg-light text-dark border'>Stock: " + bookRset.getInt("qty") + "</span>");
               out.println("      </div>");
               out.println("    </div>");
               out.println("  </div>");
               out.println("</div>");
               delay++;
            }
         }
         out.println("</div>");

      } catch (SQLException ex) {
         out.println("<div class='alert alert-danger mt-4'>Database Error: " + escapeHtml(ex.getMessage()) + "</div>");
      }

      out.println("</div>");
      out.println("<footer class='text-center mt-5 mb-4 text-muted small'>&copy; 2026 e-Bookshop Experience</footer>");
      out.println("</body></html>");
      out.close();
   }
}
