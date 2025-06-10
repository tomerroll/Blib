
package subscriber;

import java.io.IOException;

import client.ClientUI;
import common.LoginController;
import common.SearchBookController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.ClientMessage;

/**
 * Controller class for managing the Subscriber GUI view.
 * Handles navigation and actions within the subscriber interface.
 */
public class SubscriberGUIController {

    @FXML
    private Button btnReserveBook, btnRequestExtension, btnActivityHistory, btnPersonalDetails, btnBack, btnSearch, btnMyBooks;

    @FXML
    private Label welcome; // Fixed Typo: "Lable" -> "Label"

    /**
     * Initializes the controller class.
     * Sets the welcome message based on the logged-in user's first name.
     */
    @FXML
    public void initialize() {
        LoginController controller = LoginController.getInstance();
        if (controller != null && controller.user != null) {
            String subUserName = controller.user.getFirstName();
            welcome.setText(String.format("Welcome, %s!", subUserName));
        } else {
            welcome.setText("Welcome, Guest!");
        }
    }

    /**
     * Handles the action when the Request Extension button is clicked.
     * Opens the Request Extension window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void RequestExtension(ActionEvent event) {
        if (isUserFrozen()) return;
        openNewWindow(event, "/subscriber/RequestExtensionGUI.fxml", "Request Extension");
    }

    /**
     * Handles the action when the Reserve Book button is clicked.
     * Opens the Reserve Book window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void ReserveBook(ActionEvent event) {
        if (isUserFrozen()) return;
        openNewWindow(event, "/subscriber/ReservationGUI.fxml", "Reserve a Book");
    }

    /**
     * Handles the action when the Activity History button is clicked.
     * Opens the Activity History window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void ActivityHistory(ActionEvent event) {
        openNewWindow(event, "/subscriber/ActivityHistoryGUI.fxml", "Activity History");
    }

    /**
     * Handles the action when the Personal Details button is clicked.
     * Opens the Personal Details window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void PersonalDetails(ActionEvent event) {
        openNewWindow(event, "/subscriber/PersonalDetailsGUI.fxml", "Personal Details");
    }

    /**
     * Handles the action when the My Books button is clicked.
     * Opens the My Books window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void MyBooks(ActionEvent event) {
        openNewWindow(event, "/subscriber/MyBooksGUI.fxml", "My Books");
    }

    /**
     * Handles the action when the Search button is clicked.
     * Opens the Search Book window.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void Search(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/SearchBookGUI.fxml"));
            Parent root = loader.load();

            // Get the controller and set the flag
            SearchBookController controller = loader.getController();
            controller.setFlagForBackBtn("Subscriber");

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Search Book");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the Back button is clicked.
     * Logs out the user and navigates back to the login screen.
     *
     * @param event the action event triggered by clicking the button
     */
    @FXML
    void Back(ActionEvent event) {
        LoginController controllerLogin = LoginController.getInstance();

        if (controllerLogin.isLoggedIn()) {
            openNewWindow(event, "/common/LoginScreenGUI.fxml", "Login Screen");

            int id = controllerLogin.user.getUserId();
            // Send a disconnect message to the server on a separate thread
            new Thread(() -> {
                try {
                    ClientUI.chat.accept(new ClientMessage(id, "disconnect"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Utility method to open a new window and hide the current one.
     *
     * @param event the action event triggered by clicking the button
     * @param fxmlPath the path to the FXML file for the new window
     * @param title the title of the new window
     */
    private void openNewWindow(ActionEvent event, String fxmlPath, String title) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the user's subscription status is frozen.
     * Displays an alert if the status is frozen.
     *
     * @return true if the user's status is frozen, false otherwise
     */
    private boolean isUserFrozen() {
        LoginController controllerLogin = LoginController.getInstance();
        if ("Frozen".equals(controllerLogin.user.getSubscriptionStatus())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Subscription Status");
            alert.setHeaderText(null);
            alert.setContentText("Your status is Frozen, you cannot perform this action.");
            alert.showAndWait();
            return true;
        }
        return false;
    }
}
