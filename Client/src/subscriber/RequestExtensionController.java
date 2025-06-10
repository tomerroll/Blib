
package subscriber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import logic.ClientMessage;
import client.ClientUI;
import common.LoginController;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller class for managing the request extension view.
 * Handles the display of loaned books and the request for loan extensions.
 */
public class RequestExtensionController {

    @FXML
    private TableView<String[]> loanTable; // TableView for displaying loaned books

    @FXML
    private TableColumn<String[], String> colBookId; // TableColumn for book ID

    @FXML
    private TableColumn<String[], String> colBookName; // TableColumn for book name

    @FXML
    private TableColumn<String[], String> colAuthor; // TableColumn for author name

    @FXML
    private TableColumn<String[], String> colExpirationDate; // TableColumn for expiration date

    @FXML
    private TextField txtLoanId; // TextField for entering the loan ID

    @FXML
    private Button btnRequestExtension; // Button to request an extension

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    @FXML
    private Label lblStatus; // Label to display status messages

    private ObservableList<String[]> loansData = FXCollections.observableArrayList(); // ObservableList for loan data

    // Instance of RequestExtensionController for use in other classes
    public static RequestExtensionController instance;

    /**
     * Static method to get the instance of RequestExtensionController.
     *
     * @return the instance of RequestExtensionController
     */
    public static RequestExtensionController getInstance() {
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and requests loan data from the server.
     */
    @FXML
    void initialize() {
        instance = this;
        loanTable.setPlaceholder(new Label(
                "Unable to extend the loan due to one or more of the following reasons:\n" +
                "1. No active loans found.\n" +
                "2. The book is not eligible for an extension due to:\n" +
                "   • More than a week remains until the return date.\n" +
                "   • An active reservation exists for the book."
            ));
        // Set up the columns to extract data from the String[] array
        colBookId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0])); // Populate Book ID column
        colBookName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        colExpirationDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));

        // Request loans data from the server
        Platform.runLater(() -> {
            LoginController loginController = LoginController.getInstance();
            ClientUI.chat.accept(new ClientMessage(loginController.user.getUserId(), "getLoanTable"));
        });
    }

    /**
     * Loads the loan table data into the view.
     *
     * @param loans the list of loan data
     */
    public void loadLoanTableData(ArrayList<String[]> loans) {
        Platform.runLater(() -> {
            try {
                loansData.clear();
                loansData.addAll(loans);
                loanTable.setItems(loansData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the action when the request extension button is clicked.
     * Sends a request to extend the loan period for the selected book.
     *
     * @param event the action event triggered by clicking the request extension button
     */
    @FXML
    void requestExtension(ActionEvent event) {
        String[] selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null || selectedLoan.length < 4) { // Ensure there are enough elements (including Book ID)
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to loan.");
            alert.showAndWait();
            return;
        }

        LoginController controllerlogin = LoginController.getInstance();
        int userId = controllerlogin.user.getUserId();
        String bookId = selectedLoan[0]; // Get Book ID from selected item

        String[] loanDetails = new String[2];
        loanDetails[0] = String.valueOf(userId);
        loanDetails[1] = bookId;
        ClientUI.chat.accept(new ClientMessage(loanDetails, "setExpirationDate"));
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
            ((Node) event.getSource()).getScene().getWindow().hide();

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
}
