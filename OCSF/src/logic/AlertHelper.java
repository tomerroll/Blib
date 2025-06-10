
package logic;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Helper class for displaying alerts in a JavaFX application.
 */
public class AlertHelper {

    /**
     * Displays an alert with the specified parameters.
     * Ensures the alert is shown on the JavaFX Application Thread.
     *
     * @param alertType the type of alert (e.g., INFORMATION, WARNING, ERROR)
     * @param title the title of the alert dialog
     * @param header the header text of the alert dialog
     * @param content the content text of the alert dialog
     */
    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Platform.runLater(() -> { // Ensure this runs on the JavaFX Application Thread
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.show();
        });
    }
}
