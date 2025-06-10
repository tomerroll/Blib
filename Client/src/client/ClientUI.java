package client;

import common.ClientIpController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
	public static ClientController chat; // only one instance

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		//String serverIp = java.net.InetAddress.getLocalHost().getHostAddress();
		//chat = new ClientController("localhost", 55555);
		// TODO Auto-generated method stub

		ClientIpController aFrame = new ClientIpController(); // create SubscriberFrame

		aFrame.start(primaryStage);
	}
}
