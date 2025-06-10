
package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import Server.EchoServer;

/**
 * Controller class for the server log GUI.
 * Manages the display of the server's IP address and the log of connected clients.
 */
public class ServerLogController {

    @FXML
    private TextArea logArea; // Text area to display the log of connected clients

    @FXML
    private Label IpServer; // Label to display the server's IP address

    private static ServerLogController instance; // Singleton instance of the controller

    /**
     * Returns the singleton instance of the ServerLogController.
     *
     * @return the singleton instance of the ServerLogController
     */
    public static ServerLogController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller.
     * Sets the singleton instance and displays the server's IP address.
     */
    @FXML
    public void initialize() {
        instance = this; // Set the instance when the controller is initialized
        try {
            // Display the server's IP address
            String serverIp = InetAddress.getLocalHost().getHostAddress();
            IpServer.setText("Server IP: " + serverIp);
        } catch (UnknownHostException e) {
            IpServer.setText("Unable to retrieve IP address.");
            e.printStackTrace();
        }

        updateLogArea(); // Initial display of connected clients
    }

    /**
     * Updates the log area with the list of connected clients.
     * Runs on the JavaFX application thread.
     */
    public void updateLogArea() {
        Platform.runLater(() -> {
            StringBuilder logText = new StringBuilder();
            // Access the static map from EchoServer directly
            for (Map.Entry<String, String> entry : EchoServer.connectedClients.entrySet()) {
                logText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            logArea.setText(logText.toString());
        });
    }

    /**
     * Removes a client from the log based on their IP address.
     * Runs on the JavaFX application thread.
     *
     * @param clientIP the IP address of the client to remove
     */
    public void removeClient(String clientIP) {
        Platform.runLater(() -> {
            if (EchoServer.connectedClients.containsKey(clientIP)) {
                EchoServer.connectedClients.remove(clientIP); // Remove the client from the map
                System.out.println("Removed client with IP: " + clientIP); // Debugging output
            } else {
                System.out.println("Client IP not found: " + clientIP);
            }

            updateLogArea(); // Refresh the log to reflect the changes
        });
    }
}
