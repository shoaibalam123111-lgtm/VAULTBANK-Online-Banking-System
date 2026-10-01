/* =====================================
   VAULTBANK ONLINE BANKING SYSTEM
===================================== */

let users = JSON.parse(localStorage.getItem("vaultbank_users")) || [
    {
        id: 1,
        name: "VAULTBANK Admin",
        email: "admin@gmail.com",
        password: "admin123",
        role: "admin",
        balance: 0
    },
    {
        id: 2,
        name: "Customer",
        email: "customer@gmail.com",
        password: "customer123",
        role: "customer",
        balance: 50000
    }
];

let transactions =
    JSON.parse(localStorage.getItem("vaultbank_transactions")) || [];

let currentUser = null;


/* =====================================
   SAVE DATA
===================================== */

function saveData() {

    localStorage.setItem(
        "vaultbank_users",
        JSON.stringify(users)
    );

    localStorage.setItem(
        "vaultbank_transactions",
        JSON.stringify(transactions)
    );
}


/* =====================================
   LOGIN
===================================== */

function login() {

    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value;

    const role =
        document.getElementById("role").value;

    const message =
        document.getElementById("loginMessage");

    if (email === "" || password === "") {

        message.className = "message error";

        message.textContent =
            "Please enter email and password.";

        return;
    }

    fetch("/OnlineBanking/login", {

        method: "POST",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body:
            "email=" +
            encodeURIComponent(email) +
            "&password=" +
            encodeURIComponent(password)
    })

    .then(function(response) {

        return response.text();

    })

    .then(function(data) {

        if (data.includes("Login Successful!")) {

            message.className = "message";

            message.textContent =
                "Login Successful!";

            /*
             * Create the current user object.
             * The actual authentication is now
             * performed by Java Servlet + MySQL.
             */
            currentUser = users.find(function(u) {

                return u.email === email &&
                       u.role === role;

            });

            /*
             * If user is not present in localStorage,
             * create a basic user object so the existing
             * dashboard can continue to work.
             */
            if (!currentUser) {

                currentUser = {

                    id: 0,

                    name:
                        role === "admin"
                        ? "VAULTBANK Admin"
                        : "Customer",

                    email: email,

                    password: password,

                    role: role,

                    balance: 0
                };
            }

            document.getElementById("loginPage")
                .style.display = "none";


            /* =========================
               CUSTOMER LOGIN
            ========================= */

            if (role === "customer") {

                document.getElementById(
                    "customerDashboard"
                ).style.display = "block";

                updateCustomerData();

                showCustomerSection("dashboard");
            }


            /* =========================
               ADMIN LOGIN
            ========================= */

            else {

                document.getElementById(
                    "adminDashboard"
                ).style.display = "block";

                updateAdminData();

                showAdminSection("dashboard");
            }

        }

        else {

            message.className =
                "message error";

            message.textContent =
                "Invalid email or password.";
        }

    })

    .catch(function(error) {

        console.error(error);

        message.className =
            "message error";

        message.textContent =
            "Server connection error.";
    });
}


/* =====================================
   CUSTOMER DASHBOARD
===================================== */

function updateCustomerData() {

    if (!currentUser) return;

    document.getElementById("customerName")
        .textContent = currentUser.name;

    document.getElementById("customerBalance")
        .textContent =
        "₹" + currentUser.balance.toFixed(2);

    document.getElementById("accountHolder")
        .textContent = currentUser.name;

    document.getElementById("accountEmail")
        .textContent = currentUser.email;

    document.getElementById("accountBalance")
        .textContent =
        "₹" + currentUser.balance.toFixed(2);

    const count =
        transactions.filter(function(t) {

            return t.sender === currentUser.email ||
                   t.receiver === currentUser.email;

        }).length;

    document.getElementById(
        "customerTransactionCount"
    ).textContent = count;

    displayTransactions();
}


/* =====================================
   CUSTOMER NAVIGATION
===================================== */

function hideCustomerSections() {

    document.getElementById("customerHome")
        .classList.add("hidden");

    document.getElementById("accountSection")
        .classList.add("hidden");

    document.getElementById("depositSection")
        .classList.add("hidden");

    document.getElementById("withdrawSection")
        .classList.add("hidden");

    document.getElementById("transferSection")
        .classList.add("hidden");

    document.getElementById("transactionsSection")
        .classList.add("hidden");

    document.getElementById("servicesSection")
        .classList.add("hidden");

    document.getElementById("profileSection")
        .classList.add("hidden");
}


