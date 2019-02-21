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
/**
 * Pop up window controller class.
 * 
 * @author ak
 *
 */
public class PopUpController extends AbstractController implements Initializable {
	@FXML private TextField userName;
	@FXML private Button setUserBtn;
	@FXML private Button wgCreate;
	@FXML private Button wgCancel;
	@FXML private TextField wgName;
	@FXML private TextField wgPassword;
	@FXML private TextField wgDescription;
	@FXML private PasswordField password;
	@FXML private Button btnPassword;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		if(type.equalsIgnoreCase("createuser")) {
			setUserBtn.setOnAction((event) -> {
				result.put("name", userName.getText());
				closeStage();
			});
		}
		
		if(type.equalsIgnoreCase("creategroup")) {
			wgCreate.setOnAction((event) -> {
				createWorkGroup();
				closeStage();
			});
			wgCancel.setOnAction((event) -> {
				this.result = null;
				closeStage();
			});
			
			
		}
		if(type.equalsIgnoreCase("password")) {
			btnPassword.setOnAction((event) -> {
				result.put("password", password.getText());
				closeStage();
			});
		}
		
	}
	
	private void createWorkGroup() {
		result.put("workGroup", wgName.getText());
		result.put("password", wgPassword.getText());
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
