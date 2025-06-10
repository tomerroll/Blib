
package common;

import java.io.IOException;

import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ClientMessage;
import logic.User;

/**
 * Controller class for managing the login view.
 * Handles user login and navigation to other screens.
 */
public class LoginController {

    /** The user object representing the logged-in user. */
    public User user = new User(0, null, null, null, null, null, null, null, null);

    /** The action event triggered by the login button. */
    private ActionEvent loginevent;

    @FXML
    private TextField txtUserName; // TextField for entering the username

    @FXML
    private PasswordField txtPassword; // PasswordField for entering the password

    @FXML
    private Button btnLogin; // Button to trigger the login action

    @FXML
    private Button btnSearch; // Button to trigger the search action

    @FXML
    private Label lblStatus; // Label to display the login status

    /** Flag to track login status. */
    private boolean isLoggedIn = false;

    /** Instance of LoginController for use in other classes. */
    public static LoginController instance;

    /**
     * Gets the singleton instance of the LoginController.
     *
     * @return the instance of LoginController
     */
    public static LoginController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Stores the instance for later use.
     */
    @FXML
    public void initialize() {
        instance = this;
    }

    /**
     * Handles the login button action.
     * Validates the username and password, and sends a login request to the server.
     *
     * @param event the action event triggered by clicking the login button
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        loginevent = event;
        String username = txtUserName.getText();
        String password = txtPassword.getText();
        if (username == null || password == null) {
            System.out.println("You have to fill username and password fields");
        } else {
            String[] loginDetails = new String[2];
            loginDetails[0] = username;
            loginDetails[1] = password;
            ClientUI.chat.accept(new ClientMessage(loginDetails, "CheckLoginDetails"));
        }
    }

    /**
     * Handles the search button action.
     * Navigates to the SearchBook view.
     *
     * @param event the action event triggered by clicking the search button
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/SearchBookGUI.fxml"));
            Parent root = loader.load();
            SearchBookController controller = loader.getController();
            controller.setFlagForBackBtn("LogIn");
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SearchBook");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs in the user and navigates to the appropriate GUI based on the user's role.
     *
     * @param user the user object containing the login details
     */
    public void LogIn(User user) {
        Platform.runLater(() -> {
            if (user == null) {
                txtUserName.setText("");
                txtPassword.setText("");
                lblStatus.setText("Invalid login details. Please try again.");
            } else {
                this.user.setUserId(user.getUserId());
                this.user.setUsername(user.getUsername());
                this.user.setPasswordHash(user.getPasswordHash());
                this.user.setFirstName(user.getFirstName());
                this.user.setLastName(user.getLastName());
                this.user.setPhoneNumber(user.getPhoneNumber());
                this.user.setEmail(user.getEmail());
                this.user.setSubscriptionStatus(user.getSubscriptionStatus());
                this.user.setRole(user.getRole());

                isLoggedIn = true;

                txtUserName.setText("");
                txtPassword.setText("");
                try {
                    ((Node) loginevent.getSource()).getScene().getWindow().hide();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + this.user.getRole().toLowerCase() + "/" + this.user.getRole() + "GUI.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(this.user.getRole() + "GUI");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    lblStatus.setText("An error occurred while loading the GUI.");
                }
            }
        });
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
