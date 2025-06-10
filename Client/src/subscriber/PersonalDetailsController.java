
package subscriber;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.ClientMessage;
import client.ClientUI;
import common.LoginController;

import java.io.IOException;

/**
 * Controller class for managing the personal details view.
 * Handles the display and update of user personal details.
 */
public class PersonalDetailsController {

    @FXML
    private Label lblName; // Label to display the first name

    @FXML
    private Label lblLastName; // Label to display the last name

    @FXML
    private Label lblUserID; // Label to display the user ID

    @FXML
    private TextField txtPhone; // TextField for entering the phone number

    @FXML
    private TextField txtEmail; // TextField for entering the email address

    @FXML
    private Label lblStatus; // Label to display the subscription status

    // Instance of PersonalDetailsController for use in other classes
    public static PersonalDetailsController instance;

    /**
     * Static method to get the instance of PersonalDetailsController.
     *
     * @return the instance of PersonalDetailsController
     */
    public static PersonalDetailsController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Requests personal details from the server.
     */
    @FXML
    public void initialize() {
        // Store instance for later use
        instance = this;

        // Request personal details from the server
        Platform.runLater(() -> {
            LoginController controllerLogin = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(controllerLogin.user.getUserId(), "getPersonalDetailsById"));
            System.out.println("Request sent for personal details");
        });
    }

    /**
     * Loads the personal details into the view.
     *
     * @param details the array of personal details
     */
    public void loadPersonalDetails(String[] details) {
        if (details != null && details.length >= 6) {
            // Ensure UI updates happen on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    System.out.println("Status: " + details[3]);  // Check if this prints "Active"

                    lblName.setText(details[0]);       // Set first name
                    lblLastName.setText(details[1]);   // Set last name
                    lblUserID.setText(details[2]);     // Set username
                    lblStatus.setText(details[3]);     // Set subscription status
                    txtPhone.setText(details[4]);      // Set phone number
                    txtEmail.setText(details[5]);      // Set email
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Insufficient details provided");
        }
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the previous screen.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    void btnBack(ActionEvent event) {
        try {
            // Hide the current window (Personal Details screen)
            ((Node) event.getSource()).getScene().getWindow().hide();

            // Load the previous screen (e.g., Subscriber GUI)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/subscriber/SubscriberGUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Subscriber GUI");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the update button is clicked.
     * Validates the input fields and sends an update request to the server.
     *
     * @param event the action event triggered by clicking the update button
     */
    @FXML
    void btnUpdate(ActionEvent event) {
        try {
            // Retrieve updated values from text fields
            String updatedPhone = txtPhone.getText();
            String updatedEmail = txtEmail.getText();

            // Validation for phone number
            if (updatedPhone.isEmpty() || updatedEmail.isEmpty()) {
                showAlert("Phone number or email cannot be empty.");
                return;
            }

            // Check if phone number is valid (must start with 05, only digits, length 10)
            if (!updatedPhone.matches("^05\\d{8}$")) {
                showAlert("Invalid phone number. It must contain only 10 digits.");
                return;
            }

            // Check if email is valid using a basic regex pattern
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!updatedEmail.matches(emailRegex)) {
                showAlert("Invalid email format.");
                return;
            }

            // Send the updated details to the server
            LoginController controllerLogin = LoginController.getInstance();
            int userId = controllerLogin.user.getUserId();

            // Construct message for updating personal details
            String[] updateDetails = {String.valueOf(userId), updatedPhone, updatedEmail};
            ClientUI.chat.accept(new ClientMessage(updateDetails, "updatePersonalDetails"));

            System.out.println("Update request sent for user ID: " + userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to show an alert.
     *
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Request Status");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
