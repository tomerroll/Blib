
package librarian;

import java.io.IOException;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.AlertHelper;
import logic.ClientMessage;

/**
 * Controller class for managing the book lost view.
 * Handles the input of user and book details and sends a lost book report to the server.
 */
public class book_lostController {

    @FXML
    private TextField username; // TextField for entering the username

    @FXML
    private TextField bookId; // TextField for entering the book ID

    @FXML
    private Button submitButton; // Button to submit the lost book report

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    /**
     * Handles the action when the submit button is clicked.
     * Validates the input fields and sends a lost book report to the server.
     */
    @FXML
    private void handleSubmitButtonAction() {
        String user = username.getText();
        String book = bookId.getText();

        // Validate fields are not empty
        if (user == null || user.trim().isEmpty() ||
            book == null || book.trim().isEmpty()){
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "All fields are required. Please fill in all fields.");
            return;
        }
        if(!book.matches("^[0-9]+$")) {
         AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "wrong Barcode");
        }
        String[] details = {user, book};
        ClientUI.chat.accept(new ClientMessage(details, "lost"));
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
            System.out.println("Failed to load LibrarianGUI.fxml");
        }
    }
}
