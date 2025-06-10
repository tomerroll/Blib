
package subscriber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import logic.ClientMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;

import client.ClientUI;
import common.LoginController;

/**
 * Controller class for managing the reservation view.
 * Handles the display of available books for reservation and the reservation process.
 */
public class ReservationController {

    @FXML
    private TableView<String[]> bookTable; // TableView for displaying available books

    @FXML
    private TableColumn<String[], String> colBookId; // Book ID column

    @FXML
    private TableColumn<String[], String> colTitle; // Title column

    @FXML
    private TableColumn<String[], String> colAuthor; // Author column

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    @FXML
    private Button btnReserve; // Button to reserve a selected book

    // Instance of ReservationController for use in other classes
    public static ReservationController instance;

    /**
     * Static method to get the instance of ReservationController.
     *
     * @return the instance of ReservationController
     */
    public static ReservationController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and requests the list of available books for reservation from the server.
     */
    @FXML
    public void initialize() {
        // Store instance for later use
        instance = this;
        bookTable.setPlaceholder(new Label(
                "You can't place orders right now.\nFor more information, please contact the librarian."
            ));
        // Set up table columns
        colBookId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0])); // Bind Book ID
        colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));  // Bind Title
        colAuthor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2])); // Bind Author

        // Request available books for reservation from the server
        Platform.runLater(() -> {
            LoginController controllerlogin = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(controllerlogin.user.getUserId(), "getReservationBookTable"));
        });
    }

    /**
     * Loads the list of available books for reservation into the table view.
     *
     * @param books the list of available books
     */
    public void loadBooksForReservationTable(ArrayList<String[]> books) {
        // Ensure UI updates happen on the JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                ObservableList<String[]> BooksData = FXCollections.observableArrayList(books);
                bookTable.setItems(BooksData);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            // Hide the current window (Reservation screen)
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
     * Handles the action when the reserve button is clicked.
     * Sends a reservation request for the selected book to the server.
     *
     * @param event the action event triggered by clicking the reserve button
     */
    @FXML
    void reserveBook(ActionEvent event) {
        // Get the selected book
        String[] selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            // Show alert if no book is selected
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to reserve.");
            alert.showAndWait();
            return;
        }

        LoginController controllerlogin = LoginController.getInstance();
        int userId = controllerlogin.user.getUserId(); // Get the user ID as an int
        String bookId = selectedBook[0];

        // Create an array of size 2
        String[] reservation = new String[2];
        reservation[0] = String.valueOf(userId);
        reservation[1] = bookId;

        // Send reservation request to the server
        ClientUI.chat.accept(new ClientMessage(reservation, "setReservation"));
    }
}
