package wgapp.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wgapp.client.Events;
/**
 * Pop up window controller class.
 * 
 * @author ak
 *
 */
public class PopUpController extends AbstractController implements Initializable {
	@FXML private TextField userName;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	@FXML private TextField wgName;
	@FXML private TextField wgPassword;
	@FXML private PasswordField password;

	private Map<String, String> result = new HashMap<>();
	private Stage popUp = null;
	private String type;
	private AbstractController c;

	public PopUpController(AbstractController c, FXMLLoader loader, String type) {
		this.type = type;
		this.c = c;

		loader.setController(this);
		Parent layout;
		try {
			layout = loader.load();
			Scene d = new Scene(layout);
			popUp = new Stage();

			popUp.setOnCloseRequest((event) -> {
				this.result = null;
			});

			this.setStage(popUp);
			popUp.initOwner(c.mainUI.getPrimaryStage());
			popUp.initModality(Modality.WINDOW_MODAL);
			popUp.setAlwaysOnTop(true);
			popUp.setScene(d);

			this.initialize(null, null);

			popUp.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(type.equalsIgnoreCase(Events.LOG_IN)) {
			btnOK.setOnAction((event) -> {
				result.put("name", userName.getText());
				result.put("password", password.getText());
				closeStage();
			});
		}

		if(type.equalsIgnoreCase(Events.CREATE_USER)) {
			btnOK.setOnAction((event) -> {
				result.put("name", userName.getText());
				result.put("password", password.getText());
				closeStage();
			});
		}

		if(type.equalsIgnoreCase(Events.NEW_GROUP)) {
			btnOK.setOnAction((event) -> {
				createWorkGroup();
				closeStage();
			});
		}

		if(type.equalsIgnoreCase(Events.JOIN_WORKGROUP)) {
			btnOK.setOnAction((event) -> {
				result.put("password", password.getText());
				closeStage();
			});
		}
		
		btnCancel.setOnAction((event) -> {
			this.result = null;
			closeStage();
		});
	}

	private void createWorkGroup() {
		result.put("workGroup", wgName.getText());
		result.put("password", password.getText());
	}

	public Map<String, String> getResult() {
		return this.result;
	}

	public void setStage(Stage stage) {
		this.popUp = stage;
	}

	/**
	 * Closes the stage of this view
	 */
	private void closeStage() {
		if(popUp!=null) {
			popUp.close();
		}
	}

}
