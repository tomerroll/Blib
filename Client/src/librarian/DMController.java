
package librarian;

import java.io.IOException;
import java.util.ArrayList;
import client.ClientUI;
import common.LoginController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.ClientMessage;

/**
 * Controller class for managing the Direct Messages (DM) view.
 * Handles the display of DMs and navigation to other screens.
 */
public class DMController {

    @FXML
    private TableView<String[]> DMTable; // TableView for displaying DMs

    @FXML
    private TableColumn<String[], String> messageClm; // TableColumn for displaying message content

    @FXML
    private TableColumn<String[], String> dateClm; // TableColumn for displaying message date

    @FXML
    private Button backBtn; // Button to navigate back to the previous screen

    // Instance of DMController for use in other classes
    public static DMController instance;

    // Static method to get instance of DMController
    public static DMController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and requests DMs from the server.
     */
    @FXML
    public void initialize() {
        // Store instance for later use
        instance = this;
        DMTable.setPlaceholder(new Label(
                "No messages to display right now.\nPlease check back later."
            ));
        // Set up table columns
        messageClm.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        dateClm.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        // Request DMs from the server
        Platform.runLater(() -> {
            LoginController controllerlogin = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(controllerlogin.user.getUserId(), "getDMbyID"));
            System.out.println("Request sent for DM");
        });
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the previous screen based on the user's role.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    void handleBackBtn(javafx.event.ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            // Choose the next GUI based on the user's role
            LoginController controllerlogin = LoginController.getInstance();
            String fxmlPath = switch (controllerlogin.user.getRole()) {
                case "Subscriber" -> "/subscriber/SubscriberGUI.fxml";
                case "Librarian" -> "/librarian/LibrarianGUI.fxml";
                default -> "/common/LoginScreenGUI.fxml"; // Default fallback
            };
            loader.setLocation(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            // Load and show the stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Return GUI");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the DMs into the TableView.
     *
     * @param ActivityTable the list of DMs to be displayed
     */
    public void loadDM(ArrayList<String[]> ActivityTable) {
        if (ActivityTable.size() != 0) {
            // Ensure UI updates happen on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    ObservableList<String[]> activityData = FXCollections.observableArrayList(ActivityTable);
                    DMTable.setItems(activityData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("There is no DM'S");
        }
    }
}
