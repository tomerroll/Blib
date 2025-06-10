package Server;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gui.ServerLogController;
import javafx.application.Platform;
import logic.ClientMessage;
import logic.ServerMessage;
import logic.User;

/**
 * The EchoServer class extends the AbstractServer class and handles client-server communication.
 * It manages client connections, processes client messages, and interacts with the database.
 */
public class EchoServer extends AbstractServer {

    /** Static map to store connected clients using IP address as key and client details as value. */
    public static Map<String, String> connectedClients = new HashMap<>();

    /** Singleton instance for database connection. */
    private static Connection dbConnection;

    /**
     * Constructor to initialize the server with a specific port.
     *
     * @param port The port number on which the server will listen for connections.
     */
    public EchoServer(int port) {
        super(port);  // Calls the constructor of AbstractServer with the port
    }

    /**
     * Handles messages received from clients.
     *
     * @param msg    The message received from the client.
     * @param client The connection to the client.
     */
    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received: " + msg + " from " + client);

        if (msg instanceof ClientMessage) {
            ClientMessage clientMessage = (ClientMessage) msg;
            String command = clientMessage.getCommand();
            Object messageContent = clientMessage.getMsg();
            //switch cases for the accepted command.
            try (Connection conn = getDBConnection()) {
                switch (command) {
                    case "disconnect":
                        int userIdDisconnect = (int) messageContent;
                        removeOnlineUser(conn, userIdDisconnect);
                        clientDisconnected(client);
                        System.out.println("Client connection closed successfully.");
                        break;
                    case "getDMbyID":
                        int userIdForDM = (int) messageContent;
                        ArrayList<String[]> DMTable = getDMById(conn, userIdForDM);
                        client.sendToClient(new ServerMessage(DMTable, "loadDM"));
                        break;
                    case "getBookDetailsByDescription":
                        String[] BookDetailsByDescription = (String[]) messageContent;
                        String[][] bookByDescription = getBookDetailsByDescription(conn, BookDetailsByDescription);
                        if (bookByDescription != null) {
                            client.sendToClient(new ServerMessage(bookByDescription, "BookDetails"));
                        } else {
                            client.sendToClient(new ServerMessage("No suitable books were found", "ERROR"));
                            System.out.println("Book Details not found");
                        }
                        break;
                    case "getPersonalDetailsById":
                        int subscriberId = (int) messageContent;
                        String[] personalDetailsList = getPersonalDetailsById(conn, subscriberId);
                        client.sendToClient(new ServerMessage(personalDetailsList, "loadPersonalDetails"));
                        break;
                    case "getReservationBookTable":
                        int subscriberId2 = (int) messageContent;
                        ArrayList<String[]> booksForReservationTable = getReservationBookTable(conn, subscriberId2);
                        client.sendToClient(new ServerMessage(booksForReservationTable, "loadBooksForReservationTable"));
                        break;
                    case "getActivityHistorybyID":
                        int subscriberId3 = (int) messageContent;
                        ArrayList<String[]> activityHistoryTable = getActivityHistoryById(conn, subscriberId3);
                        client.sendToClient(new ServerMessage(activityHistoryTable, "loadActivityHistory"));
                        break;
                    case "getLoanTable":
                        int subscriberId4 = (int) messageContent;
                        ArrayList<String[]> loansForExtensionTable = getLoanTable(conn, subscriberId4);
                        client.sendToClient(new ServerMessage(loansForExtensionTable, "loadLoanForExtensionTable"));
                        break;
                    case "getAllBooks":
                        String[] BookDetailsForAllBooks = (String[]) messageContent;
                        String[][] allBooks = getAllBooks(conn, BookDetailsForAllBooks);
                        if (allBooks != null) {
                            client.sendToClient(new ServerMessage(allBooks, "BookDetails"));
                        } else {
                            client.sendToClient(new ServerMessage("No books in library", "ERROR"));
                            System.out.println("Book Details not found");
                        }
                        break;
                    case "getMyBooksID":
                        int subscriberId5 = (int) messageContent;
                        ArrayList<String[]> myBooksTable = getMyBooksByID(conn, subscriberId5);
                        client.sendToClient(new ServerMessage(myBooksTable, "loadMyBooks"));
                        break;
                    case "setExpirationDate":
                        String[] loanDetails = (String[]) messageContent; // [0] = user_id, [1] = book_id
                        setExpirationDate(conn, loanDetails[0], loanDetails[1], client);
                        client.sendToClient(new ServerMessage(getLoanTable(conn, Integer.parseInt(loanDetails[0])), "loadLoanForExtensionTable"));
                        break;
                    case "lost":
                        String[] lostDetails = (String[]) messageContent; // [0] = user_id, [1] = book_id
                        lostBook(conn, lostDetails[0], lostDetails[1], client);
                        break;
                    case "setReservation":
                        String[] reservationDetails = (String[]) messageContent; // [0] = id, [1] = bookTitle
                        setReservation(conn, reservationDetails[0], reservationDetails[1], client);
                        client.sendToClient(new ServerMessage(getReservationBookTable(conn, Integer.parseInt(reservationDetails[0])), "loadBooksForReservationTable"));
                        break;
                    case "CheckLoginDetails":
                        String[] loginDetailsList = (String[]) messageContent;
                        User user = CheckLoginDetails(conn, loginDetailsList);
                        if (user != null) {
                            if (user.getUserId() != -1) {
                                // User is found and is not online, proceed with login
                                clientConnected2(client);
                                client.sendToClient(new ServerMessage(user, "LogIn"));
                                System.out.println("User logged in successfully: " + user.getUsername());
                            } else {
                                // User is found but is online, cannot log in
                                client.sendToClient(new ServerMessage("User is already online.", "ERROR"));
                                System.out.println("User is already logged in: " + user.getUsername());
                            }
                        } else {
                            // User not found, invalid login credentials
                            client.sendToClient(new ServerMessage("Wrong Username or Password", "ERROR"));
                            System.out.println("User details (username and password) not found.");
                        }
                        break;
                    case "updatePersonalDetails":
                        String[] updatedDetails = (String[]) messageContent; // [0] = userId, [1] = firstName, [2] = lastName, ...
                        boolean isUpdated = updatePersonalDetails(conn, updatedDetails);
                        if (isUpdated) {
                            client.sendToClient(new ServerMessage("Personal details updated successfully", "SUCCESS"));
                        } else {
                            client.sendToClient(new ServerMessage("Failed to update personal details", "ERROR"));
                        }
                        break;
                    case "getBookDetailsByTitle":
                        String[] BookDetailsByTitle = (String[]) messageContent;
                        String[][] bookByTitle = getBookDetailsByTitle(conn, BookDetailsByTitle);
                        if (bookByTitle != null) {
                            client.sendToClient(new ServerMessage(bookByTitle, "BookDetails"));
                        } else {
                            client.sendToClient(new ServerMessage("Wrong Title", "ERROR"));
                            System.out.println("Book Details not found");
                        }
                        break;
                    case "getBookDetailsByGenre":
                        String[] BookDetailsByGenre = (String[]) messageContent;
                        String[][] bookByGenre = getBookDetailsByGenre(conn, BookDetailsByGenre);
                        if (bookByGenre != null) {
                            client.sendToClient(new ServerMessage(bookByGenre, "BookDetails"));
                        } else {
                            client.sendToClient(new ServerMessage("Wrong Genre", "ERROR"));
                            System.out.println("Book Details not found");
                        }
                        break;
                    case "register":
                        String[] subscriberDetails = (String[]) messageContent;
                        SubscriptionRegistration(conn, subscriberDetails, client);
                        break;
                    case "borrow":
                        String[] borrowDetails = (String[]) messageContent;
                        BorrowBook(conn, borrowDetails, client);
                        break;
                    case "return":
                        String[] returnDetails = (String[]) messageContent;
                        ReturnBook(conn, returnDetails, client);
                        break;
                    case "viewingCardReader":
                        String username = (String) messageContent;
                        String[] bookLoans = viewingCardReader(conn, username);
                        String[] loansHistory = getLoanHistory(conn, username);
                        String userDetails = getPersonalDetailsByUsername(conn, username);
                        List<String> combinedList = new ArrayList<>(Arrays.asList(bookLoans));
                        combinedList.add("Table2");
                        combinedList.addAll(Arrays.asList(loansHistory));
                        combinedList.add("Table3");
                        combinedList.add(userDetails);
                        String[] combinedArray = combinedList.toArray(new String[0]);
                        client.sendToClient(new ServerMessage(combinedArray, "viewingCardReader"));
                        break;
                    case "updateReturnDate":
                        String[] returnDateDetails = (String[]) messageContent;
                        updateReturnDate(conn, returnDateDetails);
                        client.sendToClient(new ServerMessage("Return date updated successfully", "SUCCESS"));
                        break;
                    case "getSubscriptionStatusReport":
                        int[] date = (int[]) messageContent;
                        getSubscriptionStatusReport(conn, client, date[0], date[1]);
                        break;
                    case "getBookLoanTimesReport":
                        getBookLoanTimesReport(conn, client);
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        client.sendToClient("Error: Unknown command");
                        break;
                }
                conn.close();
            } catch (SQLException | IOException e) {
                System.out.println("Error handling client message: " + e.getMessage());
                try {
                    client.sendToClient("Error: Server encountered an issue");
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        } else {
            System.out.println("Invalid message format");
            try {
                client.sendToClient("Error: Invalid message format");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when the server starts listening for connections.
     */
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
        Connection conn = getDBConnection();
        deleteOutdatedOrders(conn);
        updateSubscriptionStatuses(conn);
        sendDueDateReminders(conn);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the server stops listening for connections.
     */
    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    /**
     * Called when a client connects to the server.
     *
     * @param client The connection to the client.
     */
    protected void clientConnected2(ConnectionToClient client) {
        String clientIp = client.getInetAddress().getHostAddress();
        String clientDetails = client.getInetAddress().getHostName();
        System.out.println("Client connected: " + clientIp);
        connectedClients.put(clientIp, clientDetails);
        Platform.runLater(this::refreshLog);
    }

    /**
     * Called when a client disconnects from the server.
     *
     * @param client The connection to the client.
     */
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        String clientIp = client.getInetAddress().getHostAddress();
        System.out.println("Client disconnected: " + clientIp);
        connectedClients.remove(clientIp);
        Platform.runLater(this::refreshLog);
    }

    /**
     * Establishes a connection to the database.
     *
     * @return The database connection.
     */
    public static Connection getDBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/g7?serverTimezone=Asia/Jerusalem&useSSL=false", "root", "Aa123456");
            System.out.println("SQL connection succeeded");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error establishing database connection: " + e.getMessage());
        }
        return dbConnection;
    }

    /**
     * Refreshes the server log.
     */
    private void refreshLog() {
        ServerLogController controller = ServerLogController.getInstance();
        if (controller != null) {
            controller.updateLogArea();
        }
    }

    /**
     * Deletes outdated orders from the reservations table.
     *
     * @param conn The database connection.
     */
    public static void deleteOutdatedOrders(Connection conn) {
        String selectSql = "SELECT book_id FROM reservations " +
                "WHERE message_date IS NOT NULL " +
                "AND message_date < CURDATE() - INTERVAL 2 DAY";
        String countSql = "SELECT COUNT(*) FROM reservations WHERE book_id = ? AND message_date IS NULL";
        String updateAvailableCopiesSql = "UPDATE books SET available_copies = available_copies + ? WHERE book_id = ?";
        String getReservationsForBookSql = "SELECT user_id, reservation_date FROM reservations WHERE book_id = ? AND message_date IS NULL ORDER BY reservation_date ASC";
        String deleteSql = "DELETE FROM reservations " +
                "WHERE message_date IS NOT NULL " +
                "AND message_date < CURDATE() - INTERVAL 2 DAY";

        Map<Integer, Integer> deletedReservationsCount = new HashMap<>();

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement countStmt = conn.prepareStatement(countSql);
             PreparedStatement updateAvailableCopiesStmt = conn.prepareStatement(updateAvailableCopiesSql);
             PreparedStatement getReservationsForBookStmt = conn.prepareStatement(getReservationsForBookSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                deletedReservationsCount.put(bookId, deletedReservationsCount.getOrDefault(bookId, 0) + 1);
            }

            for (Map.Entry<Integer, Integer> entry : deletedReservationsCount.entrySet()) {
                int bookId = entry.getKey();
                int deletedReservationsCountForBook = entry.getValue();

                countStmt.setInt(1, bookId);
                ResultSet countRs = countStmt.executeQuery();
                int x = 0;
                if (countRs.next()) {
                    x = countRs.getInt(1);
                }

                if (x <= deletedReservationsCountForBook) {
                    int difference = deletedReservationsCountForBook - x;
                    updateAvailableCopiesStmt.setInt(1, difference);
                    updateAvailableCopiesStmt.setInt(2, bookId);
                    updateAvailableCopiesStmt.executeUpdate();

                    getReservationsForBookStmt.setInt(1, bookId);
                    ResultSet reservationsRs = getReservationsForBookStmt.executeQuery();
                    while (reservationsRs.next()) {
                        int userId = reservationsRs.getInt("user_id");
                        notify(conn, bookId, userId);
                    }
                } else {
                    int y = deletedReservationsCountForBook;
                    getReservationsForBookStmt.setInt(1, bookId);
                    ResultSet reservationsRs = getReservationsForBookStmt.executeQuery();
                    int counter = 0;
                    while (reservationsRs.next() && counter < y) {
                        int userId = reservationsRs.getInt("user_id");
                        notify(conn, bookId, userId);
                        counter++;
                    }
                }
            }

            int rowsDeleted = deleteStmt.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " outdated reservations.");
        } catch (SQLException e) {
            System.err.println("An error occurred while deleting outdated reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a notification to the user about the availability of a reserved book.
     *
     * @param connection The database connection.
     * @param bookId     The ID of the book.
     * @param userId     The ID of the user.
     */
    public static void notify(Connection connection, int bookId, int userId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = "SELECT u.first_name, u.email, b.title " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "JOIN books b ON r.book_id = b.book_id " +
                "WHERE r.book_id = ? AND r.user_id = ?";

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String toEmail = rs.getString("email");
                String bookTitle = rs.getString("title");

                String subject = "Dear " + firstName + ", Your Reserved Book is Now Available";
                String message = "Dear " + firstName + ",\n\n" +
                        "The book you reserved, \"" + bookTitle + "\", is now available for you to borrow. " +
                        "It will be kept reserved for you for the next 2 days.\n\n" +
                        "Please visit the library to collect your book.\n\n" +
                        "Thank you,\nBLib Team";

                boolean EmailSent = SendEmail.sendEmail(toEmail, subject, message);

                if (EmailSent) {
                    String updateQuery = "UPDATE reservations SET message_date = CURRENT_DATE WHERE book_id = ? AND user_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, bookId);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the subscription statuses of users.
     *
     * @param connection The database connection.
     */
    public static void updateSubscriptionStatuses(Connection connection) {
        PreparedStatement stmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        String selectQuery = "SELECT u.user_id, MAX(h.change_date) AS last_frozen_date " +
                "FROM users u " +
                "JOIN subscription_status_history h ON u.user_id = h.user_id " +
                "WHERE u.subscription_status = 'Frozen' AND h.new_status = 'Frozen' " +
                "GROUP BY u.user_id";

        String updateQuery = "UPDATE users SET subscription_status = 'Active' WHERE user_id = ?";

        try {
            stmt = connection.prepareStatement(selectQuery);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                Date lastFrozenDate = rs.getDate("last_frozen_date");

                if (lastFrozenDate != null) {
                    LocalDate frozenDate = lastFrozenDate.toLocalDate();
                    LocalDate currentDate = LocalDate.now();

                    if (ChronoUnit.DAYS.between(frozenDate, currentDate) > 30) {
                        updateStmt = connection.prepareStatement(updateQuery);
                        updateStmt.setInt(1, userId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends reminder emails for books due tomorrow.
     *
     * @param conn The database connection.
     */
    public static void sendDueDateReminders(Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PreparedStatement updateStmt = null;

        try {
            String query = "SELECT u.first_name, u.email, b.title, b.book_id, bl.due_date, bl.loan_id, bl.last_reminder_sent " +
                    "FROM book_loans bl " +
                    "JOIN users u ON bl.user_id = u.user_id " +
                    "JOIN books b ON bl.book_id = b.book_id " +
                    "WHERE bl.due_date = CURDATE() + INTERVAL 1 DAY " +
                    "AND (bl.last_reminder_sent IS NULL OR bl.last_reminder_sent <> CURDATE())";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String toEmail = rs.getString("email");
                String bookTitle = rs.getString("title");
                int bookId = rs.getInt("book_id");
                int loanId = rs.getInt("loan_id");

                String subject = "Dear " + firstName + ", Reminder: Book Due Tomorrow";
                String message = "Dear " + firstName + ",\n\n" +
                        "This is a reminder that the book you borrowed, \"" + bookTitle + "\" (Book ID: " + bookId + "), " +
                        "is due tomorrow. Please make sure to return it on time.\n\n" +
                        "Thank you,\nBLib Team";

                boolean EmailSent = SendEmail.sendEmail(toEmail, subject, message);
                if (EmailSent) {
                    String updateQuery = "UPDATE book_loans SET last_reminder_sent = CURDATE() WHERE loan_id = ?";
                    updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, loanId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching data from database:");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (updateStmt != null) updateStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies the user about the availability of a reserved book.
     *
     * @param connection The database connection.
     * @param bookId     The ID of the book.
     */
    public static void notifyReservationAvailability(Connection connection, int bookId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = "SELECT u.first_name, u.email, b.title, r.reservation_date, r.message_date, r.user_id " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "JOIN books b ON r.book_id = b.book_id " +
                "WHERE r.book_id = ? " +
                "ORDER BY r.reservation_date ASC " +
                "LIMIT 1";

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, bookId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String toEmail = rs.getString("email");
                String bookTitle = rs.getString("title");
                Date messageDate = rs.getDate("message_date");
                int userId = rs.getInt("user_id");

                if (messageDate == null) {
                    String subject = "Dear " + firstName + ", Your Reserved Book is Now Available";
                    String message = "Dear " + firstName + ",\n\n" +
                            "The book you reserved, \"" + bookTitle + "\", is now available for you to borrow. " +
                            "It will be kept reserved for you for the next 2 days.\n\n" +
                            "Please visit the library to collect your book.\n\n" +
                            "Thank you,\nBLib Team";

                    boolean EmailSent = SendEmail.sendEmail(toEmail, subject, message);

                    if (EmailSent) {
                        String updateQuery = "UPDATE reservations SET message_date = CURRENT_DATE WHERE book_id = ? AND user_id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, bookId);
                            updateStmt.setInt(2, userId);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes an online user from the online_users table.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user to remove.
     */
    public static void removeOnlineUser(Connection conn, int userId) {
        String query = "DELETE FROM online_users WHERE user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User with user_id " + userId + " has been removed from online_users.");
            } else {
                System.out.println("No user found with user_id " + userId + " in online_users.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while removing user from online_users: " + e.getMessage());
        }
    }

    /**
     * Marks a book as lost and updates the database accordingly.
     *
     * @param conn   The database connection.
     * @param username The username of the user who lost the book.
     * @param bookId The ID of the lost book.
     * @param client The connection to the client.
     * @throws IOException If an I/O error occurs.
     */
    public void lostBook(Connection conn, String username, String bookId, ConnectionToClient client) throws IOException {
        try {
            int bookIdInt = Integer.parseInt(bookId);
            String checkLoanQuery = "SELECT user_id FROM book_loans WHERE book_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?)";
            try (PreparedStatement checkLoanStmt = conn.prepareStatement(checkLoanQuery)) {
                checkLoanStmt.setInt(1, bookIdInt);
                checkLoanStmt.setString(2, username);

                try (ResultSet rs = checkLoanStmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String updateLoanQuery = "UPDATE book_loans SET lost = 1 WHERE book_id = ? AND user_id = ?";
                        try (PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanQuery)) {
                            updateLoanStmt.setInt(1, bookIdInt);
                            updateLoanStmt.setInt(2, userId);
                            updateLoanStmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Error updating book_loans table: " + e.getMessage());
                            String errorMessage = "Failed to update the loan status.";
                            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                            client.sendToClient(serverMessage);
                            return;
                        }

                        String deleteLoanQuery = "DELETE FROM book_loans WHERE book_id = ? AND user_id = ?";
                        try (PreparedStatement deleteLoanStmt = conn.prepareStatement(deleteLoanQuery)) {
                            deleteLoanStmt.setInt(1, bookIdInt);
                            deleteLoanStmt.setInt(2, userId);
                            deleteLoanStmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Error deleting from book_loans table: " + e.getMessage());
                            String errorMessage = "Failed to delete the loan record.";
                            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                            client.sendToClient(serverMessage);
                            return;
                        }

                        String updateBookQuery = "UPDATE books SET total_copies = total_copies - 1 WHERE book_id = ?";
                        try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery)) {
                            updateBookStmt.setInt(1, bookIdInt);
                            updateBookStmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Error updating books table: " + e.getMessage());
                            String errorMessage = "Failed to update the total copies of the book.";
                            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                            client.sendToClient(serverMessage);
                            return;
                        }

                        String updateUserQuery = "UPDATE users SET subscription_status = 'Frozen' WHERE user_id = ?";
                        try (PreparedStatement updateUserStmt = conn.prepareStatement(updateUserQuery)) {
                            updateUserStmt.setInt(1, userId);
                            updateUserStmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Error updating users table: " + e.getMessage());
                            String errorMessage = "Failed to freeze the user's subscription.";
                            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                            client.sendToClient(serverMessage);
                            return;
                        }

                        String checkReservationsQuery = "SELECT user_id, book_id, reservation_date FROM reservations WHERE book_id = ? ORDER BY reservation_date DESC LIMIT 1";
                        try (PreparedStatement checkReservationsStmt = conn.prepareStatement(checkReservationsQuery)) {
                            checkReservationsStmt.setInt(1, bookIdInt);
                            try (ResultSet resRs = checkReservationsStmt.executeQuery()) {
                                if (resRs.next()) {
                                    String deleteReservationQuery = "DELETE FROM reservations WHERE user_id = ? AND book_id = ?";
                                    try (PreparedStatement deleteReservationStmt = conn.prepareStatement(deleteReservationQuery)) {
                                        deleteReservationStmt.setInt(1, resRs.getInt("user_id"));
                                        deleteReservationStmt.setInt(2, bookIdInt);
                                        deleteReservationStmt.executeUpdate();
                                    } catch (SQLException e) {
                                        System.out.println("Error deleting from reservations table: " + e.getMessage());
                                        String errorMessage = "Failed to delete the latest reservation.";
                                        ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                                        client.sendToClient(serverMessage);
                                        return;
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            System.out.println("Error checking reservations table: " + e.getMessage());
                            String errorMessage = "Error checking reservations for the lost book.";
                            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                            client.sendToClient(serverMessage);
                            return;
                        }

                        String successMessage = "Book marked as lost, subscription frozen, and latest reservation deleted for book: " + bookId;
                        ServerMessage serverMessage = new ServerMessage(successMessage, "SUCCESS");
                        client.sendToClient(serverMessage);
                    } else {
                        String errorMessage = "No loan found for the book with ID: " + bookId + " and user: " + username;
                        ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                        client.sendToClient(serverMessage);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error checking loan in book_loans table: " + e.getMessage());
                String errorMessage = "Error checking loan status.";
                ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                client.sendToClient(serverMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An unexpected error occurred while processing the lost book.";
            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
            client.sendToClient(serverMessage);
        }
    }

    /**
     * Retrieves personal details of a user by their ID.
     *
     * @param con    The database connection.
     * @param userId The ID of the user.
     * @return An array of strings containing the user's personal details.
     */
    public String[] getPersonalDetailsById(Connection con, int userId) {
        String[] personalDetails = null;
        String query = "SELECT first_name, last_name, user_id, phone_number, email, subscription_status FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    personalDetails = new String[6];
                    personalDetails[0] = rs.getString("first_name");
                    personalDetails[1] = rs.getString("last_name");
                    personalDetails[2] = rs.getString("user_id");
                    personalDetails[3] = rs.getString("subscription_status");
                    personalDetails[4] = rs.getString("phone_number");
                    personalDetails[5] = rs.getString("email");
                } else {
                    System.out.println("No user found with user_id: " + userId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving personal details for user_id: " + userId);
            e.printStackTrace();
        }
        return personalDetails;
    }

    /**
     * Checks the login details of a user.
     *
     * @param con           The database connection.
     * @param loginDetails  An array containing the username and password hash.
     * @return A User object if the login details are correct, otherwise null.
     */
    public User CheckLoginDetails(Connection con, String[] loginDetails) {
        String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, loginDetails[0]);
            pstmt.setString(2, loginDetails[1]);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                int userId = rs.getInt("user_id");
                String checkOnlineQuery = "SELECT * FROM online_users WHERE user_id = ?";
                try (PreparedStatement checkOnlineStmt = con.prepareStatement(checkOnlineQuery)) {
                    checkOnlineStmt.setInt(1, userId);
                    ResultSet onlineRs = checkOnlineStmt.executeQuery();
                    if (onlineRs.next()) {
                        user.setUserId(-1);
                        user.setUsername(null);
                        user.setPasswordHash(null);
                        user.setFirstName(null);
                        user.setLastName(null);
                        user.setPhoneNumber(null);
                        user.setEmail(null);
                        user.setSubscriptionStatus(null);
                        user.setRole(null);
                    } else {
                        user.setUserId(userId);
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(rs.getString("password_hash"));
                        user.setFirstName(rs.getString("first_name"));
                        user.setLastName(rs.getString("last_name"));
                        user.setPhoneNumber(rs.getString("phone_number"));
                        user.setEmail(rs.getString("email"));
                        user.setSubscriptionStatus(rs.getString("subscription_status"));
                        user.setRole(rs.getString("role"));
                        String insertOnlineQuery = "INSERT INTO online_users (user_id) VALUES (?)";
                        try (PreparedStatement insertOnlineStmt = con.prepareStatement(insertOnlineQuery)) {
                            insertOnlineStmt.setInt(1, userId);
                            insertOnlineStmt.executeUpdate();
                        }
                    }
                }
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the activity history of a user by their ID.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return A list of activity history entries.
     */
    public ArrayList<String[]> getActivityHistoryById(Connection conn, int userId) {
        ArrayList<String[]> activityList = new ArrayList<>();
        String query = "SELECT transaction, transaction_time FROM activity_history WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String transaction = rs.getString("transaction");
                    String time = rs.getString("transaction_time");
                    activityList.add(new String[]{transaction, time});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityList;
    }

    /**
     * Retrieves the reservation book table for a user by their ID.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return A list of books available for reservation.
     */
    public ArrayList<String[]> getReservationBookTable(Connection conn, int userId) {
        ArrayList<String[]> books = new ArrayList<>();
        String query = "SELECT b.book_id, b.title, b.author, b.available_copies, b.total_copies, " +
                "COUNT(r.user_id) AS reservation_count " +
                "FROM books b " +
                "LEFT JOIN reservations r ON b.book_id = r.book_id " +
                "WHERE b.available_copies = 0 " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM reservations r2 " +
                "    WHERE r2.book_id = b.book_id AND r2.user_id = ? " +
                ") " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM book_loans bl " +
                "    WHERE bl.book_id = b.book_id AND bl.user_id = ? " +
                ") " +
                "GROUP BY b.book_id, b.title, b.author, b.available_copies, b.total_copies " +
                "HAVING COUNT(r.user_id) < b.total_copies;";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] bookData = new String[]{
                            rs.getString("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("available_copies"),
                            rs.getString("total_copies"),
                            rs.getString("reservation_count")
                    };
                    books.add(bookData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Sets a reservation for a book by a user.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param bookId The ID of the book.
     * @param client The connection to the client.
     * @throws IOException If an I/O error occurs.
     */
    public void setReservation(Connection conn, String userId, String bookId, ConnectionToClient client) throws IOException {
        try {
            ArrayList<String[]> availableBooks = getReservationBookTable(conn, Integer.parseInt(userId));
            boolean canReserve = false;
            for (String[] book : availableBooks) {
                if (book[0].equals(bookId)) {
                    canReserve = true;
                    break;
                }
            }
            if (canReserve) {
                String query = "INSERT INTO reservations (user_id, book_id, reservation_date) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, userId);
                    ps.setString(2, bookId);
                    LocalDateTime now = LocalDateTime.now();
                    ps.setString(3, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Reservation successfully added for user_id: " + userId + ", book_id: " + bookId);
                        String successMessage = "Reservation successful for book: " + bookId;
                        ServerMessage serverMessage = new ServerMessage(successMessage, "SUCCESS");
                        client.sendToClient(serverMessage);
                    } else {
                        System.out.println("Failed to add reservation for user_id: " + userId + ", book_id: " + bookId);
                        String failureMessage = "Failed to reserve the book: " + bookId;
                        ServerMessage serverMessage = new ServerMessage(failureMessage, "ERROR");
                        client.sendToClient(serverMessage);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    String errorMessage = "An error occurred while reserving the book: " + bookId;
                    ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                    client.sendToClient(serverMessage);
                }
            } else {
                System.out.println("The user cannot reserve this book. Either it's already reserved or not available.");
                String message = "The user cannot reserve this book. Either it's already reserved or not available.";
                ServerMessage serverMessage = new ServerMessage(message, "ERROR");
                client.sendToClient(serverMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "An unexpected error occurred while processing the reservation.";
            ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
            client.sendToClient(serverMessage);
        }
    }

    /**
     * Retrieves the loan table for a user by their ID.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return A list of loans for the user.
     */
    public ArrayList<String[]> getLoanTable(Connection conn, int userId) {
        ArrayList<String[]> loans = new ArrayList<>();
        String query = "SELECT b.book_id, b.title AS bookname, b.author, l.due_date AS time " +
                "FROM book_loans l " +
                "JOIN books b ON l.book_id = b.book_id " +
                "LEFT JOIN reservations r ON l.book_id = r.book_id " +
                "WHERE l.user_id = ? " +
                "AND l.due_date > CURDATE() " +
                "AND l.due_date <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
                "AND r.book_id IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookId = rs.getString("book_id");
                String author = rs.getString("author");
                String bookTitle = rs.getString("bookname");
                String expirationDate = rs.getString("time");
                loans.add(new String[]{bookId, author, bookTitle, expirationDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    /**
     * Sets the expiration date for a loan.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param bookId The ID of the book.
     * @param client The connection to the client.
     * @throws IOException If an I/O error occurs.
     */
    public void setExpirationDate(Connection conn, String userId, String bookId, ConnectionToClient client) throws IOException {
        ArrayList<String[]> activeLoans = getLoanTable(conn, Integer.parseInt(userId));
        boolean canExtend = false;
        for (String[] loan : activeLoans) {
            if (loan[0].equals(bookId)) {
                canExtend = true;
                break;
            }
        }
        if (canExtend) {
            String updateLoanQuery = "UPDATE book_loans SET due_date = DATE_ADD(due_date, INTERVAL 7 DAY), extended_at = NOW() WHERE book_id = ? AND user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateLoanQuery)) {
                ps.setString(1, bookId);
                ps.setString(2, userId);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Loan expiration date updated successfully for user_id: " + userId + ", book_id: " + bookId);
                    String successMessage = "Loan expiration date successfully extended for book: " + bookId;
                    ServerMessage serverMessage = new ServerMessage(successMessage, "SUCCESS");
                    client.sendToClient(serverMessage);
                } else {
                    System.out.println("Failed to update loan expiration date for user_id: " + userId + ", book_id: " + bookId);
                    String failureMessage = "Failed to extend the loan expiration date for book: " + bookId;
                    ServerMessage serverMessage = new ServerMessage(failureMessage, "ERROR");
                    client.sendToClient(serverMessage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                String errorMessage = "An error occurred while updating the loan expiration date for book: " + bookId;
                ServerMessage serverMessage = new ServerMessage(errorMessage, "ERROR");
                client.sendToClient(serverMessage);
            }
        } else {
            System.out.println("The user cannot extend the loan for this book. It may not be loaned or is not due for extension.");
            String message = "The user cannot extend the loan for this book. It may not be loaned or is not due for extension.";
            ServerMessage serverMessage = new ServerMessage(message, "ERROR");
            client.sendToClient(serverMessage);
        }
    }

    /**
     * Updates the personal details of a user.
     *
     * @param conn    The database connection.
     * @param details An array containing the updated details.
     * @return True if the update was successful, otherwise false.
     */
    private boolean updatePersonalDetails(Connection conn, String[] details) {
        String query = "UPDATE Users SET phone_number = ?, email = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, details[1]);
            pstmt.setString(2, details[2]);
            pstmt.setInt(3, Integer.parseInt(details[0]));
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the books borrowed by a user by their ID.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return A list of books borrowed by the user.
     */
    public ArrayList<String[]> getMyBooksByID(Connection conn, int userId) {
        ArrayList<String[]> MyBooks = new ArrayList<>();
        String query = "SELECT bl.book_id, b.title, b.author, bl.loan_date, bl.due_date " +
                "FROM book_loans bl " +
                "JOIN books b ON bl.book_id = b.book_id " +
                "WHERE bl.user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String bookID = rs.getString("book_id");
                    String bookTitle = rs.getString("title");
                    String bookAuthor = rs.getString("author");
                    String loanDate = rs.getString("loan_date");
                    String dueDate = rs.getString("due_date");
                    MyBooks.add(new String[]{bookID, bookTitle, bookAuthor, loanDate, dueDate});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MyBooks;
    }

    /**
     * Retrieves all books from the database.
     *
     * @param con          The database connection.
     * @param bookDetails  An array containing additional book details.
     * @return A 2D array of strings containing all books.
     */
    public String[][] getAllBooks(Connection con, String[] bookDetails) {
        List<String[]> books = new ArrayList<>();
        String bookQuery = "SELECT * FROM books";
        String reservationQuery = "SELECT COUNT(*) AS reservation_count FROM reservations WHERE book_id = ?";
        String dueDateQuery = "SELECT due_date \n" +
                "            FROM book_loans \n" +
                "            WHERE book_id = ? AND due_date > NOW() \n" +
                "            ORDER BY due_date ASC \n" +
                "            LIMIT ?";
        try (PreparedStatement bookStmt = con.prepareStatement(bookQuery)) {
            ResultSet bookRs = bookStmt.executeQuery();
            while (bookRs.next()) {
                String[] book = new String[9];
                int bookId = bookRs.getInt("book_id");
                int availableCopies = bookRs.getInt("available_copies");
                book[0] = bookRs.getString("title");
                book[1] = bookRs.getString("author");
                book[2] = bookRs.getString("genre");
                book[3] = bookRs.getString("description");
                book[4] = availableCopies > 0 ? "✅" : "❌";
                book[5] = bookRs.getString("total_copies");
                book[7] = bookRs.getString("location");
                book[8] = bookDetails[0];
                book[6] = "";
                if (availableCopies == 0) {
                    int reservationCount = 0;
                    try (PreparedStatement reservationStmt = con.prepareStatement(reservationQuery)) {
                        reservationStmt.setInt(1, bookId);
                        ResultSet reservationRs = reservationStmt.executeQuery();
                        if (reservationRs.next()) {
                            reservationCount = reservationRs.getInt("reservation_count");
                        }
                    }
                    try (PreparedStatement dueDateStmt = con.prepareStatement(dueDateQuery)) {
                        dueDateStmt.setInt(1, bookId);
                        dueDateStmt.setInt(2, reservationCount + 1);
                        ResultSet dueDateRs = dueDateStmt.executeQuery();
                        int count = 0;
                        while (dueDateRs.next()) {
                            count++;
                            if (count == reservationCount + 1) {
                                book[6] = dueDateRs.getString("due_date");
                                break;
                            }
                        }
                    }
                    if (book[6].equals(""))
                        book[6] = "No upcoming return date";
                }
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (books.isEmpty()) {
            return null;
        }
        return books.toArray(new String[0][0]);
    }

    /**
     * Retrieves book details by title.
     *
     * @param con          The database connection.
     * @param bookDetails  An array containing the book title and additional details.
     * @return A 2D array of strings containing the book details.
     */
    public String[][] getBookDetailsByTitle(Connection con, String[] bookDetails) {
        List<String[]> books = new ArrayList<>();
        String bookQuery = "SELECT * FROM books WHERE title LIKE ?";
        String reservationQuery = "SELECT COUNT(*) AS reservation_count FROM reservations WHERE book_id = ?";
        String dueDateQuery = "SELECT due_date \n" +
                "FROM book_loans \n" +
                "WHERE book_id = ? AND due_date > NOW() \n" +
                "ORDER BY due_date ASC \n" +
                "LIMIT ?";
        try (PreparedStatement bookStmt = con.prepareStatement(bookQuery)) {
            bookStmt.setString(1, "%" + bookDetails[0] + "%");
            ResultSet bookRs = bookStmt.executeQuery();
            while (bookRs.next()) {
                String[] book = new String[9];
                int bookId = bookRs.getInt("book_id");
                int availableCopies = bookRs.getInt("available_copies");
                book[0] = bookRs.getString("title");
                book[1] = bookRs.getString("author");
                book[2] = bookRs.getString("genre");
                book[3] = bookRs.getString("description");
                book[4] = availableCopies > 0 ? "✅" : "❌";
                book[5] = bookRs.getString("total_copies");
                book[7] = bookRs.getString("location");
                book[8] = bookDetails[1];
                book[6] = "";
                if (availableCopies == 0) {
                    int reservationCount = 0;
                    try (PreparedStatement reservationStmt = con.prepareStatement(reservationQuery)) {
                        reservationStmt.setInt(1, bookId);
                        ResultSet reservationRs = reservationStmt.executeQuery();
                        if (reservationRs.next()) {
                            reservationCount = reservationRs.getInt("reservation_count");
                        }
                    }
                    try (PreparedStatement dueDateStmt = con.prepareStatement(dueDateQuery)) {
                        dueDateStmt.setInt(1, bookId);
                        dueDateStmt.setInt(2, reservationCount + 1);
                        ResultSet dueDateRs = dueDateStmt.executeQuery();
                        int count = 0;
                        while (dueDateRs.next()) {
                            count++;
                            if (count == reservationCount + 1) {
                                book[6] = dueDateRs.getString("due_date");
                                break;
                            }
                        }
                    }
                    if (book[6].isEmpty())
                        book[6] = "No upcoming return date";
                }
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (books.isEmpty()) {
            return null;
        }
        return books.toArray(new String[0][0]);
    }

    /**
     * Retrieves book details by genre from the database.
     *
     * @param con          The database connection.
     * @param bookDetails  An array containing the genre and additional data.
     * @return A 2D String array containing book details, or null if no results are found.
     */
    public String[][] getBookDetailsByGenre(Connection con, String[] bookDetails) {
        List<String[]> books = new ArrayList<>();
        String bookQuery = "SELECT * FROM books WHERE genre = ?";
        String reservationQuery = "SELECT COUNT(*) AS reservation_count FROM reservations WHERE book_id = ?";
        String dueDateQuery = "SELECT due_date \n"
                + "            FROM book_loans \n"
                + "            WHERE book_id = ? AND due_date > NOW() \n"
                + "            ORDER BY due_date ASC \n"
                + "            LIMIT ?"; // Dynamic limit based on reservations
        try (PreparedStatement bookStmt = con.prepareStatement(bookQuery)) {
            bookStmt.setString(1, bookDetails[0]); // Set book genre
            ResultSet bookRs = bookStmt.executeQuery();
            while (bookRs.next()) {
                String[] book = new String[9];
                int bookId = bookRs.getInt("book_id");
                int availableCopies = bookRs.getInt("available_copies");
                // Basic book details
                book[0] = bookRs.getString("title");
                book[1] = bookRs.getString("author");
                book[2] = bookRs.getString("genre");
                book[3] = bookRs.getString("description");
                book[4] = availableCopies > 0 ? "✅" : "❌";
                book[5] = bookRs.getString("total_copies");
                book[7] = bookRs.getString("location");
                book[8] = bookDetails[1]; // Additional data from input
                // Default value for the closest return date
                book[6] = "";
                // Proceed with return date logic only if available copies are 0
                if (availableCopies == 0) {
                    // Step 1: Check reservations
                    int reservationCount = 0;
                    try (PreparedStatement reservationStmt = con.prepareStatement(reservationQuery)) {
                        reservationStmt.setInt(1, bookId);
                        ResultSet reservationRs = reservationStmt.executeQuery();
                        if (reservationRs.next()) {
                            reservationCount = reservationRs.getInt("reservation_count");
                        }
                    }
                    // Step 2: Get the closest due date considering reservations
                    try (PreparedStatement dueDateStmt = con.prepareStatement(dueDateQuery)) {
                        dueDateStmt.setInt(1, bookId);
                        dueDateStmt.setInt(2, reservationCount + 1); // Consider the n+1th closest due date
                        ResultSet dueDateRs = dueDateStmt.executeQuery();

                        int count = 0;
                        while (dueDateRs.next()) {
                            count++;
                            if (count == reservationCount + 1) {
                                book[6] = dueDateRs.getString("due_date");
                                break;
                            }
                        }
                    }
                    if (book[6] == "")
                        book[6] = "No upcoming return date";
                }
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (books.isEmpty()) {
            return null; // Return null if no results
        }
        // Convert List to 2D String array
        return books.toArray(new String[0][0]);
    }

    /**
     * Retrieves book details by description from the database.
     *
     * @param con          The database connection.
     * @param bookDetails  An array containing the description and additional data.
     * @return A 2D String array containing book details, or null if no results are found.
     */
    public String[][] getBookDetailsByDescription(Connection con, String[] bookDetails) {
        List<String[]> books = new ArrayList<>();
        String bookQuery = "SELECT * FROM books WHERE description LIKE ?";
        String reservationQuery = "SELECT COUNT(*) AS reservation_count FROM reservations WHERE book_id = ?";
        String dueDateQuery = " SELECT due_date \n"
                + "            FROM book_loans \n"
                + "            WHERE book_id = ? AND due_date > NOW() \n"
                + "            ORDER BY due_date ASC \n"
                + "            LIMIT ?"; // Dynamic limit based on reservations
        try (PreparedStatement bookStmt = con.prepareStatement(bookQuery)) {
            bookStmt.setString(1, "%" + bookDetails[0] + "%"); // Set book description
            ResultSet bookRs = bookStmt.executeQuery();
            while (bookRs.next()) {
                String[] book = new String[9];
                int bookId = bookRs.getInt("book_id");
                int availableCopies = bookRs.getInt("available_copies");
                // Basic book details
                book[0] = bookRs.getString("title");
                book[1] = bookRs.getString("author");
                book[2] = bookRs.getString("genre");
                book[3] = bookRs.getString("description");
                book[4] = availableCopies > 0 ? "✅" : "❌";
                book[5] = bookRs.getString("total_copies");
                book[7] = bookRs.getString("location");
                book[8] = bookDetails[1]; // Additional data from input
                // Default value for the closest return date
                book[6] = "";
                // Proceed with return date logic only if available copies are 0
                if (availableCopies == 0) {
                    // Step 1: Check reservations
                    int reservationCount = 0;
                    try (PreparedStatement reservationStmt = con.prepareStatement(reservationQuery)) {
                        reservationStmt.setInt(1, bookId);
                        ResultSet reservationRs = reservationStmt.executeQuery();
                        if (reservationRs.next()) {
                            reservationCount = reservationRs.getInt("reservation_count");
                        }
                    }
                    // Step 2: Get the closest due date considering reservations
                    try (PreparedStatement dueDateStmt = con.prepareStatement(dueDateQuery)) {
                        dueDateStmt.setInt(1, bookId);
                        dueDateStmt.setInt(2, reservationCount + 1); // Consider the n+1th closest due date
                        ResultSet dueDateRs = dueDateStmt.executeQuery();

                        int count = 0;
                        while (dueDateRs.next()) {
                            count++;
                            if (count == reservationCount + 1) {
                                book[6] = dueDateRs.getString("due_date");
                                break;
                            }
                        }
                    }
                    if (book[6] == "")
                        book[6] = "No upcoming return date";
                }
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (books.isEmpty()) {
            return null; // Return null if no results
        }
        // Convert List to 2D String array
        return books.toArray(new String[0][0]);
    }

    /**
     * Generates a report of book loan times.
     *
     * @param conn   The database connection.
     * @param client The client connection to send the report.
     */
    public void getBookLoanTimesReport(Connection conn, ConnectionToClient client) {
        String query = "SELECT loan_id, loan_date, due_date, return_date FROM loan_history";
        List<String> reportData = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String loanId = rs.getString("loan_id"); // Change from book_id to loan_id
                LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                LocalDate returnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : LocalDate.now();
                long totalDays;

                // Use isAfter and isEqual for date comparison
                if (returnDate.isAfter(dueDate) || returnDate.isEqual(dueDate)) {
                    totalDays = ChronoUnit.DAYS.between(loanDate, dueDate);
                } else {
                    totalDays = ChronoUnit.DAYS.between(loanDate, returnDate);
                }

                long overdueDays = returnDate.isAfter(dueDate) ? ChronoUnit.DAYS.between(dueDate, returnDate) : 0;

                reportData.add(loanId + ":" + totalDays + ":" + overdueDays); // Change to loan identifier
            }

            client.sendToClient(new ServerMessage(reportData.toArray(new String[0]), "loadBookLoanTimesReport"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates a subscription status report for a specific month and year.
     *
     * @param conn   The database connection.
     * @param client The client connection to send the report.
     * @param year   The year for the report.
     * @param month  The month for the report.
     */
    public void getSubscriptionStatusReport(Connection conn, ConnectionToClient client, int year, int month) {
        String query = "SELECT u.user_id, u.username, u.subscription_status, u.created_at, ssh.new_status, ssh.change_date " +
                "FROM users u " +
                "JOIN ( " +
                "    SELECT user_id, new_status, change_date " +
                "    FROM subscription_status_history " +
                "    WHERE YEAR(change_date) = ? AND MONTH(change_date) = ? " +
                "    AND (user_id, change_date) IN ( " +
                "        SELECT user_id, MAX(change_date) " +
                "        FROM subscription_status_history " +
                "        WHERE YEAR(change_date) = ? AND MONTH(change_date) = ? " +
                "        GROUP BY user_id " +
                "    ) " +
                ") ssh ON u.user_id = ssh.user_id";

        List<String> subscriptionStatusData = new ArrayList<>();
        int activeCount = 0;
        int frozenCount = 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the year and month parameters
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            stmt.setInt(4, month);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String subscriptionStatus = rs.getString("new_status");
                    Date changeDate = rs.getDate("change_date");

                    // Count active and frozen users
                    if ("active".equalsIgnoreCase(subscriptionStatus)) {
                        activeCount++;
                    } else if ("frozen".equalsIgnoreCase(subscriptionStatus)) {
                        frozenCount++;
                    }

                    // Add each entry as "userId:username:subscriptionStatus:changeDate"
                    subscriptionStatusData.add(userId + ":" + username + ":" + subscriptionStatus + ":" + changeDate);
                }

                // Add counts to the data
                subscriptionStatusData.add("Active Users: " + activeCount);
                subscriptionStatusData.add("Frozen Users: " + frozenCount);

                // Send the data back to the client
                client.sendToClient(new ServerMessage(subscriptionStatusData.toArray(new String[0]), "SubscriptionStatusReport"));

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                try {
                    client.sendToClient(new ServerMessage("Error retrieving subscription status report", "Error"));
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the return date of a book loan.
     *
     * @param con              The database connection.
     * @param returnDateDetails An array containing book ID, new due date, username, and librarian username.
     */
    public void updateReturnDate(Connection con, String[] returnDateDetails) {
        int bookId = Integer.parseInt(returnDateDetails[0]);
        String newDueDate = returnDateDetails[1];
        String username = returnDateDetails[2];
        String librarianUser = returnDateDetails[3];

        // SQL query to update the due date, librarian, and extended_at in the Book_Loans table
        String query = "UPDATE Book_Loans SET due_date = ?, librarian = ?, extended_at = CURRENT_TIMESTAMP WHERE book_id = ? AND username = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            // Set the parameters in the prepared statement
            stmt.setString(1, newDueDate);
            stmt.setString(2, librarianUser);
            stmt.setInt(3, bookId);
            stmt.setString(4, username);

            // Execute the query
            stmt.executeUpdate();
            System.out.println("Return date updated successfully");
        } catch (SQLException e) {
            System.out.println("Error updating return date");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves personal details of a user by their username.
     *
     * @param conn     The database connection.
     * @param username The username of the user.
     * @return A comma-separated string containing the user's personal details.
     */
    private String getPersonalDetailsByUsername(Connection conn, String username) {
        String personalDetails = "";

        // SQL query to fetch details for a specific user based on their username
        String query = "SELECT first_name, last_name, user_id, subscription_status, phone_number, email FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the username parameter in the prepared statement
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String userId = rs.getString("user_id");
                    String subscriptionStatus = rs.getString("subscription_status");
                    String phoneNumber = rs.getString("phone_number");
                    String email = rs.getString("email");

                    // Combine the details into a comma-separated string
                    personalDetails = String.join(", ", firstName, lastName, userId, subscriptionStatus, phoneNumber, email);
                } else {
                    System.out.println("No user found with username: " + username);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving personal details for username: " + username);
            e.printStackTrace();
        }

        return personalDetails;
    }

    /**
     * Retrieves book loan details for a specific user.
     *
     * @param con      The database connection.
     * @param username The username of the user.
     * @return An array of strings containing book loan details, or an error message if the username does not exist.
     */
    public String[] viewingCardReader(Connection con, String username) {
        List<String> bookLoanStrings = new ArrayList<>();

        // First, check if the username exists in the database
        String checkUsernameQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement checkStatement = con.prepareStatement(checkUsernameQuery)) {
            checkStatement.setString(1, username);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    // Username does not exist, return an error message
                    return new String[]{"ERROR: Username does not exist"};
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
            return new String[]{"ERROR: Database error occurred"};
        }

        // If username exists, fetch book loans
        String query = "SELECT bl.book_id, bl.loan_date, bl.due_date, b.title AS book_name, bl.librarian, bl.extended_at " +
                "FROM book_loans bl " +
                "JOIN books b ON bl.book_id = b.book_id " +
                "WHERE bl.username = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int bookId = resultSet.getInt("book_id");
                    LocalDate loanDate = resultSet.getDate("loan_date").toLocalDate();
                    LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
                    String bookName = resultSet.getString("book_name");
                    String librarian = resultSet.getString("librarian");
                    Timestamp extendedAt = resultSet.getTimestamp("extended_at");

                    String bookLoan = "Book ID: " + bookId +
                            ", Book Name: " + (bookName != null ? bookName : " ") +
                            ", Loan Date: " + loanDate +
                            ", Due Date: " + dueDate +
                            ", Librarian: " + (librarian != null ? librarian : " ") +
                            ", Extended At: " + (extendedAt != null ? extendedAt.toLocalDateTime() : " ");
                    bookLoanStrings.add(bookLoan);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching book loans for user: " + username);
            e.printStackTrace();
        }

        return bookLoanStrings.isEmpty() ? new String[]{"NO_LOANS"} : bookLoanStrings.toArray(new String[0]);
    }

    /**
     * Retrieves loan history for a specific user.
     *
     * @param con      The database connection.
     * @param username The username of the user.
     * @return An array of strings containing loan history details, or an error message if the username does not exist.
     */
    public String[] getLoanHistory(Connection con, String username) {
        List<String> loanHistoryStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Check if the username exists in the database
        String checkUsernameQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement checkStatement = con.prepareStatement(checkUsernameQuery)) {
            checkStatement.setString(1, username);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    // Username does not exist, return an error message
                    return new String[]{"ERROR: Username does not exist"};
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
            return new String[]{"ERROR: Database error occurred"};
        }

        // If username exists, fetch loan history
        String query = "SELECT lh.book_id, b.title AS book_name, lh.loan_date, lh.return_date, lh.is_late, lh.late_days " +
                "FROM loan_history lh " +
                "JOIN books b ON lh.book_id = b.book_id " +
                "WHERE lh.username = ?";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int bookId = resultSet.getInt("book_id");
                    String bookName = resultSet.getString("book_name");
                    LocalDate loanDate = resultSet.getDate("loan_date").toLocalDate();
                    LocalDate returnDate = resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null;
                    int isLate = resultSet.getInt("is_late");
                    int lateDays = resultSet.getInt("late_days");

                    String other = null;
                    if (isLate == 1 && lateDays < 7) {
                        other = "Late Return (" + lateDays + " days)";
                    }
                    if (isLate == 1 && lateDays >= 7) {
                        other = "Late Return -> Account Frozen";
                    } else if (returnDate == null) {
                        other = "Book was Lost -> Account Frozen";
                    }

                    String loanHistory = "Book ID: " + bookId +
                            ", Book Name: " + bookName +
                            ", Loan Date: " + loanDate +
                            ", Return Date: " + (returnDate != null ? returnDate : " ") +
                            ", Is Late: " + (returnDate == null ? " " : (isLate == 0 ? "✅" : "❌")) +

                            ", Other: " + (other != null ? other : " ");
                    loanHistoryStrings.add(loanHistory);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching loan history for user: " + username);
            e.printStackTrace();
        }

        return loanHistoryStrings.isEmpty() ? new String[]{"NO_LOANS_HISTORY"} : loanHistoryStrings.toArray(new String[0]);
    }

    /**
     * Registers a new subscriber in the database.
     *
     * @param con               The database connection.
     * @param subscriberDetails An array containing subscriber details.
     * @param client            The client connection to send the result.
     * @throws IOException If an I/O error occurs.
     */
    public void SubscriptionRegistration(Connection con, String[] subscriberDetails, ConnectionToClient client) throws IOException {
        // SQL query to check if the username already exists
        String checkUsernameQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";

        // SQL query to insert a new subscriber into the database
        String insertQuery = "INSERT INTO Users (username, password_hash, first_name, last_name, phone_number, email, role, subscription_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = con.prepareStatement(checkUsernameQuery);
             PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {

            // Check if the username already exists
            checkStmt.setString(1, subscriberDetails[0]); // Username
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Username already exists, send error message
                client.sendToClient(new ServerMessage("Username already exists. Please choose a different username.", "ERROR"));
                System.out.println("Username already exists: " + subscriberDetails[0]);
                return;
            }

            // Set the parameters for the insert query
            insertStmt.setString(1, subscriberDetails[0]); // Username
            insertStmt.setString(2, subscriberDetails[1]); // Password hash
            insertStmt.setString(3, subscriberDetails[2]); // First name
            insertStmt.setString(4, subscriberDetails[3]); // Last name
            insertStmt.setString(5, subscriberDetails[4]); // Phone number
            insertStmt.setString(6, subscriberDetails[5]); // Email
            insertStmt.setString(7, subscriberDetails[6]); // Role
            insertStmt.setString(8, subscriberDetails[7]); // Subscription status

            // Execute the insert query
            insertStmt.executeUpdate();
            client.sendToClient(new ServerMessage("New subscriber registered successfully.", "SUCCESS"));
            System.out.println("New subscriber registered successfully: " + subscriberDetails[0]);

        } catch (SQLException e) {
            client.sendToClient(new ServerMessage("Error registering new subscriber: " + e.getMessage(), "ERROR"));
            System.out.println("Error registering new subscriber");
            e.printStackTrace();
        }
    }

    /**
     * Handles the borrowing of a book by a user.
     *
     * @param con           The database connection.
     * @param borrowDetails An array containing borrowing details.
     * @param client        The client connection to send the result.
     * @throws IOException If an I/O error occurs.
     */
    public void BorrowBook(Connection con, String[] borrowDetails, ConnectionToClient client) throws IOException {
        String username = borrowDetails[0];
        int bookId = Integer.parseInt(borrowDetails[1]);
        String loanDate = borrowDetails[2];
        String dueDate = borrowDetails[3];

        // SQL queries
        String checkUserQuery = "SELECT user_id, subscription_status FROM Users WHERE username = ? AND subscription_status != 'Frozen'";
        String checkReservationQuery = "SELECT message_date FROM reservations WHERE book_id = ? AND user_id = (SELECT user_id FROM Users WHERE username = ?)";
        String checkActiveLoanQuery = "SELECT COUNT(*) FROM book_loans WHERE username = ? AND book_id = ?";
        String updateBookQuery = "UPDATE Books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0";
        String insertLoanQuery = "INSERT INTO book_loans (username, book_id, loan_date, due_date, extended_at, librarian, user_id, last_reminder_sent) VALUES (?, ?, ?, ?, NULL, NULL, ?, NULL)";
        String deleteReservationQuery = "DELETE FROM reservations WHERE book_id = ? AND user_id = (SELECT user_id FROM Users WHERE username = ?)";

        try (PreparedStatement checkUserStmt = con.prepareStatement(checkUserQuery);
             PreparedStatement checkReservationStmt = con.prepareStatement(checkReservationQuery);
             PreparedStatement checkActiveLoanStmt = con.prepareStatement(checkActiveLoanQuery);
             PreparedStatement updateBookStmt = con.prepareStatement(updateBookQuery);
             PreparedStatement insertLoanStmt = con.prepareStatement(insertLoanQuery);
             PreparedStatement deleteReservationStmt = con.prepareStatement(deleteReservationQuery)) {

            // Check if the user is not frozen
            checkUserStmt.setString(1, username);
            ResultSet rsUser = checkUserStmt.executeQuery();

            if (rsUser.next()) {
                int userId = rsUser.getInt("user_id");

                // Check if there is a reservation for the book by the user
                checkReservationStmt.setInt(1, bookId);
                checkReservationStmt.setString(2, username);
                ResultSet rsReservation = checkReservationStmt.executeQuery();

                boolean hasReservation = false;
                boolean receivedMessage = false;

                if (rsReservation.next()) {
                    hasReservation = true;
                    receivedMessage = rsReservation.getDate("message_date") != null;
                }

                // Check if the user already has an active loan for the book
                checkActiveLoanStmt.setString(1, username);
                checkActiveLoanStmt.setInt(2, bookId);
                ResultSet rsLoan = checkActiveLoanStmt.executeQuery();

                if (rsLoan.next() && rsLoan.getInt(1) > 0) {
                    client.sendToClient(new ServerMessage("You already have an active loan for this book. Please return it before borrowing again.", "ERROR"));
                    System.out.println("User already has an active loan for this book.");
                    return;
                }

                // Logic for borrowing
                if (hasReservation) {
                    if (receivedMessage) {
                        // Allow borrowing, keep available_copies = 0
                        insertLoanStmt.setString(1, username);
                        insertLoanStmt.setInt(2, bookId);
                        insertLoanStmt.setString(3, loanDate);
                        insertLoanStmt.setString(4, dueDate);
                        insertLoanStmt.setInt(5, userId);

                        insertLoanStmt.executeUpdate();

                        // Delete reservation after loan is created
                        deleteReservationStmt.setInt(1, bookId);
                        deleteReservationStmt.setString(2, username);
                        deleteReservationStmt.executeUpdate();

                        client.sendToClient(new ServerMessage("Book borrowed successfully", "SUCCESS"));
                        System.out.println("Book borrowed successfully (with reservation)");
                    } else {
                        client.sendToClient(new ServerMessage("You cannot borrow the book now, please wait for a notification.", "ERROR"));
                        System.out.println("Reservation exists but no message sent. Cannot borrow.");
                    }
                } else {
                    // If no reservation, check available copies
                    updateBookStmt.setInt(1, bookId);
                    int rowsAffected = updateBookStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Insert a new loan record
                        insertLoanStmt.setString(1, username);
                        insertLoanStmt.setInt(2, bookId);
                        insertLoanStmt.setString(3, loanDate);
                        insertLoanStmt.setString(4, dueDate);
                        insertLoanStmt.setInt(5, userId);

                        insertLoanStmt.executeUpdate();
                        client.sendToClient(new ServerMessage("Book borrowed successfully", "SUCCESS"));
                        System.out.println("Book borrowed successfully");
                    } else {
                        client.sendToClient(new ServerMessage("No available copies for the book with ID: " + bookId, "ERROR"));
                        System.out.println("No available copies for the book with ID: " + bookId);
                    }
                }
            } else {
                client.sendToClient(new ServerMessage("User is Frozen or does not exist", "ERROR"));
                System.out.println("User is frozen or does not exist");
            }
        } catch (SQLException e) {
            client.sendToClient(new ServerMessage("Error borrowing book: " + e.getMessage(), "ERROR"));
            System.out.println("Error borrowing book");
            e.printStackTrace();
        }
    }

    /**
     * Handles the return of a book by a user.
     *
     * @param conn          The database connection.
     * @param returnDetails An array containing return details.
     * @param client        The client connection to send the result.
     * @throws IOException If an I/O error occurs.
     */
    public void ReturnBook(Connection conn, String[] returnDetails, ConnectionToClient client) throws IOException {
        String username = returnDetails[0];
        int bookId = Integer.parseInt(returnDetails[1]);

        // SQL queries
        String checkUserQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";
        String checkBookQuery = "SELECT COUNT(*) FROM Books WHERE book_id = ?";
        String getDueDateQuery = "SELECT due_date FROM Book_Loans WHERE username = ? AND book_id = ?";
        String checkReservationQuery = "SELECT user_id FROM reservations WHERE book_id = ?";
        String updateBookQuery = "UPDATE Books SET available_copies = available_copies + 1 WHERE book_id = ?";
        String deleteLoanQuery = "DELETE FROM Book_Loans WHERE username = ? AND book_id = ?";
        String updateUserStatusQuery = "UPDATE Users SET subscription_status = 'Frozen' WHERE username = ?";

        try (PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement checkBookStmt = conn.prepareStatement(checkBookQuery);
             PreparedStatement getDueDateStmt = conn.prepareStatement(getDueDateQuery);
             PreparedStatement checkReservationStmt = conn.prepareStatement(checkReservationQuery);
             PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
             PreparedStatement deleteLoanStmt = conn.prepareStatement(deleteLoanQuery);
             PreparedStatement updateUserStatusStmt = conn.prepareStatement(updateUserStatusQuery)) {

            // Check if the username exists
            checkUserStmt.setString(1, username);
            ResultSet rsUser = checkUserStmt.executeQuery();
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                client.sendToClient(new ServerMessage("Username does not exist.", "ERROR"));
                System.out.println("Username does not exist: " + username);
                return;
            }

            // Check if the book exists
            checkBookStmt.setInt(1, bookId);
            ResultSet rsBook = checkBookStmt.executeQuery();
            if (rsBook.next() && rsBook.getInt(1) == 0) {
                client.sendToClient(new ServerMessage("Book with ID " + bookId + " does not exist.", "ERROR"));
                System.out.println("Book does not exist with ID: " + bookId);
                return;
            }

            // Get due date
            getDueDateStmt.setString(1, username);
            getDueDateStmt.setInt(2, bookId);
            ResultSet rs = getDueDateStmt.executeQuery();

            if (rs.next()) {
                Date dueDate = rs.getDate("due_date");
                Date currentDate = new Date(System.currentTimeMillis());

                // Check if the book is overdue by 7 or more days
                long diffInDays = (currentDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);

                if (diffInDays >= 7) {
                    updateUserStatusStmt.setString(1, username);
                    updateUserStatusStmt.executeUpdate();
                    System.out.println("User's subscription status updated to 'Frozen'");
                }

                // Check if there is a reservation for the book
                checkReservationStmt.setInt(1, bookId);
                ResultSet rsReservation = checkReservationStmt.executeQuery();

                if (rsReservation.next()) {
                    int reservedUserId = rsReservation.getInt("user_id");
                    client.sendToClient(new ServerMessage("The reserved book is now available for user ID: " + reservedUserId, "INFO"));
                    System.out.println("Reservation exists for book ID: " + bookId + ". Available copies remain 0.");
                } else {
                    // Update book availability if no reservation exists
                    updateBookStmt.setInt(1, bookId);
                    int rowsAffected = updateBookStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Book availability updated. Book ID: " + bookId);
                    } else {
                        System.out.println("Failed to update availability for book ID: " + bookId);
                    }
                }

                // Delete loan record
                deleteLoanStmt.setString(1, username);
                deleteLoanStmt.setInt(2, bookId);
                deleteLoanStmt.executeUpdate();
                client.sendToClient(new ServerMessage("Book returned successfully", "SUCCESS"));
                System.out.println("Book returned successfully");
                notifyReservationAvailability(conn, bookId); // Send an email to the user who reserved this book.
            } else {
                client.sendToClient(new ServerMessage("No loan found for user: " + username + " with book ID: " + bookId, "ERROR"));
                System.out.println("No loan found for user: " + username + " with book ID: " + bookId);
            }
        } catch (SQLException | IOException e) {
            client.sendToClient(new ServerMessage("Error returning book: " + e.getMessage(), "ERROR"));
            System.out.println("Error returning book");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves direct messages (DMs) for a specific user by their user ID.
     *
     * @param conn   The database connection.
     * @param userId The user ID of the user.
     * @return A list of arrays containing message and date.
     */
    public ArrayList<String[]> getDMById(Connection conn, int userId) {
        ArrayList<String[]> DMList = new ArrayList<>();
        String query = "SELECT message, date FROM message";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String message = rs.getString("message");
                    String date = rs.getString("date");
                    DMList.add(new String[]{message, date});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally log or handle errors here
        }
        return DMList;
    }
    
}
