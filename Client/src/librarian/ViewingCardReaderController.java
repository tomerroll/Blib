
package librarian;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import client.ClientUI;
import common.LoginController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import logic.AlertHelper;
import logic.BookLoan;
import logic.BookLoanHistory;
import logic.ClientMessage;

/**
 * Controller class for viewing card reader information.
 * Handles the display and management of book loans and user details.
 */
public class ViewingCardReaderController {

    private static ViewingCardReaderController instance;

    /**
     * Gets the singleton instance of the ViewingCardReaderController.
     *
     * @return the instance of ViewingCardReaderController
     */
    public static ViewingCardReaderController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        bookLoansTable.setPlaceholder(new Label("No Active Loans found."));
        loansHistoryTable.setPlaceholder(new Label("No Loans History found."));
        // Make Active Loans table editable for return date changes
        bookLoansTable.setEditable(true);
        returnDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        returnDateColumn.setOnEditCommit(event -> {
            String updatedReturnDate = event.getNewValue();
            BookLoan selectedBookLoan = event.getRowValue();

            // Validate return date format
            if (!isValidDate(updatedReturnDate)) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Invalid date.");
                bookLoansTable.refresh(); // Refresh table to prevent incorrect value from being saved
                return;
            }

            selectedBookLoan.setReturnDate(updatedReturnDate);
            updateReturnDateInDatabase(selectedBookLoan);
        });
    }

    /**
     * Validates the date format.
     *
     * @param dateStr the date string to validate
     * @return true if the date is valid, false otherwise
     */
    private boolean isValidDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(dateStr, formatter);
            return true; // Valid date
        } catch (DateTimeParseException e) {
            return false; // Invalid date
        }
    }

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label accountStatusLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button okButton, btnBack;

    @FXML
    private TableView<BookLoan> bookLoansTable;

    @FXML
    private TableColumn<BookLoan, String> bookIdColumn, loanDateColumn, returnDateColumn, bookNameColumn, librarianColumn, extendedAtColumn;

    @FXML
    private TableView<BookLoanHistory> loansHistoryTable;

    @FXML
    private TableColumn<BookLoanHistory, String> bookIdHistoryColumn, bookNameHistoryColumn, loanDateHistoryColumn, returnDateHistoryColumn, isLateColumn, otherColumn;

    /**
     * Handles the action when the OK button is clicked.
     * Validates the username and sends a request to view card reader information.
     */
    @FXML
    private void handleOkButtonAction() {
        String username = usernameTextField.getText().trim();
        if (username.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Username is required.");
            return;
        }
        ClientUI.chat.accept(new ClientMessage(username, "viewingCardReader"));
    }

    /**
     * Handles the action when the Back button is clicked.
     * Navigates back to the Librarian GUI.
     *
     * @param event the action event
     */
    @FXML
    private void Back(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/LibrarianGUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Librarian");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the book loans and user details.
     *
     * @param bookLoans the array of book loan details
     */
    public void loadBookLoans(String[] bookLoans) {
        new Thread(() -> {
            ObservableList<BookLoan> data = FXCollections.observableArrayList();
            ObservableList<BookLoanHistory> historyData = FXCollections.observableArrayList();

            boolean isHistory = false;
            boolean isUserDetails = false;

            for (String loan : bookLoans) {
                if ("Table2".equals(loan)) {
                    isHistory = true;
                    continue;
                }

                if ("Table3".equals(loan)) {
                    isUserDetails = true;
                    continue;
                }

                if (loan.contains("ERROR")) {
                    Platform.runLater(() -> AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", null, loan));
                    return;
                }

                if (isUserDetails) {
                    String[] userDetails = loan.split(", ");

                    Platform.runLater(() -> {
                        firstNameLabel.setText(userDetails[0]);
                        lastNameLabel.setText(userDetails[1]);
                        userIdLabel.setText(userDetails[2]);
                        accountStatusLabel.setText(userDetails[3]);
                        phoneNumberLabel.setText(userDetails[4]);
                        emailLabel.setText(userDetails[5]);
                    });

                    continue;
                }

                String[] details = loan.split(", ");
                try {
                    int bookId = Integer.parseInt(details[0].split(": ")[1]);
                    String loanDateStr = details[2].split(": ")[1];
                    String returnDateStr = details[3].split(": ")[1];
                    String bookName = details[1].split(": ")[1];

                    if (isHistory) {
                        String isLate = details[4].split(": ")[1];
                        String other = details[5].split(": ")[1];

                        BookLoanHistory historyLoan = new BookLoanHistory(bookId, bookName, loanDateStr, returnDateStr, isLate, other);
                        historyData.add(historyLoan);
                    } else {
                        String librarianUser = details[4].split(": ")[1];
                        String extendedAt = details[5].split(": ")[1].replace("T", " ");

                        BookLoan bookLoan = new BookLoan(bookId, loanDateStr, returnDateStr, bookName, librarianUser, extendedAt);
                        data.add(bookLoan);
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            Platform.runLater(() -> {
                // Set Active Loans Table
                bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
                loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
                returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
                bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
                librarianColumn.setCellValueFactory(new PropertyValueFactory<>("librarianUser"));
                extendedAtColumn.setCellValueFactory(new PropertyValueFactory<>("extendedAt"));
                bookLoansTable.setItems(data);

                // Set Loans History Table
                bookIdHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
                loanDateHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
                returnDateHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
                bookNameHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
                isLateColumn.setCellValueFactory(new PropertyValueFactory<>("isLate"));
                otherColumn.setCellValueFactory(new PropertyValueFactory<>("other"));

                loansHistoryTable.setItems(historyData);

                // Refresh tables to reflect changes
                bookLoansTable.refresh();
                loansHistoryTable.refresh();
            });
        }).start();
    }

    /**
     * Updates the return date of a book loan in the database.
     *
     * @param bookLoan the book loan to update
     */
    private void updateReturnDateInDatabase(BookLoan bookLoan) {
        if (bookLoan == null) return;

        String updatedReturnDate = bookLoan.getReturnDate();
        int bookId = bookLoan.getBookId();
        String username = usernameTextField.getText().trim();

        LoginController controller = LoginController.getInstance();
        if (controller == null || controller.user == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to get librarian information.");
            return;
        }

        String librarianUser = controller.user.getUsername();
        if (librarianUser == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", null, "Librarian user not found.");
            return;
        }

        String[] details = {String.valueOf(bookId), updatedReturnDate, username, librarianUser};
        ClientUI.chat.accept(new ClientMessage(details, "updateReturnDate"));

        bookLoan.setLibrarianUser(librarianUser);
        bookLoan.setExtendedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

        Platform.runLater(bookLoansTable::refresh);
    }
}
