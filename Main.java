import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

// ================= USER CLASS =================
class User {
    int id;
    String name;
    String email;
    String password;
    String role;
    double balance;

    User(int id, String name, String email, String password,
         String role, double balance) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.balance = balance;
    }
}

// ================= TRANSACTION CLASS =================
class Transaction {
    int id;
    String sender;
    String receiver;
    double amount;
    String type;

    Transaction(int id, String sender, String receiver,
                double amount, String type) {

        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
    }

    void display() {

        System.out.println("----------------------------------------");
        System.out.println("Transaction ID : " + id);
        System.out.println("Type           : " + type);
        System.out.println("Sender         : " + sender);
        System.out.println("Receiver       : " + receiver);
        System.out.println("Amount         : Rs. " + amount);
    }
}

// ================= MAIN CLASS =================
public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Transaction> transactions = new ArrayList<>();

    static int nextTransactionId = 1;

    // ================= MAIN =================
    public static void main(String[] args) {

        loadUsersFromDatabase();

        while (true) {

            System.out.println("\n========================================");
            System.out.println("          WELCOME TO VAULTBANK");
            System.out.println("       ONLINE BANKING SYSTEM");
            System.out.println("========================================");

            System.out.println("1. Login");
            System.out.println("2. Exit");

            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:
                    login();
                    break;

                case 2:
                    System.out.println("\nThank you for using VAULTBANK.");
                    System.out.println("Have a great day!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= LOAD USERS FROM MYSQL =================
    static void loadUsersFromDatabase() {

        users.clear();

        String sql = "SELECT id, name, email, password, role, balance FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDouble("balance")
                );

                users.add(user);
            }

            System.out.println("Users loaded from MySQL successfully.");

        } catch (Exception e) {

            System.out.println("Error loading users from MySQL.");
            e.printStackTrace();
        }
    }

    // ================= LOGIN =================
    static void login() {

        System.out.println("\n========== VAULTBANK LOGIN ==========");

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User loggedInUser = null;

        String sql =
                "SELECT id, name, email, password, role, balance " +
                "FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                loggedInUser = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDouble("balance")
                );
            }

        } catch (Exception e) {

            System.out.println("Database error during login.");
            e.printStackTrace();
            return;
        }

        if (loggedInUser == null) {

            System.out.println("\nInvalid email or password!");
            return;
        }

        System.out.println("\nLogin Successful!");
        System.out.println("Welcome, " + loggedInUser.name);

        if (loggedInUser.role.equalsIgnoreCase("admin")) {

            adminDashboard();

        } else {

            customerDashboard(loggedInUser);
        }
    }

    // ================= CUSTOMER DASHBOARD =================
    static void customerDashboard(User customer) {

        while (true) {

            refreshUser(customer);

            System.out.println("\n========================================");
            System.out.println("        VAULTBANK CUSTOMER DASHBOARD");
            System.out.println("========================================");

            System.out.println("Welcome, " + customer.name);
            System.out.println("Account Balance: Rs. " + customer.balance);

            System.out.println("\n1. Account Overview");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Send Money");
            System.out.println("5. Transaction History");
            System.out.println("6. Banking Services");
            System.out.println("7. Profile Management");
            System.out.println("8. Logout");

            System.out.print("\nEnter your choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:
                    accountOverview(customer);
                    break;

                case 2:
                    depositMoney(customer);
                    break;

                case 3:
                    withdrawMoney(customer);
                    break;

                case 4:
                    sendMoney(customer);
                    break;

                case 5:
                    transactionHistory(customer);
                    break;

                case 6:
                    bankingServices();
                    break;

                case 7:
                    profileManagement(customer);
                    break;

                case 8:
                    System.out.println("\nLogged out successfully.");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= REFRESH USER =================
    static void refreshUser(User customer) {

        String sql =
                "SELECT name, email, password, role, balance " +
                "FROM users WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customer.id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                customer.name = rs.getString("name");
                customer.email = rs.getString("email");
                customer.password = rs.getString("password");
                customer.role = rs.getString("role");
                customer.balance = rs.getDouble("balance");
            }

        } catch (Exception e) {

            System.out.println("Unable to refresh user data.");
        }
    }

    // ================= ACCOUNT OVERVIEW =================
    static void accountOverview(User customer) {

        refreshUser(customer);

        System.out.println("\n========== ACCOUNT OVERVIEW ==========");

        System.out.println("Account ID : " + customer.id);
        System.out.println("Name       : " + customer.name);
        System.out.println("Email      : " + customer.email);
        System.out.println("Role       : " + customer.role);
        System.out.println("Balance    : Rs. " + customer.balance);
    }

    // ================= DEPOSIT =================
    static void depositMoney(User customer) {

        System.out.println("\n========== DEPOSIT MONEY ==========");

        System.out.print("Enter amount: Rs. ");
        double amount = readDouble();

        if (amount <= 0) {

            System.out.println("Amount must be greater than zero.");
            return;
        }

        String sql =
                "UPDATE users SET balance = balance + ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, customer.id);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                customer.balance += amount;

                transactions.add(
                        new Transaction(
                                nextTransactionId++,
                                "VAULTBANK",
                                customer.email,
                                amount,
                                "Deposit"
                        )
                );

                System.out.println("\nDeposit successful!");
                System.out.println("Amount Deposited: Rs. " + amount);
                System.out.println("New Balance: Rs. " + customer.balance);

            } else {

                System.out.println("Deposit failed.");
            }

        } catch (Exception e) {

            System.out.println("Database error during deposit.");
            e.printStackTrace();
        }
    }

    // ================= WITHDRAW =================
    static void withdrawMoney(User customer) {

        refreshUser(customer);

        System.out.println("\n========== WITHDRAW MONEY ==========");

        System.out.print("Enter amount: Rs. ");
        double amount = readDouble();

        if (amount <= 0) {

            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > customer.balance) {

            System.out.println("Insufficient balance!");
            return;
        }

        String sql =
                "UPDATE users SET balance = balance - ? " +
                "WHERE id = ? AND balance >= ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setInt(2, customer.id);
            ps.setDouble(3, amount);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                customer.balance -= amount;

                transactions.add(
                        new Transaction(
                                nextTransactionId++,
                                customer.email,
                                "VAULTBANK",
                                amount,
                                "Withdrawal"
                        )
                );

                System.out.println("\nWithdrawal successful!");
                System.out.println("Amount Withdrawn: Rs. " + amount);
                System.out.println("Remaining Balance: Rs. " + customer.balance);

            } else {

                System.out.println("Withdrawal failed.");
            }

        } catch (Exception e) {

            System.out.println("Database error during withdrawal.");
            e.printStackTrace();
        }
    }

    // ================= SEND MONEY =================
    static void sendMoney(User sender) {

        refreshUser(sender);

        System.out.println("\n========== SEND MONEY ==========");

        System.out.print("Enter receiver email: ");
        String receiverEmail = sc.nextLine();

        if (receiverEmail.equalsIgnoreCase(sender.email)) {

            System.out.println("You cannot send money to yourself.");
            return;
        }

        User receiver = null;

        String findSql =
                "SELECT id, name, email, password, role, balance " +
                "FROM users WHERE email = ? AND role = 'customer'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(findSql)) {

            ps.setString(1, receiverEmail);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                receiver = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDouble("balance")
                );
            }

        } catch (Exception e) {

            System.out.println("Database error finding receiver.");
            e.printStackTrace();
            return;
        }

        if (receiver == null) {

            System.out.println("Receiver account not found!");
            return;
        }

        System.out.print("Enter amount: Rs. ");
        double amount = readDouble();

        if (amount <= 0) {

            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > sender.balance) {

            System.out.println("Insufficient balance!");
            return;
        }

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            String withdrawSql =
                    "UPDATE users SET balance = balance - ? " +
                    "WHERE id = ? AND balance >= ?";

            try (PreparedStatement ps = con.prepareStatement(withdrawSql)) {

                ps.setDouble(1, amount);
                ps.setInt(2, sender.id);
                ps.setDouble(3, amount);

                int rows = ps.executeUpdate();

                if (rows == 0) {

                    con.rollback();

                    System.out.println("Transfer failed. Insufficient balance.");
                    return;
                }
            }

            String depositSql =
                    "UPDATE users SET balance = balance + ? WHERE id = ?";

            try (PreparedStatement ps = con.prepareStatement(depositSql)) {

                ps.setDouble(1, amount);
                ps.setInt(2, receiver.id);

                ps.executeUpdate();
            }

            con.commit();

            sender.balance -= amount;
            receiver.balance += amount;

            transactions.add(
                    new Transaction(
                            nextTransactionId++,
                            sender.email,
                            receiver.email,
                            amount,
                            "Money Transfer"
                    )
            );

            System.out.println("\n================================");
            System.out.println("      TRANSACTION SUCCESSFUL");
            System.out.println("================================");

            System.out.println("Sender   : " + sender.email);
            System.out.println("Receiver : " + receiver.email);
            System.out.println("Amount   : Rs. " + amount);
            System.out.println("Balance  : Rs. " + sender.balance);

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ignored) {
            }

            System.out.println("Transfer failed.");
            e.printStackTrace();

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    // ================= TRANSACTION HISTORY =================
    static void transactionHistory(User customer) {

        System.out.println("\n========== TRANSACTION HISTORY ==========");

        boolean found = false;

        for (Transaction transaction : transactions) {

            if (transaction.sender.equalsIgnoreCase(customer.email)
                    || transaction.receiver.equalsIgnoreCase(customer.email)) {

                transaction.display();
                found = true;
            }
        }

        if (!found) {

            System.out.println("No transactions found.");
        }
    }

    // ================= BANKING SERVICES =================
    static void bankingServices() {

        while (true) {

            System.out.println("\n========== VAULTBANK SERVICES ==========");

            System.out.println("1. Personal Loan");
            System.out.println("2. Home Loan");
            System.out.println("3. Investment");
            System.out.println("4. Credit Card");
            System.out.println("5. Back");

            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:
                    System.out.println("\nPersonal Loan");
                    System.out.println("Interest Rate: 10.5%");
                    System.out.println("Loan service available at VAULTBANK.");
                    break;

                case 2:
                    System.out.println("\nHome Loan");
                    System.out.println("Interest Rate: 8.5%");
                    System.out.println("Home loan service available.");
                    break;

                case 3:
                    System.out.println("\nInvestment");
                    System.out.println("Investment plans are available.");
                    break;

                case 4:
                    System.out.println("\nCredit Card");
                    System.out.println("VAULTBANK Credit Card service available.");
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= PROFILE MANAGEMENT =================
    static void profileManagement(User customer) {

        while (true) {

            System.out.println("\n========== PROFILE MANAGEMENT ==========");

            System.out.println("1. View Profile");
            System.out.println("2. Change Name");
            System.out.println("3. Change Password");
            System.out.println("4. Back");

            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:

                    refreshUser(customer);

                    System.out.println("\nName  : " + customer.name);
                    System.out.println("Email : " + customer.email);

                    break;

                case 2:

                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine();

                    String nameSql =
                            "UPDATE users SET name = ? WHERE id = ?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(nameSql)) {

                        ps.setString(1, newName);
                        ps.setInt(2, customer.id);

                        ps.executeUpdate();

                        customer.name = newName;

                        System.out.println("Name updated successfully!");

                    } catch (Exception e) {

                        System.out.println("Unable to update name.");
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    System.out.print("Enter new password: ");
                    String newPassword = sc.nextLine();

                    String passwordSql =
                            "UPDATE users SET password = ? WHERE id = ?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps =
                                 con.prepareStatement(passwordSql)) {

                        ps.setString(1, newPassword);
                        ps.setInt(2, customer.id);

                        ps.executeUpdate();

                        customer.password = newPassword;

                        System.out.println("Password updated successfully!");

                    } catch (Exception e) {

                        System.out.println("Unable to update password.");
                        e.printStackTrace();
                    }

                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= ADMIN DASHBOARD =================
    static void adminDashboard() {

        while (true) {

            loadUsersFromDatabase();

            System.out.println("\n========================================");
            System.out.println("          VAULTBANK ADMIN PANEL");
            System.out.println("========================================");

            System.out.println("1. User Management");
            System.out.println("2. Transaction Monitoring");
            System.out.println("3. System Settings");
            System.out.println("4. Operational Metrics");
            System.out.println("5. Logout");

            System.out.print("\nEnter your choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:
                    userManagement();
                    break;

                case 2:
                    transactionMonitoring();
                    break;

                case 3:
                    systemSettings();
                    break;

                case 4:
                    operationalMetrics();
                    break;

                case 5:
                    System.out.println("\nAdmin logged out.");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= USER MANAGEMENT =================
    static void userManagement() {

        while (true) {

            System.out.println("\n========== USER MANAGEMENT ==========");

            System.out.println("1. View All Users");
            System.out.println("2. Add User");
            System.out.println("3. Delete User");
            System.out.println("4. Back");

            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {

                case 1:
                    viewUsers();
                    break;

                case 2:
                    addUser();
                    break;

                case 3:
                    deleteUser();
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= VIEW USERS =================
    static void viewUsers() {

        loadUsersFromDatabase();

        System.out.println("\n========== ALL USERS ==========");

        for (User user : users) {

            System.out.println("----------------------------------------");
            System.out.println("ID       : " + user.id);
            System.out.println("Name     : " + user.name);
            System.out.println("Email    : " + user.email);
            System.out.println("Role     : " + user.role);
            System.out.println("Balance  : Rs. " + user.balance);
        }
    }

    // ================= ADD USER =================
    static void addUser() {

        System.out.println("\n========== ADD NEW USER ==========");

        System.out.print("Enter name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        String checkSql =
                "SELECT id FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check =
                     con.prepareStatement(checkSql)) {

            check.setString(1, email);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                System.out.println("Email already exists!");
                return;
            }

        } catch (Exception e) {

            System.out.println("Error checking email.");
            e.printStackTrace();
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("1. Customer");
        System.out.println("2. Admin");

        System.out.print("Enter choice: ");
        int roleChoice = readInt();

        String role;

        if (roleChoice == 2) {
            role = "admin";
        } else {
            role = "customer";
        }

        System.out.print("Enter initial balance: Rs. ");
        double balance = readDouble();

        String insertSql =
                "INSERT INTO users (name, email, password, role, balance) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(insertSql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setDouble(5, balance);

            ps.executeUpdate();

            System.out.println("\nUser created successfully!");

        } catch (Exception e) {

            System.out.println("Unable to create user.");
            e.printStackTrace();
        }
    }

    // ================= DELETE USER =================
    static void deleteUser() {

        System.out.println("\n========== DELETE USER ==========");

        System.out.print("Enter user email: ");
        String email = sc.nextLine();

        String findSql =
                "SELECT role FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement find =
                     con.prepareStatement(findSql)) {

            find.setString(1, email);

            ResultSet rs = find.executeQuery();

            if (!rs.next()) {

                System.out.println("User not found!");
                return;
            }

            String role = rs.getString("role");

            if (role.equalsIgnoreCase("admin")) {

                System.out.println("Admin account cannot be deleted.");
                return;
            }

        } catch (Exception e) {

            System.out.println("Error checking user.");
            e.printStackTrace();
            return;
        }

        String deleteSql =
                "DELETE FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(deleteSql)) {

            ps.setString(1, email);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                System.out.println("User deleted successfully!");

            } else {

                System.out.println("User not found!");
            }

        } catch (Exception e) {

            System.out.println("Unable to delete user.");
            e.printStackTrace();
        }
    }

    // ================= TRANSACTION MONITORING =================
    static void transactionMonitoring() {

        System.out.println("\n========== TRANSACTION MONITORING ==========");

        if (transactions.isEmpty()) {

            System.out.println("No transactions available.");
            return;
        }

        for (Transaction transaction : transactions) {

            transaction.display();
        }
    }

    // ================= SYSTEM SETTINGS =================
    static void systemSettings() {

        System.out.println("\n========== VAULTBANK SYSTEM SETTINGS ==========");

        System.out.println("Bank Name       : VAULTBANK");
        System.out.println("System Status   : Online");
        System.out.println("Security        : Enabled");
        System.out.println("Transaction     : Enabled");
        System.out.println("User Management : Enabled");

        System.out.println("\nSystem settings loaded successfully.");
    }

    // ================= OPERATIONAL METRICS =================
    static void operationalMetrics() {

        loadUsersFromDatabase();

        int customerCount = 0;
        int adminCount = 0;

        double totalBalance = 0;

        for (User user : users) {

            if (user.role.equalsIgnoreCase("customer")) {

                customerCount++;
                totalBalance += user.balance;
            }

            if (user.role.equalsIgnoreCase("admin")) {

                adminCount++;
            }
        }

        System.out.println(
                "\n========== VAULTBANK OPERATIONAL METRICS =========="
        );

        System.out.println("Total Users           : " + users.size());
        System.out.println("Total Customers       : " + customerCount);
        System.out.println("Total Admins          : " + adminCount);
        System.out.println("Total Transactions    : " + transactions.size());
        System.out.println(
                "Total Customer Balance: Rs. " + totalBalance
        );
    }

    // ================= INPUT METHODS =================
    static int readInt() {

        while (true) {

            try {

                int value = Integer.parseInt(sc.nextLine());
                return value;

            } catch (Exception e) {

                System.out.print("Please enter a valid number: ");
            }
        }
    }

    static double readDouble() {

        while (true) {

            try {

                double value = Double.parseDouble(sc.nextLine());
                return value;

            } catch (Exception e) {

                System.out.print("Please enter a valid amount: ");
            }
        }
    }
}