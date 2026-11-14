
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/banking")
public class BankingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // Check login session
        if (session == null ||
            !(session.getAttribute("userId") instanceof Number)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Please login first.\"}"
            );
            return;
        }

        // Only customers can perform banking operations
        if (!"customer".equalsIgnoreCase(
                String.valueOf(session.getAttribute("role")))) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Access denied.\"}"
            );
            return;
        }

        int userId =
            ((Number) session.getAttribute("userId")).intValue();

        String action = request.getParameter("action");
        String amountText = request.getParameter("amount");

        try {

            if (action == null || amountText == null) {
                throw new IllegalArgumentException(
                    "Missing banking details."
                );
            }

            BigDecimal amount = new BigDecimal(amountText);

            switch (action.toLowerCase()) {

                case "deposit":

                    BankingService.deposit(userId, amount);

                    sendSuccess(response, "Deposit successful.");
                    break;

                case "withdraw":

                    BankingService.withdraw(userId, amount);

                    sendSuccess(response, "Withdrawal successful.");
                    break;

                case "transfer":

                    String receiverEmail =
                        request.getParameter("receiverEmail");

                    if (receiverEmail == null ||
                        receiverEmail.isBlank()) {

                        throw new IllegalArgumentException(
                            "Receiver email is required."
                        );
                    }

                    int receiverId = findReceiverId(receiverEmail);

                    BankingService.transfer(
                        userId, receiverId, amount
                    );

                    sendSuccess(response, "Transfer successful.");
                    break;

                default:

                    throw new IllegalArgumentException(
                        "Invalid banking operation."
                    );
            }

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            sendError(response, e.getMessage());

        } catch (SQLException e) {

            getServletContext().log(
                "Banking operation failed.", e
            );

            response.setStatus(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            sendError(response, "Banking operation could not be completed.");
        }
    }

    // Find receiver using email from database
    private int findReceiverId(String email) throws SQLException {

        String sql =
            "SELECT id FROM users WHERE email = ? AND role = 'customer'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new IllegalArgumentException(
            "Receiver account not found."
        );
    }

    private void sendSuccess(HttpServletResponse response,
                             String message) throws IOException {

        response.getWriter().write(
            "{\"success\":true,\"message\":\"" + message + "\"}"
        );
    }

    private void sendError(HttpServletResponse response,
                           String message) throws IOException {

        response.getWriter().write(
            "{\"success\":false,\"message\":\"" +
            message.replace("\\", "\\\\").replace("\"", "\\\"") +
            "\"}"
        );
    }
}