function showCustomerSection(section) {

    hideCustomerSections();

    if (section === "dashboard") {

        document.getElementById("customerHome")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Customer Dashboard";

        updateCustomerData();

    }

    else if (section === "account") {

        document.getElementById("accountSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Account Overview";

        updateCustomerData();

    }

    else if (section === "deposit") {

        document.getElementById("depositSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Deposit Money";

    }

    else if (section === "withdraw") {

        document.getElementById("withdrawSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Withdraw Money";

    }

    else if (section === "transfer") {

        document.getElementById("transferSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Send Money";

    }

    else if (section === "transactions") {

        document.getElementById("transactionsSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Transaction History";

        displayTransactions();

    }

    else if (section === "services") {

        document.getElementById("servicesSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Banking Services";

    }

    else if (section === "profile") {

        document.getElementById("profileSection")
            .classList.remove("hidden");

        document.getElementById("customerPageTitle")
            .textContent = "Profile Management";

        document.getElementById("profileName")
            .value = currentUser.name;

        document.getElementById("profileEmail")
            .value = currentUser.email;
    }
}


/* =====================================
   DEPOSIT
===================================== */

function depositMoney() {

    const input =
        document.getElementById("depositAmount");

    const amount =
        Number(input.value);

    if (!Number.isFinite(amount) || amount <= 0) {

        alert("Please enter a valid amount.");

        return;
    }

    currentUser.balance += amount;

    transactions.push({

        id: Date.now(),

        sender: "VAULTBANK",

        receiver: currentUser.email,

        amount: amount,

        type: "Deposit",

        date: new Date().toLocaleString()
    });

    saveData();

    input.value = "";

    alert(
        "₹" + amount.toFixed(2) +
        " deposited successfully!"
    );

    updateCustomerData();
}


/* =====================================
   WITHDRAW
===================================== */

function withdrawMoney() {

    const input =
        document.getElementById("withdrawAmount");

    const amount =
        Number(input.value);

    if (!Number.isFinite(amount) || amount <= 0) {

        alert("Please enter a valid amount.");

        return;
    }

    if (amount > currentUser.balance) {

        alert("Insufficient balance!");

        return;
    }

    currentUser.balance -= amount;

    transactions.push({

        id: Date.now(),

        sender: currentUser.email,

        receiver: "VAULTBANK",

        amount: amount,

        type: "Withdrawal",

        date: new Date().toLocaleString()
    });

    saveData();

    input.value = "";

    alert(
        "₹" + amount.toFixed(2) +
        " withdrawn successfully!"
    );

    updateCustomerData();
}


/* =====================================
   SEND MONEY
===================================== */

function sendMoney() {

    const receiverEmail =
        document.getElementById("receiverEmail")
            .value.trim();

    const amount =
        Number(
            document.getElementById("transferAmount")
                .value
        );

    if (receiverEmail === "") {

        alert("Enter receiver email.");

        return;
    }

    if (!Number.isFinite(amount) || amount <= 0) {

        alert("Enter a valid amount.");

        return;
    }

    if (receiverEmail === currentUser.email) {

        alert("You cannot send money to yourself.");

        return;
    }

    if (amount > currentUser.balance) {

        alert("Insufficient balance!");

        return;
    }

    const receiver =
        users.find(function(u) {

            return u.email === receiverEmail &&
                   u.role === "customer";

        });

    if (!receiver) {

        alert("Receiver account not found.");

        return;
    }

    currentUser.balance -= amount;

    receiver.balance += amount;

    transactions.push({

        id: Date.now(),

        sender: currentUser.email,

        receiver: receiver.email,

        amount: amount,

        type: "Money Transfer",

        date: new Date().toLocaleString()
    });

    saveData();

    document.getElementById("receiverEmail").value = "";

    document.getElementById("transferAmount").value = "";

    alert(
        "₹" + amount.toFixed(2) +
        " transferred successfully!"
    );

    updateCustomerData();
}


/* =====================================
   TRANSACTIONS
===================================== */

function displayTransactions() {

    if (!currentUser) return;

    const history =
        document.getElementById("transactionHistory");

    const recent =
        document.getElementById(
            "customerRecentTransactions"
        );

    const searchInput =
        document.getElementById("transactionSearch");

    const search =
        searchInput ?
        searchInput.value.toLowerCase() :
        "";

    let list =
        transactions.filter(function(t) {

            return t.sender === currentUser.email ||
                   t.receiver === currentUser.email;

        });

    if (search !== "") {

        list = list.filter(function(t) {

            return (
                t.type.toLowerCase().includes(search) ||
                t.sender.toLowerCase().includes(search) ||
                t.receiver.toLowerCase().includes(search)
            );

        });
    }

    list.reverse();

    if (history) {

        history.innerHTML = "";

        if (list.length === 0) {

            history.innerHTML =
                '<p class="empty">No transactions found.</p>';

        } else {

            list.forEach(function(t) {

                history.appendChild(
                    createTransactionElement(t)
                );

            });
        }
    }

    if (recent) {

        recent.innerHTML = "";

        const recentList = list.slice(0, 5);

        if (recentList.length === 0) {

            recent.innerHTML =
                '<p class="empty">No transactions available.</p>';

        } else {

            recentList.forEach(function(t) {

                recent.appendChild(
                    createTransactionElement(t)
                );

            });
        }
    }
}


function createTransactionElement(transaction) {

    const div =
        document.createElement("div");

    div.className = "transaction";

    const isCredit =
        transaction.receiver === currentUser.email;

    div.innerHTML = `

        <div class="transaction-info">

            <strong>
                ${transaction.type}
            </strong>

            <small>
                ${transaction.date}
            </small>

        </div>

        <strong class="${isCredit ? "credit" : "debit"}">

            ${isCredit ? "+" : "-"}
            ₹${transaction.amount.toFixed(2)}

        </strong>

    `;

    return div;
}


/* =====================================
   PROFILE
===================================== */

function updateProfile() {

    const name =
        document.getElementById("profileName")
            .value.trim();

    const email =
        document.getElementById("profileEmail")
            .value.trim();

    if (name === "" || email === "") {

        alert("Please fill all fields.");

        return;
    }

    const emailExists =
        users.some(function(u) {

            return u.email === email &&
                   u.id !== currentUser.id;

        });

    if (emailExists) {

        alert("This email is already registered.");

        return;
    }

    currentUser.name = name;
    currentUser.email = email;

    saveData();

    alert("Profile updated successfully!");

    updateCustomerData();
}


/* =====================================
   CHANGE PASSWORD
===================================== */

function changePassword() {

    const oldPassword =
        document.getElementById("oldPassword").value;

    const newPassword =
        document.getElementById("newPassword").value;

    if (oldPassword !== currentUser.password) {

        alert("Old password is incorrect.");

        return;
    }

    if (newPassword.length < 6) {

        alert(
            "New password must contain at least 6 characters."
        );

        return;
    }

    currentUser.password = newPassword;

    saveData();

    document.getElementById("oldPassword").value = "";
    document.getElementById("newPassword").value = "";

    alert("Password changed successfully!");
}


/* =====================================
   BANKING SERVICES
===================================== */

function applyService(service) {

    alert(
        service +
        " application selected.\n\n" +
        "VAULTBANK will process your request."
    );
}


/* =====================================
   ADMIN DATA
===================================== */

function updateAdminData() {

    document.getElementById("totalUsers")
        .textContent = users.length;

    document.getElementById("totalTransactions")
        .textContent = transactions.length;

    let totalBalance = 0;

    users.forEach(function(user) {

        if (user.role === "customer") {

            totalBalance += user.balance;

        }

    });

    document.getElementById("totalBalance")
        .textContent =
        "₹" + totalBalance.toFixed(2);

    document.getElementById("metricUsers")
        .textContent = users.length;

    document.getElementById("metricTransactions")
        .textContent = transactions.length;

    displayUsers();

    displayAllTransactions();
}


/* =====================================
   ADMIN NAVIGATION
===================================== */

function hideAdminSections() {

    document.getElementById("adminHome")
        .classList.add("hidden");

    document.getElementById("usersSection")
        .classList.add("hidden");

    document.getElementById("adminTransactionsSection")
        .classList.add("hidden");

    document.getElementById("settingsSection")
        .classList.add("hidden");

    document.getElementById("metricsSection")
        .classList.add("hidden");
}


function showAdminSection(section) {

    hideAdminSections();

    if (section === "dashboard") {

        document.getElementById("adminHome")
            .classList.remove("hidden");

        updateAdminData();

    }

    else if (section === "users") {

        document.getElementById("usersSection")
            .classList.remove("hidden");

        displayUsers();

    }

    else if (section === "transactions") {

        document.getElementById("adminTransactionsSection")
            .classList.remove("hidden");

        displayAllTransactions();

    }

    else if (section === "settings") {

        document.getElementById("settingsSection")
            .classList.remove("hidden");

    }

    else if (section === "metrics") {

        document.getElementById("metricsSection")
            .classList.remove("hidden");

        updateAdminData();

    }
}


/* =====================================
   ADMIN - USERS
===================================== */

function displayUsers() {

    const table =
        document.getElementById("userTableBody");

    if (!table) return;

    table.innerHTML = "";

    users.forEach(function(user) {

        const row =
            document.createElement("tr");

        row.innerHTML = `

            <td>${user.id}</td>

            <td>${user.name}</td>

            <td>${user.email}</td>

            <td>${user.role}</td>

            <td>₹${user.balance.toFixed(2)}</td>

            <td>

                ${
                    user.role === "admin"
                    ? "<span>Protected</span>"
                    : `
                    <button
                        class="danger-btn"
                        onclick="deleteUser(${user.id})">
                        Delete
                    </button>
                    `
                }

            </td>
        `;

        table.appendChild(row);
    });
}


/* =====================================
   ADD USER
===================================== */

function addUser() {

    const name =
        document.getElementById("newUserName")
            .value.trim();

    const email =
        document.getElementById("newUserEmail")
            .value.trim();

    const password =
        document.getElementById("newUserPassword")
            .value;

    const role =
        document.getElementById("newUserRole")
            .value;

    const balance =
        Number(
            document.getElementById("newUserBalance")
                .value
        );

    if (
        name === "" ||
        email === "" ||
        password === ""
    ) {

        alert("Please fill all required fields.");

        return;
    }

    if (password.length < 6) {

        alert("Password must contain at least 6 characters.");

        return;
    }

    if (
        users.some(function(user) {
            return user.email === email;
        })
    ) {

        alert("Email already exists!");

        return;
    }

    const newId =
        users.length === 0
        ? 1
        : Math.max(...users.map(u => u.id)) + 1;

    users.push({

        id: newId,

        name: name,

        email: email,

        password: password,

        role: role,

        balance:
            Number.isFinite(balance)
            ? balance
            : 0

    });

    saveData();

    document.getElementById("newUserName").value = "";
    document.getElementById("newUserEmail").value = "";
    document.getElementById("newUserPassword").value = "";
    document.getElementById("newUserBalance").value = "";

    alert("User created successfully!");

    updateAdminData();
}


/* =====================================
   DELETE USER
===================================== */

function deleteUser(id) {

    const user =
        users.find(function(u) {
            return u.id === id;
        });

    if (!user) return;

    if (user.role === "admin") {

        alert("Admin account cannot be deleted.");

        return;
    }

    const confirmDelete =
        confirm(
            "Are you sure you want to delete this user?"
        );

    if (!confirmDelete) return;

    users =
        users.filter(function(u) {
            return u.id !== id;
        });

    saveData();

    alert("User deleted successfully!");

    updateAdminData();
}


/* =====================================
   ADMIN TRANSACTIONS
===================================== */

function displayAllTransactions() {

    const container =
        document.getElementById("adminTransactions");

    if (!container) return;

    container.innerHTML = "";

    if (transactions.length === 0) {

        container.innerHTML =
            '<p class="empty">No transactions available.</p>';

        return;
    }

    [...transactions]
        .reverse()
        .forEach(function(transaction) {

            const div =
                document.createElement("div");

            div.className = "transaction";

            div.innerHTML = `

                <div class="transaction-info">

                    <strong>
                        ${transaction.type}
                    </strong>

                    <small>
                        From: ${transaction.sender}
                        <br>
                        To: ${transaction.receiver}
                        <br>
                        ${transaction.date}
                    </small>

                </div>

                <strong>
                    ₹${transaction.amount.toFixed(2)}
                </strong>

            `;

            container.appendChild(div);
        });
}


/* =====================================
   LOGOUT
===================================== */

function logout() {

    if (!confirm("Are you sure you want to logout?")) {
        return;
    }

    currentUser = null;

    document.getElementById("customerDashboard")
        .style.display = "none";

    document.getElementById("adminDashboard")
        .style.display = "none";

    document.getElementById("loginPage")
        .style.display = "flex";

    document.getElementById("email").value = "";
    document.getElementById("password").value = "";

    alert("Logged out successfully!");
}


/* =====================================
   ENTER KEY LOGIN
===================================== */

document.addEventListener("DOMContentLoaded", function() {

    document.getElementById("password")
        .addEventListener("keydown", function(event) {

            if (event.key === "Enter") {
                login();
            }

        });

});