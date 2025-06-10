
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
 * Controller class for managing the My Books view.
 * Handles the display of the user's borrowed books and navigation back to the previous screen.
 */
public class MyBooksController {

    @FXML
    private TableView<String[]> MyBooksTable; // TableView for displaying borrowed books

    @FXML
    private TableColumn<String[], String> colBookID; // TableColumn for book ID

    @FXML
    private TableColumn<String[], String> colBookTitle; // TableColumn for book title

    @FXML
    private TableColumn<String[], String> colBookAuthor; // TableColumn for book author

    @FXML
    private TableColumn<String[], String> colLoanDate; // TableColumn for loan date

    @FXML
    private TableColumn<String[], String> colReturnDate; // TableColumn for return date

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    @FXML
    private Label lblStatus; // Label to display status messages

    // Instance of MyBooksController for use in other classes
    public static MyBooksController instance;

    /**
     * Static method to get the instance of MyBooksController.
     *
     * @return the instance of MyBooksController
     */
    public static MyBooksController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and requests the user's borrowed books from the server.
     */
    @FXML
    public void initialize() {
        // Store instance for later use
        instance = this;
        MyBooksTable.setPlaceholder(new Label(
                "No books available.\nFeel free to borrow one!"
                ));
        // Set up table columns
        colBookID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colBookTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colBookAuthor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        colLoanDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        colReturnDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));

        // Request books from the server
        Platform.runLater(() -> {
            LoginController controllerlogin = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(controllerlogin.user.getUserId(), "getMyBooksID"));
            System.out.println("Request sent for My Books");
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
            // Hide the current window (My Books screen)
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
     * Loads the user's borrowed books into the table view.
     *
     * @param MyBooksTableData the list of borrowed books
     */
    public void loadMyBooks(ArrayList<String[]> MyBooksTableData) {
        if (MyBooksTableData.size() != 0) {
            // Ensure UI updates happen on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    ObservableList<String[]> myBooksData = FXCollections.observableArrayList(MyBooksTableData);
                    MyBooksTable.setItems(myBooksData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("There are no books.");
        }
    }
}
