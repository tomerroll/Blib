
package common;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import logic.AlertHelper;
import logic.ClientMessage;

import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 * Controller class for managing the search book view.
 * Handles the search functionality and navigation to other screens.
 */
public class SearchBookController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField BookNameTxt; // TextField for entering the book name

    @FXML
    private TextField GenreTxt; // TextField for entering the book genre

    @FXML
    private TextField DescriptionTxt; // TextField for entering the book description

    @FXML
    private Button SearchBtn; // Button to trigger the search action

    @FXML
    private Button AllBooksBtn; // Button to display all books

    @FXML
    private Button BackBtn; // Button to navigate back to the previous screen

    // Flag to choose what page to return to when the back button is clicked
    private String flagForBackBtn;

    // Setter for flagForBackBtn
    public void setFlagForBackBtn(String flag) {
        this.flagForBackBtn = flag;
    }

    // Instance of SearchBookController for use in other classes
    public static SearchBookController instance;

    // Static method to get instance of SearchBookController
    public static SearchBookController getInstance() {
        return instance;
    }

    /**
     * Handles the action when the "All Books" button is clicked.
     * Sends a request to the server to get all books.
     *
     * @param event the action event triggered by clicking the "All Books" button
     */
    @FXML
    void handleAllBooksBtnClick(ActionEvent event) {
        String[] bookDetails = new String[1];
        bookDetails[0] = flagForBackBtn;
        ClientUI.chat.accept(new ClientMessage(bookDetails, "getAllBooks"));
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the previous screen based on the flag.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    void handleBackBtnClick(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            // Choose the next GUI based on the flag
            String fxmlPath = switch (flagForBackBtn) {
                case "LogIn" -> "/common/LoginScreenGUI.fxml";
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
     * Handles the action when the search button is clicked.
     * Validates the input fields and sends a search request to the server.
     *
     * @param event the action event triggered by clicking the search button
     * @throws IOException if an I/O error occurs
     */
    @FXML
    void handleSearchBtnClick(ActionEvent event) throws IOException {
        SearchBookController searchBookController = SearchBookController.getInstance();
        String bookTitle = searchBookController.BookNameTxt.getText(); // Get text
        String bookGenre = searchBookController.GenreTxt.getText();   // Get text
        String bookDescription = searchBookController.DescriptionTxt.getText(); // Get text
        if (bookTitle.isEmpty() && bookGenre.isEmpty() && bookDescription.isEmpty()) {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "An Error Occurred", "You have to fill some of fields");
            System.out.println("You have to fill some of fields");
        } else if (!bookTitle.isEmpty() && !bookGenre.isEmpty() || !bookTitle.isEmpty() && !bookDescription.isEmpty() || !bookDescription.isEmpty() && !bookGenre.isEmpty()) {
            AlertHelper.showAlert(AlertType.ERROR, "Error", "An Error Occurred", "You have to fill just one of the fields");
            System.out.println("You have to fill just one of the fields");
        } else {
            String[] bookDetails = new String[2];
            if (!bookTitle.isEmpty() && bookGenre.isEmpty() && bookDescription.isEmpty()) {
                bookDetails[0] = bookTitle;
                bookDetails[1] = flagForBackBtn;
                ClientUI.chat.accept(new ClientMessage(bookDetails, "getBookDetailsByTitle"));
            } else if (!bookGenre.isEmpty() && bookTitle.isEmpty() && bookDescription.isEmpty()) {
                bookDetails[0] = bookGenre;
                bookDetails[1] = flagForBackBtn;
                ClientUI.chat.accept(new ClientMessage(bookDetails, "getBookDetailsByGenre"));
            } else {
                bookDetails[0] = bookDescription;
                bookDetails[1] = flagForBackBtn;
                ClientUI.chat.accept(new ClientMessage(bookDetails, "getBookDetailsByDescription"));
            }
        }
    }

    /**
     * Closes the current GUI.
     */
    public void closeGUI() {
        Stage stage = (Stage) BookNameTxt.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     * Stores the instance for later use.
     */
    @FXML
    void initialize() {
        instance = this;
    }
}
