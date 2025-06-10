package Server;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.ServerPortController;

/**
 * The ServerUI class is the main entry point for the server application.
 * It extends the JavaFX Application class and provides methods to start the server.
 */
public class ServerUI extends Application {
    /**
     * The default port number for the server.
     */
    final public static int DEFAULT_PORT = 55555;

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args the command line arguments
     * @throws Exception if an error occurs during application launch
     */
    public static void main(String args[]) throws Exception {
        launch(args);
    } // end main

    /**
     * Starts the JavaFX application and initializes the server port controller.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during application start
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerPortController aFrame = new ServerPortController();
        aFrame.start(primaryStage);
    }

    /**
     * Runs the server on the specified port.
     *
     * @param p the port number as a string
     */
    public static void runServer(String p) {
        int port = 0; // Port to listen on

        try {
            port = Integer.parseInt(p); // Set port to 5555
        } catch (Throwable t) {
            System.out.println("ERROR - Could not connect!");
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); // Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }
}
