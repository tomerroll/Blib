
package librarian;

import java.io.IOException;

import client.ClientUI;
import common.LoginController;
import common.SearchBookController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.ClientMessage;

/**
 * Controller class for managing the librarian GUI.
 * Handles various actions such as borrowing books, returning books, and viewing reports.
 */
public class LibrarianGUIController {

    @FXML
    private Button btnSubscription_Registration; // Button for subscription registration

    @FXML
    private Button btnBorrowing_a_Book; // Button for borrowing a book

    @FXML
    private Button btnReturing_a_Book; // Button for returning a book

    @FXML
    private Button btnMonthly_Reports; // Button for viewing monthly reports

    @FXML
    private Button btnViewing_Card_Reader; // Button for viewing card reader

    @FXML
    private Button btnBack; // Button to navigate back

    @FXML
    private Button btnDM; // Button to view direct messages

    @FXML
    private Button btnSearch; // Button to search for books

    @FXML
    private Label welcome; // Label to display welcome message

    @FXML
    private Button btnlost_book; // Button to report a lost book

    /**
     * Initializes the controller class.
     * Sets the welcome message with the librarian's first name.
     */
    @FXML
    public void initialize() {
        LoginController controller = LoginController.getInstance();
        String librarianUserName = controller.user.getFirstName();
        welcome.setText(String.format("Welcome, %s!", librarianUserName));
    }

    /**
     * Handles the action when the DM button is clicked.
     * Navigates to the DM view.
     *
     * @param event the action event triggered by clicking the DM button
     */
    @FXML
    public void DM(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/DMGUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("DM'S");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the subscription registration button is clicked.
     * Navigates to the subscription registration view.
     *
     * @param event the action event triggered by clicking the subscription registration button
     */
    @FXML
    void Subscription_Registration(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/Subscription_RegistrationGUI.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Subscription Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the borrowing a book button is clicked.
     * Navigates to the borrowing book view.
     *
     * @param event the action event triggered by clicking the borrowing a book button
     */
    @FXML
    void Borrowing_a_Book(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/BorrowingBookGUI.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Borrowing Book");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the returning a book button is clicked.
     * Navigates to the returning book view.
     *
     * @param event the action event triggered by clicking the returning a book button
     */
    @FXML
    void Returing_a_Book(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/ReturningBookGUI.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Returning Book");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the lost book button is clicked.
     * Navigates to the lost book view.
     *
     * @param event the action event triggered by clicking the lost book button
     */
    @FXML
    void lost_book(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/book_lost.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Book Lost");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the monthly reports button is clicked.
     * Navigates to the monthly reports view.
     *
     * @param event the action event triggered by clicking the monthly reports button
     */
    @FXML
    void Monthly_Reports(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/MonthlyReportsGUI.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Monthly Reports");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the action when the viewing card reader button is clicked.
     * Navigates to the viewing card reader view.
     *
     * @param event the action event triggered by clicking the viewing card reader button
     */
    @FXML
    void Viewing_Card_Reader(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/ViewingCardReaderGUI.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Viewing Card Reader");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the action when the search button is clicked.
     * Navigates to the search book view.
     *
     * @param event the action event triggered by clicking the search button
     */
    @FXML
    void Search(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/SearchBookGUI.fxml"));
            Parent root = loader.load();
            SearchBookController controller = loader.getController();
            controller.setFlagForBackBtn("Librarian");
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SearchBook");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the back button is clicked.
     * Logs out the user and navigates back to the login screen.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    void Back(ActionEvent event) {
        LoginController controllerlogin = LoginController.getInstance();
        if (controllerlogin.isLoggedIn()) {
            try {
                ((Node) event.getSource()).getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/LoginScreenGUI.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Login Screen");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int id = controllerlogin.user.getUserId();
            new Thread(() -> {
                try {
                    ClientUI.chat.accept(new ClientMessage(id, "disconnect"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
