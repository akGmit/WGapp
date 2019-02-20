package wgapp.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX launcher class.
 * Loads main .fxml file and holds a primary stage of application UI on which all other components are built.
 * @author ak
 *
 */
public class UI extends Application {
	private Stage primaryStage;
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainUI.fxml"));
        MainViewController mainViewController = new MainViewController();
        mainViewController.setMainApp(this);
        loader.setController(mainViewController);
        Parent layout = loader.load();
        
        Scene scene = new Scene(layout);
        
        primaryStage.setScene(scene);
        primaryStage.show();
	}

	/**
	 * Returns primary stage of JavaFX application.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Application exit method.
	 */
	public void exit() {
		try {
			super.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(UI.class, args);
	}
}
