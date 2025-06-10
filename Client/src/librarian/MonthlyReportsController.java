
package librarian;

import java.nio.file.Paths;
import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logic.ClientMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Controller class for managing the monthly reports view.
 * Handles the generation and display of various monthly reports.
 */
public class MonthlyReportsController {

    public static MonthlyReportsController instance;

    public static MonthlyReportsController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        populateMonthYearPickers();
    }

    @FXML
    private ComboBox<String> monthPicker; // ComboBox for selecting the month

    @FXML
    private ComboBox<Integer> yearPicker; // ComboBox for selecting the year

    @FXML
    private VBox root; // VBox container for the report display

    @FXML
    private Button btnBack; // Button to navigate back to the previous screen

    /**
     * Populates the month and year pickers with appropriate values.
     */
    private void populateMonthYearPickers() {
        monthPicker.getItems().addAll(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );

        monthPicker.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        yearPicker.getItems().addAll(IntStream.range(currentYear - 10, currentYear + 11).boxed().toList());

        yearPicker.getSelectionModel().select((Integer) currentYear);
    }

    /**
     * Handles the action when the back button is clicked.
     * Navigates back to the librarian view.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    private void Back(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/LibrarianGUI.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Librarian");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the subscription status report button is clicked.
     * Sends a request to the server to generate the subscription status report.
     *
     * @param event the action event triggered by clicking the subscription status report button
     */
    @FXML
    private void handleSubscriptionStatusReport(ActionEvent event) {
        String selectedMonth = monthPicker.getValue();
        Integer selectedYear = yearPicker.getValue();

        if (selectedMonth != null && selectedYear != null) {
            int month = monthPicker.getSelectionModel().getSelectedIndex() + 1;
            int year = selectedYear;
            int[] date = {year, month};
            ClientUI.chat.accept(new ClientMessage(date, "getSubscriptionStatusReport"));
        } else {
            System.out.println("Month and/or year not selected");
        }
    }

    /**
     * Handles the action when the book borrowing times report button is clicked.
     * Sends a request to the server to generate the book borrowing times report.
     *
     * @param event the action event triggered by clicking the book borrowing times report button
     */
    @FXML
    private void handleBookBorrowingTimesReport(ActionEvent event) {
        ClientUI.chat.accept(new ClientMessage("getBookLoanTimesReport", "getBookLoanTimesReport"));
    }

    /**
     * Handles the action when the back action is triggered.
     * Navigates back to the monthly reports view.
     *
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/MonthlyReportsGUI.fxml"));
            Parent monthlyReportsRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(monthlyReportsRoot);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the subscription status report into the view.
     *
     * @param subscriptionStatusReport the subscription status report data
     */
    public void loadSubscriptionStatusReport(String[] subscriptionStatusReport) {
        Platform.runLater(() -> {
            try {
                PieChart pieChart = new PieChart();
                int activeCount = 0, frozenCount = 0;

                for (String entry : subscriptionStatusReport) {
                    String[] parts = entry.split(":");
                    if (parts.length == 2) {
                        String label = parts[0];
                        int count = Integer.parseInt(parts[1].trim());
                        if (label.equalsIgnoreCase("Active Users")) {
                            activeCount = count;
                        } else if (label.equalsIgnoreCase("Frozen Users")) {
                            frozenCount = count;
                        }
                    }
                }

                pieChart.getData().add(new PieChart.Data("Active (" + activeCount + ")", activeCount));
                pieChart.getData().add(new PieChart.Data("Frozen (" + frozenCount + ")", frozenCount));

                int totalCount = activeCount + frozenCount;

                root.getChildren().clear();
                root.setAlignment(Pos.CENTER);
                root.getChildren().add(pieChart);

                Label activeLabel = createStyledLabel("Active: " + activeCount);
                Label frozenLabel = createStyledLabel("Frozen: " + frozenCount);
                Label totalLabel = createStyledLabel("Total: " + totalCount);

                root.getChildren().addAll(activeLabel, frozenLabel, totalLabel);

                Button backButton = createStyledButton("⬅ Back to Reports", this::handleBackAction);
                root.getChildren().add(backButton);
                exportToCSV(activeCount, frozenCount, totalCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Loads the book loan times report into the view.
     *
     * @param bookLoanTimesReport the book loan times report data
     */
    @SuppressWarnings("unchecked")
    public void loadBookLoanTimesReport(String[] bookLoanTimesReport) {
        Platform.runLater(() -> {
            try {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis(0, 60, 5);
                StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
                barChart.setTitle("Loan Times and Overdue Days");

                XYChart.Series<String, Number> loanTimesSeries = new XYChart.Series<>();
                loanTimesSeries.setName("Loan Time");

                XYChart.Series<String, Number> overdueTimesSeries = new XYChart.Series<>();
                overdueTimesSeries.setName("Overdue Time");

                for (String entry : bookLoanTimesReport) {
                    String[] parts = entry.split(":");
                    if (parts.length >= 3) {
                        String loanId = parts[0];  // Use loan_id as the X-axis label
                        int loanDays = Integer.parseInt(parts[1]);
                        int overdueDays = Integer.parseInt(parts[2]);

                        // Each loan gets a separate column
                        loanTimesSeries.getData().add(new XYChart.Data<>("Loan " + loanId, loanDays));
                        overdueTimesSeries.getData().add(new XYChart.Data<>("Loan " + loanId, overdueDays));
                    }
                }

                barChart.getData().addAll(loanTimesSeries, overdueTimesSeries);

                VBox vbox = new VBox(20);
                vbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(barChart);

                Button backButton = createStyledButton("⬅ Back to Reports", this::handleBackAction);
                vbox.getChildren().add(backButton);

                root.getChildren().clear();
                root.getChildren().add(vbox);
                exportBookLoanTimesToCSV(bookLoanTimesReport);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Creates a styled label.
     *
     * @param text the text to be displayed in the label
     * @return the styled label
     */
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * Creates a styled button.
     *
     * @param text the text to be displayed on the button
     * @param eventHandler the event handler for the button click
     * @return the styled button
     */
    private Button createStyledButton(String text, javafx.event.EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #2980B9; -fx-text-fill: white; -fx-padding: 10;");
        button.setOnAction(eventHandler);
        return button;
    }

    /**
     * Exports the subscription status report to a CSV file.
     *
     * @param activeCount the count of active users
     * @param frozenCount the count of frozen users
     * @param totalCount the total count of users
     */
    private void exportToCSV(int activeCount, int frozenCount, int totalCount) {
        String userHome = System.getProperty("user.home");
        String selectedMonth = monthPicker.getValue();
        Integer selectedYear = yearPicker.getValue();
        String fileName = String.format("SubscriptionStatusReport_%s_%d.csv", selectedMonth, selectedYear);
        String oneDrivePath = Paths.get(userHome, "OneDrive", "Desktop", fileName).toString();
        String desktopPath = Paths.get(userHome, "Desktop", fileName).toString();

        try (FileWriter writer = new FileWriter(oneDrivePath)) {
            writer.append("Status,Count\n");
            writer.append("Active,").append(String.valueOf(activeCount)).append("\n");
            writer.append("Frozen,").append(String.valueOf(frozenCount)).append("\n");
            writer.append("Total,").append(String.valueOf(totalCount)).append("\n");
            writer.append("Date Generated,").append(LocalDate.now().toString()).append("\n");
            System.out.println("Report exported to: " + oneDrivePath);
        } catch (IOException e) {
            System.err.println("Failed to export to OneDrive. Trying desktop path...");
            try (FileWriter writer = new FileWriter(desktopPath)) {
                writer.append("Status,Count\n");
                writer.append("Active,").append(String.valueOf(activeCount)).append("\n");
                writer.append("Frozen,").append(String.valueOf(frozenCount)).append("\n");
                writer.append("Total,").append(String.valueOf(totalCount)).append("\n");
                writer.append("Date Generated,").append(LocalDate.now().toString()).append("\n");
                System.out.println("Report exported to: " + desktopPath);
            } catch (IOException ex) {
                System.err.println("Error exporting report to desktop: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Exports the book loan times report to a CSV file.
     *
     * @param bookLoanTimesReport the book loan times report data
     */
    private void exportBookLoanTimesToCSV(String[] bookLoanTimesReport) {
        String userHome = System.getProperty("user.home");
        String fileName = "BookLoanTimesReport.csv";
        String oneDrivePath = Paths.get(userHome, "OneDrive", "Desktop", fileName).toString();
        String desktopPath = Paths.get(userHome, "Desktop", fileName).toString();

        try (FileWriter writer = new FileWriter(oneDrivePath)) {
            writer.append("loan id,Total Days,Overdue Days\n");

            for (String entry : bookLoanTimesReport) {
                String[] parts = entry.split(":");
                if (parts.length >= 3) {
                	int totalDays = Integer.parseInt(parts[1])+Integer.parseInt(parts[2]);
                    writer.append(parts[0]).append(",")
                          .append(""+totalDays).append(",")
                          .append(parts[2]).append("\n");
                }
            }
            System.out.println("Report exported to: " + oneDrivePath);
        } catch (IOException e) {
            System.err.println("Failed to export to OneDrive. Trying desktop path...");
            try (FileWriter writer = new FileWriter(desktopPath)) {
                writer.append("loan id,Total Days,Overdue Days\n");

                for (String entry : bookLoanTimesReport) {
                    String[] parts = entry.split(":");
                    if (parts.length >= 3) {
                    	int totalDays = Integer.parseInt(parts[1])+Integer.parseInt(parts[2]);
                        writer.append(parts[0]).append(",")
                              .append(""+totalDays).append(",")
                              .append(parts[2]).append("\n");
                    }
                }
                System.out.println("Report exported to: " + desktopPath);
            } catch (IOException ex) {
                System.err.println("Error exporting report to desktop: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

}
