package wgapp.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
	private Stage stage = null;
	private String type;
	
	public PopUpController(String type) {
		this.type = type;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(type.equalsIgnoreCase("createuser")) {
			setUserBtn.setOnAction((event) -> {
				result.put("username", userName.getText());
				closeStage();
			});
		}
		if(type.equalsIgnoreCase("creategroup")) {
			wgCreate.setOnAction((event) -> {
				createWorkGroup();
				closeStage();
			});
			wgCancel.setOnAction((event) -> {
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
		result.put("wgName", wgName.getText());
		result.put("wgPassword", wgPassword.getText());
		result.put("wgDescription", wgDescription.getText());
	}

	public Map<String, String> getResult() {
		return this.result;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Closes the stage of this view
	 */
	private void closeStage() {
		if(stage!=null) {
			stage.close();
		}
	}

}
