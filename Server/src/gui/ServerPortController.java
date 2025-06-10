
package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Server.ServerUI;

/**
 * Controller class for the server port GUI.
 * Manages the input of the server port and the transition to the server log view.
 */
public class ServerPortController {

    String temp = "";

    @FXML
    private Button btnExit = null; // Button to exit the application
    @FXML
    private Button btnDone = null; // Button to confirm the port and start the server
    @FXML
    private Label lbllist; // Label to display messages or instructions

    @FXML
    private TextField portxt; // Text field to input the server port
    ObservableList<String> list; // Observable list for any required data binding

    /**
     * Gets the port number from the text field.
     *
     * @return the port number as a string
     */
    private String getport() {
        return portxt.getText();
    }

    /**
     * Handles the action when the Done button is clicked.
     * Validates the port input, hides the current window, loads the server log view, and starts the server.
     *
     * @param event the action event triggered by clicking the Done button
     * @throws Exception if an error occurs while loading the server log view or starting the server
     */
    public void Done(ActionEvent event) throws Exception {
        String port = getport();

        if (port.trim().isEmpty()) {
            System.out.println("You must enter a port number");
        } else {
            // Hide the current window
            ((Node) event.getSource()).getScene().getWindow().hide();

            try {
                // Load the ServerLog.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerLog.fxml"));
                Pane root = loader.load();

                // Set the scene and stage
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Server Log");
                stage.show();

                // Run the server
                ServerUI.runServer(port);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error loading the ServerLog.fxml file.");
            }
        }
    }

    /**
     * Starts the server port GUI.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs while loading the server port view
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the action when the Exit button is clicked.
     * Exits the application.
     *
     * @param event the action event triggered by clicking the Exit button
     * @throws Exception if an error occurs while exiting the application
     */
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("exit Academic Tool");
        System.exit(0);
    }
}
