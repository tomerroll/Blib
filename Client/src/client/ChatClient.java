
package client;

import ocsf.client.*;
import subscriber.*;
import common.*;
import logic.*;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import librarian.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The {@code ChatClient} class extends {@link AbstractClient} and is responsible
 * for handling communication between the client and the server.
 * It processes incoming messages from the server and sends messages from the client UI.
 */
public class ChatClient extends AbstractClient {

    /** The client UI interface for displaying messages. */
    ChatIF clientUI;

    /** A flag indicating whether the client is waiting for a response from the server. */
    public static boolean awaitResponse = false;

    /**
     * Constructs a new {@code ChatClient} instance.
     *
     * @param host     the server host address
     * @param port     the server port number
     * @param clientUI the client user interface
     * @throws IOException if an I/O error occurs when opening the connection
     */
    public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
        super(host, port);
        this.clientUI = clientUI;
    }

    /**
     * Handles incoming messages from the server and processes them according to the command received.
     *
     * @param msg the message object received from the server
     */
    public void handleMessageFromServer(Object msg) {
        if (msg instanceof ServerMessage) {
            ServerMessage serverMessage = (ServerMessage) msg;
            String command = serverMessage.getCommand();
            Object messageContent = serverMessage.getMsg();

            switch (command) {
                case "disconnect":
                    System.out.println("Client connection closed successfully.");
                    break;

                case "loadDM":
                    ArrayList<String[]> UserDM = (ArrayList<String[]>) messageContent;
                    DMController DMcontroller = DMController.getInstance();
                    if (DMcontroller != null) {
                        DMcontroller.loadDM(UserDM);
                    }
                    break;

                case "loadPersonalDetails":
                    String[] personalDetails = (String[]) messageContent;
                    PersonalDetailsController controller = PersonalDetailsController.getInstance();
                    if (controller != null) {
                        controller.loadPersonalDetails(personalDetails);
                    }
                    break;

                case "loadBooksForReservationTable":
                    ArrayList<String[]> BooksForReservationTable = (ArrayList<String[]>) messageContent;
                    ReservationController controller2 = ReservationController.getInstance();
                    if (controller2 != null) {
                        controller2.loadBooksForReservationTable(BooksForReservationTable);
                    }
                    break;

                case "loadActivityHistory":
                    ArrayList<String[]> UserActivityHistory = (ArrayList<String[]>) messageContent;
                    ActivityHistoryController controller3 = ActivityHistoryController.getInstance();
                    if (controller3 != null) {
                        controller3.loadActivityHistory(UserActivityHistory);
                    }
                    break;

                case "loadLoanForExtensionTable":
                    ArrayList<String[]> loansForExtensionTable = (ArrayList<String[]>) messageContent;
                    RequestExtensionController controller4 = RequestExtensionController.getInstance();
                    if (controller4 != null) {
                        controller4.loadLoanTableData(loansForExtensionTable);
                    }
                    break;

                case "loadMyBooks":
                    ArrayList<String[]> MyBooks = (ArrayList<String[]>) messageContent;
                    MyBooksController controller5 = MyBooksController.getInstance();
                    if (controller5 != null) {
                        controller5.loadMyBooks(MyBooks);
                    }
                    break;

                case "BookDetails":
                    String[][] bookDetails = (String[][]) messageContent;
                    Platform.runLater(() -> {
                        SearchBookController searchBookController = SearchBookController.getInstance();
                        searchBookController.closeGUI();
                        BookDetailsController bookDetailsController = BookDetailsController.getInstance();
                        if (bookDetailsController != null) {
                            bookDetailsController.loadBookDetails(bookDetails);
                        }
                    });
                    break;

                case "viewingCardReader":
                    String[] bookLoans = (String[]) messageContent;
                    ViewingCardReaderController controllerViewingCardReader = ViewingCardReaderController.getInstance();
                    if (controllerViewingCardReader != null) {
                        controllerViewingCardReader.loadBookLoans(bookLoans);
                    }
                    break;

                case "SubscriptionStatusReport":
                    String[] SubscriptionStatusReport = (String[]) messageContent;
                    MonthlyReportsController controllerMonthlyReports = MonthlyReportsController.getInstance();
                    if (controllerMonthlyReports != null) {
                        controllerMonthlyReports.loadSubscriptionStatusReport(SubscriptionStatusReport);
                    }
                    break;

                case "loadBookLoanTimesReport":
                    String[] BookLoanTimesReport = (String[]) messageContent;
                    MonthlyReportsController controllerMonthlyReports1 = MonthlyReportsController.getInstance();
                    if (controllerMonthlyReports1 != null) {
                        controllerMonthlyReports1.loadBookLoanTimesReport(BookLoanTimesReport);
                    }
                    break;

                case "ERROR":
                    String mess = (String) messageContent;
                    AlertHelper.showAlert(AlertType.ERROR, "Error", "An Error Occurred", mess);
                    break;

                case "SUCCESS":
                    String successMessage = (String) messageContent;
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", null, successMessage);
                    break;

                case "LogIn":
                    User user = (User) messageContent;
                    LoginController controllerlogin = LoginController.getInstance();
                    if (controllerlogin != null) {
                        controllerlogin.LogIn(user);
                    }
                    break;

                default:
                    System.out.println("Unknown command received: " + command);
                    break;
            }
        }
        awaitResponse = false;
    }

    /**
     * Handles messages sent from the client UI to the server.
     *
     * @param message the message object to be sent to the server
     */
    public void handleMessageFromClientUI(Object message) {
        try {
            openConnection();
            awaitResponse = true;
            sendToServer(message);
            while (awaitResponse) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientUI.display("Could not send message to server: Terminating client." + e);
            quit();
        }
    }

    /**
     * Terminates the client connection and exits the program.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
            // Handle exception if needed
        }
        System.exit(0);
    }
}
