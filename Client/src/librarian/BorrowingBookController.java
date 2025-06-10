
package librarian;

import javafx.scene.control.Alert;
import java.io.IOException;
import java.time.LocalDate;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ClientMessage;
import logic.AlertHelper; // Import the utility class

/**
 * Controller class for managing the borrowing book view.
 * Handles the input of user and book details and sends a borrowing request to the server.
 */
public class BorrowingBookController {

    @FXML
    private TextField username; // TextField for entering the username

    @FXML
    private TextField bookId; // TextField for entering the book ID

    @FXML
    private DatePicker borrowdate; // DatePicker for selecting the borrowing date

    @FXML
    private DatePicker returndate; // DatePicker for selecting the return date

    @FXML
    private Button submitButton; // Button to submit the borrowing request

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    /**
     * Initializes the controller class.
     * Sets up listeners and default values.
     */
    @FXML
    private void initialize() {
        // Listener for borrowdate to automatically set returndate
        borrowdate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                returndate.setValue(newValue.plusWeeks(2)); // Set returndate to 2 weeks after borrowdate
            }
        });
    }

    /**
     * Handles the action when the submit button is clicked.
     * Validates the input fields and sends a borrowing request to the server.
     */
    @FXML
    private void handleSubmitButtonAction() {
        String user = username.getText();
        String book = bookId.getText();
        LocalDate borrowDate = borrowdate.getValue();
        LocalDate returnDate = returndate.getValue();

        // Validate fields are not empty
        if (user == null || user.trim().isEmpty() ||
            book == null || book.trim().isEmpty() ||
            borrowDate == null ||
            returnDate == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "All fields are required. Please fill in all fields.");
            return;
        }
        if(!book.matches("^[0-9]+$")) {
         AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "wrong Barcode");
        }

        // Validate that the return date is after the borrowing date
        if (!returnDate.isAfter(borrowDate)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Dates", "The return date must be after the borrowing date.");
            return;
        }

        // Convert dates to strings in the desired format
        String borrowDateStr = borrowDate.toString();
        String returnDateStr = returnDate.toString();

        // Send request to the server
        String[] details = { user, book, borrowDateStr, returnDateStr };
        ClientUI.chat.accept(new ClientMessage(details, "borrow"));
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the Librarian view.
     *
     * @param event the action event triggered by clicking the back button
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
}
