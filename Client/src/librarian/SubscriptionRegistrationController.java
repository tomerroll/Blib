
package librarian;

import java.io.IOException;
import java.util.regex.Pattern;

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
 * Controller class for managing the subscription registration view.
 * Handles the input of user details and sends a registration request to the server.
 */
public class SubscriptionRegistrationController {

    @FXML
    private TextField username; // TextField for entering the username

    @FXML
    private TextField password; // TextField for entering the password

    @FXML
    private TextField first_name; // TextField for entering the first name

    @FXML
    private TextField last_name; // TextField for entering the last name

    @FXML
    private TextField phone_number; // TextField for entering the phone number

    @FXML
    private TextField email; // TextField for entering the email address

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    @FXML
    private Button submitButton; // Button to submit the registration request

    /**
     * Handles the action when the submit button is clicked.
     * Validates the input fields and sends a registration request to the server.
     */
    @FXML
    private void handleSubmitButtonAction() {
        String user = username.getText();
        String pass = password.getText();
        String firstName = first_name.getText();
        String lastName = last_name.getText();
        String phone = phone_number.getText();
        String emailAddress = email.getText();

        // Validate fields
        if (user == null || user.trim().isEmpty() ||
            pass == null || pass.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            emailAddress == null || emailAddress.trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "All fields are required. Please fill in all fields.");
            return;
        }

        // Validate first and last name (no numbers allowed)
        if (!firstName.matches("^[a-zA-Z]+$") || !lastName.matches("^[a-zA-Z]+$")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "First and Last name must contain only letters.");
            return;
        }

        // Validate phone number (only 10 digits)
        if (!phone.matches("^05\\d{8}$")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Invalid phone number.");
            return;
        }

        // Validate email format
        if (!isValidEmail(emailAddress)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please enter a valid email address.");
            return;
        }

        // If all validations pass, send the data to the server
        String role = "Subscriber";
        String status = "Active";
        String[] details = {user, pass, firstName, lastName, phone, emailAddress, role, status};
        ClientUI.chat.accept(new ClientMessage(details, "register"));
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

    /**
     * Helper method to validate email format using a regex.
     *
     * @param email the email address to validate
     * @return true if the email address is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }
}
