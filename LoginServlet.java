
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");

        if (email == null || password == null ||
            email.isBlank() || password.isBlank()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Email and password are required.");
            return;
        }

        String sql = "SELECT id, name, role, password FROM users WHERE email = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String hashedPassword = rs.getString("password");

                    if (PasswordUtil.checkPassword(password, hashedPassword)) {

                        int userId = rs.getInt("id");
                        String name = rs.getString("name");
                        String role = rs.getString("role");

                        HttpSession session = request.getSession(true);

                        session.setAttribute("userId", userId);
                        session.setAttribute("name", name);
                        session.setAttribute("role", role);

                        session.setMaxInactiveInterval(15 * 60);

                        PrintWriter out = response.getWriter();

                        out.println("<h2>Login Successful!</h2>");
                        out.println("<p>Welcome, " + escapeHtml(name) + "</p>");
                        out.println("<p>Role: " + escapeHtml(role) + "</p>");

                    } else {

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().println(
                            "Invalid email or password."
                        );
                    }

                } else {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().println(
                        "Invalid email or password."
                    );
                }
            }

        } catch (Exception e) {

            getServletContext().log("Login error", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(
                "Something went wrong. Please try again later."
            );
        }
    }

    private String escapeHtml(String value) {

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
