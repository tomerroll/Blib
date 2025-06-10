
package common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Book;

import java.io.IOException;

/**
 * Controller class for managing the book details view.
 * Handles the display and interaction with book details in a table.
 */
public class BookDetailsController {

    @FXML
    private Button Backbtn;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> Titleclm;

    @FXML
    private TableColumn<Book, String> Authorclm;

    @FXML
    private TableColumn<Book, String> Genreclm;

    @FXML
    private TableColumn<Book, String> Descriptionclm;

    @FXML
    private TableColumn<Book, String> AvaliableCopiesclm;

    @FXML
    private TableColumn<Book, String> UpcomingReturnDateClm;

    @FXML
    private TableColumn<Book, String> LocationClm;

    private ObservableList<Book> bookDetailsList = FXCollections.observableArrayList();

    // Flag to choose what page to return to when the back button is clicked
    private String flagForBackBtn;

    // Instance of BookDetailsController for use in other classes
    public static BookDetailsController instance;

    /**
     * Gets the singleton instance of the BookDetailsController.
     *
     * @return the instance of BookDetailsController
     */
    public static BookDetailsController getInstance() {
        if (instance == null) {
            try {
                FXMLLoader loader = new FXMLLoader(BookDetailsController.class.getResource("/common/BookDetailsGUI.fxml"));
                Parent root = loader.load(); // Load FXML and initialize the controller
                instance = loader.getController(); // Set the controller instance
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Book Details");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Initializes the controller class.
     * Sets up the table columns and binds the observable list to the table.
     */
    @FXML
    public void initialize() {
        instance = this;

        // Initialize table columns with appropriate properties
        Titleclm.setCellValueFactory(new PropertyValueFactory<>("title"));
        Authorclm.setCellValueFactory(new PropertyValueFactory<>("author"));
        Genreclm.setCellValueFactory(new PropertyValueFactory<>("genre"));
        Descriptionclm.setCellValueFactory(new PropertyValueFactory<>("description"));
        AvaliableCopiesclm.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        UpcomingReturnDateClm.setCellValueFactory(new PropertyValueFactory<>("upcomigReturnDate"));
        LocationClm.setCellValueFactory(new PropertyValueFactory<>("Location"));

        // Bind observable list to the table
        booksTable.setItems(bookDetailsList);
    }

    /**
     * Resets the instance of the BookDetailsController.
     * This ensures a new instance is created next time.
     */
    public static void resetInstance() {
        instance = null;  // Reset the instance so that a new one is created next time
    }

    /**
     * Clears the book details from the table.
     * This prevents old details from showing.
     */
    public void clearBookDetails() {
        bookDetailsList.clear(); // Clear the table data
    }

    /**
     * Loads the book details into the table.
     *
     * @param details a 2D array containing book details
     */
    public void loadBookDetails(String[][] details) {
        clearBookDetails();  // Clear existing details before loading new ones
        System.out.println("Books display in table");
        if (details != null) {
            for (String[] detail : details) {
                if (detail.length >= 9) {
                    bookDetailsList.add(new Book(
                            detail[0], // title
                            detail[1], // author
                            detail[2], // genre
                            detail[3], // description
                            detail[4], // availableCopies
                            detail[5], // totalCopies
                            detail[6], // upcomingReturnDate
                            detail[7]  // location
                    ));
                    this.flagForBackBtn = details[0][8];
                }
            }
        } else {
            System.out.println("Insufficient details provided");
        }
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the SearchBook view.
     *
     * @param event the action event
     */
    @FXML
    void handleBackbtn(ActionEvent event) {
        try {
            // Reset the instance to ensure a fresh BookDetailsController is created
            BookDetailsController.resetInstance();
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/SearchBookGUI.fxml"));
            Parent root = loader.load();
            // Get the controller and set the flag
            SearchBookController controller = loader.getController();
            controller.setFlagForBackBtn(flagForBackBtn); // Pass a flag
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SearchBook");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
