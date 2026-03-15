import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class ViewRenderer {

   private ViewRenderer() {
   }

   public static void writeHtml(HttpServletResponse response, String html) throws IOException {
      response.setContentType("text/html;charset=UTF-8");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(html);
   }

   public static String renderPage(
         HttpServletRequest request,
         String pageTitle,
         String bodyClass,
         String pageCss,
         String pageJs,
         String shellTemplatePath,
         Map<String, String> shellTokens,
         Map<String, Object> pageData) throws IOException {
      String shell = renderTemplate(request.getServletContext(), shellTemplatePath, shellTokens);
      return renderTemplate(request.getServletContext(), "/templates/layout.html", Map.of(
            "PAGE_TITLE", escapeHtml(pageTitle),
            "BODY_CLASS", escapeHtml(bodyClass),
            "CONTEXT_PATH", request.getContextPath(),
            "PAGE_CSS", appUrl(request, "/assets/css/" + pageCss),
            "PAGE_JS", appUrl(request, "/assets/js/" + pageJs),
            "APP_SHELL", shell,
            "PAGE_DATA_JSON", toJson(pageData)));
   }

   public static String renderTemplate(ServletContext context, String templatePath, Map<String, String> tokens)
         throws IOException {
      String template = readTextResource(context, templatePath);
      for (Map.Entry<String, String> entry : tokens.entrySet()) {
         template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
      }
      return template;
   }

   public static String readTextResource(ServletContext context, String resourcePath) throws IOException {
      try (InputStream inputStream = context.getResourceAsStream(resourcePath)) {
         if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
         }
         return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
   }

   public static String templateElement(ServletContext context, String templateId, String resourcePath)
         throws IOException {
      return "<template id=\"" + escapeHtml(templateId) + "\">\n"
            + readTextResource(context, resourcePath)
            + "\n</template>";
   }

   public static String appUrl(HttpServletRequest request, String path) {
      if (path == null || path.isBlank()) {
         return request.getContextPath();
      }
      if (path.startsWith("http://") || path.startsWith("https://")) {
         return path;
      }
      if (path.startsWith("/")) {
         return request.getContextPath() + path;
      }
      return request.getContextPath() + "/" + path;
   }

   public static String escapeHtml(String input) {
      if (input == null) {
         return "";
      }
      return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
   }

   public static String toJson(Object value) {
      StringBuilder builder = new StringBuilder();
      appendJson(builder, value);
      return builder.toString();
   }

   private static void appendJson(StringBuilder builder, Object value) {
      if (value == null) {
         builder.append("null");
         return;
      }

      if (value instanceof String) {
         appendJsonString(builder, (String) value);
         return;
      }

      if (value instanceof Number || value instanceof Boolean) {
         builder.append(String.valueOf(value));
         return;
      }

      if (value instanceof Map<?, ?>) {
         builder.append('{');
         boolean first = true;
         for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            if (!first) {
               builder.append(',');
            }
            first = false;
            appendJsonString(builder, String.valueOf(entry.getKey()));
            builder.append(':');
            appendJson(builder, entry.getValue());
         }
         builder.append('}');
         return;
      }

      if (value instanceof Iterable<?>) {
         builder.append('[');
         Iterator<?> iterator = ((Iterable<?>) value).iterator();
         boolean first = true;
         while (iterator.hasNext()) {
            if (!first) {
               builder.append(',');
            }
            first = false;
            appendJson(builder, iterator.next());
         }
         builder.append(']');
         return;
      }

      if (value.getClass().isArray()) {
         builder.append('[');
         int length = Array.getLength(value);
         for (int i = 0; i < length; i++) {
            if (i > 0) {
               builder.append(',');
            }
            appendJson(builder, Array.get(value, i));
         }
         builder.append(']');
         return;
      }

      appendJsonString(builder, String.valueOf(value));
   }

   private static void appendJsonString(StringBuilder builder, String value) {
      builder.append('"');
      for (int i = 0; i < value.length(); i++) {
         char ch = value.charAt(i);
         switch (ch) {
            case '"':
               builder.append("\\\"");
               break;
            case '\\':
               builder.append("\\\\");
               break;
            case '\b':
               builder.append("\\b");
               break;
            case '\f':
               builder.append("\\f");
               break;
            case '\n':
               builder.append("\\n");
               break;
            case '\r':
               builder.append("\\r");
               break;
            case '\t':
               builder.append("\\t");
               break;
            case '<':
               builder.append("\\u003c");
               break;
            case '>':
               builder.append("\\u003e");
               break;
            case '&':
               builder.append("\\u0026");
               break;
            case '\'':
               builder.append("\\u0027");
               break;
            default:
               if (ch < 0x20 || ch == '\u2028' || ch == '\u2029') {
                  builder.append(String.format("\\u%04x", (int) ch));
               } else {
                  builder.append(ch);
               }
         }
      }
      builder.append('"');
   }
}
