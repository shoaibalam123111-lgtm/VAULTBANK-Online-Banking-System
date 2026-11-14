
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankingService {

    // Validate money amount
    private static BigDecimal validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero."
            );
        }

        try {
            amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                "Amount cannot have more than two decimal places."
            );
        }

        if (amount.precision() - amount.scale() > 10) {
            throw new IllegalArgumentException("Amount is too large.");
        }

        return amount;
    }

    // Deposit money
    public static void deposit(int userId, BigDecimal amount)
            throws SQLException {

        amount = validateAmount(amount);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                String lockSQL =
                    "SELECT balance, status FROM accounts " +
                    "WHERE user_id = ? FOR UPDATE";

                BigDecimal balance;

                try (PreparedStatement ps =
                        con.prepareStatement(lockSQL)) {

                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Account not found.");
                        }

                        if (!"ACTIVE".equals(rs.getString("status"))) {
                            throw new SQLException("Account is not active.");
                        }

                        balance = rs.getBigDecimal("balance");
                    }
                }

                String updateSQL =
                    "UPDATE accounts SET balance = balance + ? " +
                    "WHERE user_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(updateSQL)) {

                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }

                String insertSQL =
                    "INSERT INTO transactions " +
                    "(sender_id, receiver_id, amount, type) " +
                    "VALUES (NULL, ?, ?, 'DEPOSIT')";

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setInt(1, userId);
                    ps.setBigDecimal(2, amount);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {
                con.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(e.getMessage(), e);

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Withdraw money
    public static void withdraw(int userId, BigDecimal amount)
            throws SQLException {

        amount = validateAmount(amount);

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                String lockSQL =
                    "SELECT balance, status FROM accounts " +
                    "WHERE user_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(lockSQL)) {

                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Account not found.");
                        }

                        if (!"ACTIVE".equals(rs.getString("status"))) {
                            throw new SQLException("Account is not active.");
                        }

                        BigDecimal balance = rs.getBigDecimal("balance");

                        if (balance.compareTo(amount) < 0) {
                            throw new SQLException("Insufficient balance.");
                        }
                    }
                }

                String updateSQL =
                    "UPDATE accounts SET balance = balance - ? " +
                    "WHERE user_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(updateSQL)) {

                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }

                String insertSQL =
                    "INSERT INTO transactions " +
                    "(sender_id, receiver_id, amount, type) " +
                    "VALUES (?, NULL, ?, 'WITHDRAWAL')";

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setInt(1, userId);
                    ps.setBigDecimal(2, amount);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {
                con.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(e.getMessage(), e);

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Transfer money
    public static void transfer(int senderId, int receiverId,
                                BigDecimal amount)
            throws SQLException {

        amount = validateAmount(amount);

        if (senderId == receiverId) {
            throw new IllegalArgumentException(
                "Cannot transfer money to your own account."
            );
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                String lockSQL =
                    "SELECT user_id, balance, status FROM accounts " +
                    "WHERE user_id IN (?, ?) " +
                    "ORDER BY user_id FOR UPDATE";

                BigDecimal senderBalance = null;
                int foundAccounts = 0;

                try (PreparedStatement ps =
                        con.prepareStatement(lockSQL)) {

                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {

                            foundAccounts++;

                            int id = rs.getInt("user_id");

                            if (!"ACTIVE".equals(rs.getString("status"))) {
                                throw new SQLException(
                                    "One of the accounts is not active."
                                );
                            }

                            if (id == senderId) {
                                senderBalance =
                                    rs.getBigDecimal("balance");
                            }
                        }
                    }
                }

                if (foundAccounts != 2 || senderBalance == null) {
                    throw new SQLException(
                        "Sender or receiver account not found."
                    );
                }

                if (senderBalance.compareTo(amount) < 0) {
                    throw new SQLException("Insufficient balance.");
                }

                String debitSQL =
                    "UPDATE accounts SET balance = balance - ? " +
                    "WHERE user_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(debitSQL)) {

                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, senderId);
                    ps.executeUpdate();
                }

                String creditSQL =
                    "UPDATE accounts SET balance = balance + ? " +
                    "WHERE user_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(creditSQL)) {

                    ps.setBigDecimal(1, amount);
                    ps.setInt(2, receiverId);
                    ps.executeUpdate();
                }

                String insertSQL =
                    "INSERT INTO transactions " +
                    "(sender_id, receiver_id, amount, type) " +
                    "VALUES (?, ?, ?, 'TRANSFER')";

                try (PreparedStatement ps =
                        con.prepareStatement(insertSQL)) {

                    ps.setInt(1, senderId);
                    ps.setInt(2, receiverId);
                    ps.setBigDecimal(3, amount);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {
                con.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(e.getMessage(), e);

            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
