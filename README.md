# 🏦 VAULTBANK – Online Banking System

A Java-based Online Banking System developed as a college project using **Java, JDBC, MySQL, Jakarta Servlets, and Apache Tomcat 11**.

VAULTBANK provides a web-based banking environment where customers can securely access their accounts, perform banking operations, and view transaction history. Administrators can manage customer accounts through an admin dashboard.

---

## 🚀 Features

### 👤 Customer Dashboard
- Customer Login and Authentication
- View Customer Information
- View Available Account Balance
- Deposit Money
- Withdraw Money
- Transfer Money to Other Customers
- View Transaction History

### 🔐 Security and Authentication
- BCrypt Password Hashing
- Session-Based Authentication
- Role-Based Access Control
- Server-Side Banking Operations
- Prepared SQL Statements
- Self-Transfer Prevention
- Database-Based Authentication

### 🛠️ Admin Dashboard
- Admin Login
- User Management
- Create Customer Accounts
- View Registered Users
- View Banking Transactions
- Monitor Banking System Information

---

## 💻 Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| JDBC | Database connectivity |
| MySQL 8 | Database management |
| Jakarta Servlets | Backend request handling |
| Apache Tomcat 11 | Web application server |
| HTML5 | Frontend structure |
| CSS3 | User interface design |
| JavaScript | Frontend interactions |
| BCrypt | Password hashing |
| VS Code | Development environment |

---

## 🏗️ System Architecture

The application follows a layered web architecture.

```text
          CUSTOMER / ADMIN
                 |
                 v
       HTML + CSS + JavaScript
                 |
                 v
         Jakarta Servlets
                 |
          +------+------+
          |             |
          v             v
    LoginServlet   BankingServlet
          |             |
          |        BankingService
          |             |
          +------+------+
                 |
                 v
              JDBC
                 |
                 v
             MySQL DB
```

---

## 📂 Project Structure

```text
OnlineBanking/
│
├── DBConnection.java
├── LoginServlet.java
├── CustomerServlet.java
├── BankingServlet.java
├── BankingService.java
├── PasswordUtil.java
├── Transaction.java
├── LegacyTransaction.java
├── Main.java
├── TestConnection.java
│
├── index.html
├── style.css
├── script.js
│
├── database.sql
├── README.md
├── .gitignore
│
├── .vscode/
│
└── lib/
    ├── jbcrypt-0.4.jar
    ├── jakarta.servlet-api-6.1.0.jar
    └── mysql-connector-j-26.7.0.jar
```

---

## 🗄️ Database

VAULTBANK uses MySQL for storing banking information.

The database manages:
- User information
- Customer account information
- Account balances
- Banking transactions
- Login credentials and user roles

Database setup script: `database.sql`

---

## ⚙️ Requirements

Install the following before running the project:

1. Java Development Kit (JDK 26 or compatible configured JDK)
2. MySQL Server 8
3. Apache Tomcat 11
4. MySQL Connector/J
5. Jakarta Servlet API 6.1
6. BCrypt library
7. Visual Studio Code (or another Java IDE)

---

## ▶️ How to Run

### Step 1: Clone the Repository

```bash
git clone https://github.com/shoaibalam123111-lgtm/VAULTBANK-Online-Banking-System.git
```

### Step 2: Open the Project

Open the downloaded project folder in VS Code.

### Step 3: Configure MySQL

1. Start MySQL Server.
2. Execute the `database.sql` script.
3. Configure your database connection.

Set the following environment variables:

```text
DB_USER=your_mysql_username
DB_PASSWORD=your_mysql_password
```

Do not upload actual database passwords to GitHub.

### Step 4: Configure Apache Tomcat

Configure Apache Tomcat 11 in your development environment.

Deploy the web application inside the Tomcat `webapps` directory, ensuring compiled classes and required runtime libraries are placed in the correct `WEB-INF` directories.

### Step 5: Start Tomcat

Start the Tomcat server.

### Step 6: Open VAULTBANK

Visit:

```text
http://localhost:8080/OnlineBanking/
```

---

## 🧪 Testing

The following application workflows have been manually tested:

| Test Case | Result |
|---|---|
| Customer Login | Passed |
| Admin Login | Passed |
| Deposit | Passed |
| Withdrawal | Passed |
| Customer-to-Customer Transfer | Passed |
| Self-Transfer Prevention | Passed |
| Admin User Creation | Passed |
| Transaction History | Passed |

---

## 🔒 Security Improvements

VAULTBANK includes several security-oriented implementations:

- BCrypt-based password hashing
- Server-side banking operations
- Session-based login
- Role-based access checks
- Prepared statements for database queries
- Input validation for banking operations
- Database transaction handling

Additional security testing and improvements are ongoing.

---

## 🚀 Future Improvements

- CSRF Protection
- Login Rate Limiting
- Improved Session Security
- Automated Unit and Integration Testing
- Email and OTP Verification
- PDF Transaction Statements
- Improved Audit Logging
- Mobile-Friendly Interface

---

## 👨‍💻 Project Information

**Project Name:** VAULTBANK – Online Banking System

**Project Type:** Java Web-Based Application

**Developed Using:** Java, JDBC, MySQL, Jakarta Servlets and Apache Tomcat

**Purpose:** College Academic Project

---

## 📌 Disclaimer

VAULTBANK is an educational banking simulation developed for academic purposes. It is not connected to real banking networks and must not be used to handle real customer funds or financial transactions.