import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");

        String sql = "SELECT id, name, email, role, balance FROM users WHERE email = ?";

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("{");
                out.println("\"id\": " + rs.getInt("id") + ",");
                out.println("\"name\": \"" + rs.getString("name") + "\",");
                out.println("\"email\": \"" + rs.getString("email") + "\",");
                out.println("\"role\": \"" + rs.getString("role") + "\",");
                out.println("\"balance\": " + rs.getDouble("balance"));
                out.println("}");
            } else {
                out.println("{\"error\":\"User not found\"}");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}