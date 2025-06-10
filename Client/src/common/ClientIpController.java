
package common;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for managing the Client IP input view.
 * Handles the input of the server IP address and navigation to the next screen.
 */
public class ClientIpController {

    /** Single instance of ClientController to manage communication with the server. */
    public static ClientController chat;

    @FXML
    private Button btnNext; // Button to proceed to the next screen

    @FXML
    private Button btnExit; // Button to exit the application

    @FXML
    public TextField iptxt; // TextField to input the server IP address

    /**
     * This method is called when the "Next" button is clicked.
     * It initializes the ClientController with the provided IP address and navigates to the login screen.
     *
     * @param event the action event triggered by clicking the "Next" button
     */
    @FXML
    private void Next(ActionEvent event) {
        // Get the input text (IP address) from the TextField
        String serverIp = iptxt.getText();
        System.out.println("Server IP: " + serverIp);

        // Create a new ClientController with the provided IP address and a fixed port (55555)
        ClientUI.chat = new ClientController(serverIp, 55555);

        // Hide the current window (ClientIp window)
        ((Node) event.getSource()).getScene().getWindow().hide();

        // Load the next window (LoginScreenGUI.fxml) to start the search functionality
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/LoginScreenGUI.fxml"));
        Pane root;
        try {
            // Load the SubscriberSearch form
            root = loader.load();

            // Set up the scene and stage for the SubscriberSearch window
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("LoginScreenGUI"); // Set the title of the new window
            stage.show(); // Show the new window
        } catch (IOException e) {
            // Handle potential I/O exceptions (e.g., if the FXML file is not found)
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the "Exit" button is clicked.
     * It terminates the application.
     */
    @FXML
    private void getExitBtn() {
        // Close the application or handle any exit logic
        System.out.println("Exiting application...");
        System.exit(0); // Terminate the application
    }

    /**
     * This method is used to start the ClientIp window.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during loading the FXML file
     */
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the ClientIp window
        Parent root = FXMLLoader.load(getClass().getResource("/common/ClientIp.fxml"));

        // Create a scene with the loaded root layout
        Scene scene = new Scene(root);

        // Apply CSS styles to the scene
        scene.getStylesheets().add(getClass().getResource("/common/ClientIp.css").toExternalForm());

        // Set the title and scene for the primary stage (window)
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);

        // Show the primary stage (window)
        primaryStage.show();
    }
}
