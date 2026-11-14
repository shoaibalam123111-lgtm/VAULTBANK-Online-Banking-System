
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
import java.math.BigDecimal;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println(
                "{\"error\":\"Please login first\"}"
            );
            return;
        }

        // Get user ID from trusted server-side session
        int userId = (Integer) session.getAttribute("userId");

        String sql =
            "SELECT u.id, u.name, u.email, u.role, " +
            "a.account_number, a.balance, a.status " +
            "FROM users u " +
            "JOIN accounts a ON u.id = a.user_id " +
            "WHERE u.id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                PrintWriter out = response.getWriter();

                if (rs.next()) {

                    BigDecimal balance = rs.getBigDecimal("balance");

                    out.println("{");
                    out.println("\"id\": " + rs.getInt("id") + ",");
                    out.println("\"name\": \"" +
                        escapeJson(rs.getString("name")) + "\",");
                    out.println("\"email\": \"" +
                        escapeJson(rs.getString("email")) + "\",");
                    out.println("\"role\": \"" +
                        escapeJson(rs.getString("role")) + "\",");
                    out.println("\"account_number\": \"" +
                        escapeJson(rs.getString("account_number")) + "\",");
                    out.println("\"balance\": " + balance.toPlainString() + ",");
                    out.println("\"status\": \"" +
                        escapeJson(rs.getString("status")) + "\"");
                    out.println("}");

                } else {

                    response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                    );

                    out.println("{\"error\":\"Account not found\"}");
                }
            }

        } catch (Exception e) {

            getServletContext().log("Customer servlet error", e);

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println(
                "{\"error\":\"Unable to retrieve customer information\"}"
            );
        }
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
