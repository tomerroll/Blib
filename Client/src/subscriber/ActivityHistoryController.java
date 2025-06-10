
package subscriber;

import javafx.event.ActionEvent;
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

import java.io.IOException;
import java.util.ArrayList;

import client.ClientUI;
import common.LoginController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller class for managing the activity history view.
 * Handles the display of user activity history and navigation back to the previous screen.
 */
public class ActivityHistoryController {

    @FXML
    private TableView<String[]> ActivityHistoryTable; // TableView for displaying activity history

    @FXML
    private TableColumn<String[], String> colTransaction; // TableColumn for transaction details

    @FXML
    private TableColumn<String[], String> colTime; // TableColumn for transaction time

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    @FXML
    private Label lblStatus; // Label to display status messages

    // Instance of ActivityHistoryController for use in other classes
    public static ActivityHistoryController instance;

    /**
     * Static method to get the instance of ActivityHistoryController.
     *
     * @return the instance of ActivityHistoryController
     */
    public static ActivityHistoryController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and requests activity history from the server.
     */
    @FXML
    public void initialize() {
        // Store instance for later use
        instance = this;
        ActivityHistoryTable.setPlaceholder(new Label(
                "No activity found.\nPlease check back after some actions."
            ));
        // Set up table columns
        colTransaction.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));

        // Request activity history from the server
        Platform.runLater(() -> {
            LoginController controllerlogin = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(controllerlogin.user.getUserId(), "getActivityHistorybyID"));
            System.out.println("Request sent for ActivityHistory");
        });
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the previous screen.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    void back(ActionEvent event) {
        try {
            // Hide the current window (Activity History screen)
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
     * Loads the activity history into the table view.
     *
     * @param ActivityTable the list of activity history records
     */
    public void loadActivityHistory(ArrayList<String[]> ActivityTable) {
        if (ActivityTable.size() != 0) {
            // Ensure UI updates happen on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    ObservableList<String[]> activityData = FXCollections.observableArrayList(ActivityTable);
                    ActivityHistoryTable.setItems(activityData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("There is no ActivityHistory");
        }
    }
}
