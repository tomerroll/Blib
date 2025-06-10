# BLIB

Welcome to **BLIB** – a library management system.

## 📁 Project Structure

This project is divided into three main components:

- `CLIENT` – The client-side application
- `SERVER` – The server-side application
- `OCSF` – Open Communication Server Framework used for communication between client and server


## 🗃️ Database (SQL)

The system uses an SQL database to store and manage library data such as books, users, subscriptions, and borrowing history.

### Supported Features:
- User registration and login
- Book management (add, update, delete)
- Borrow and return operations
- Search and filter functions

> Make sure the database is up and running before starting the server.

### Configuration:
- Host: `localhost`
- Port: `3306` (default for MySQL)
- Database name: `blib`
- Username: `root`
- Password: *(your password)*

## 🚀 How to Run

1. Start your local MySQL server and ensure the `blib` database is created.
2. Run the main server application from the `SERVER` directory.
3. Launch the client from the `CLIENT` directory.
4. Make sure both client and server include the `OCSF` package in their classpaths.

## 🛠️ Technologies Used

- Java
- MySQL (or other SQL-based DB)
- JDBC
- OCSF (Open Communication Server Framework)

## 👥 Developers

- [@tomerroll](https://github.com/tomerroll)
- [@aradharush](https://github.com/aradharush)
- [@lidorbenhamo26](https://github.com/lidorbenhamo26)
- [@TomBiton](https://github.com/TomBiton)
- [@yahlirap](https://github.com/yahlirap)

## 📌 Notes

- You may need to edit the `DBConnector` class (or similar) with your local database credentials.
- Avoid changing the folder structure unless you also update the corresponding import paths and configuration.
