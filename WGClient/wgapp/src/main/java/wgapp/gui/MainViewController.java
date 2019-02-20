package wgapp.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wgapp.client.ConnectionOutput;
import wgapp.client.ConnectionSocket;
import wgapp.client.User;
import wgapp.inter.Observer;
/**
 * Main UI controller of an application.
 * Using this controller instance app is providing UI functionality.
 * 
 * @author ak
 *
 */
public class MainViewController extends AbstractController implements Initializable, Observer{
	
	@FXML private VBox mainBox;
	@FXML private TextField userName;
	@FXML private TextField workGroupName;
	@FXML private MenuItem creteUserMenu;
	@FXML private TableView<WorkGroupListRow> workGroupTable;
	@FXML private TableColumn<WorkGroupListRow, String> tblColWorkGroupName;
	@FXML private TableColumn<WorkGroupListRow, Button> tblColBtnJoinWorkGroup;
	@FXML private TabPane mainWGTabPane;
	@FXML private Tab tabChat;
	@FXML private ListView<String> lstvUsers;
	@FXML private TextField txtfMessage;
	@FXML private Label lblMainUI;
	@FXML private TextArea txtaChatDisplay;
	@FXML private Button btnSendMsg;

	private ObservableList<WorkGroupListRow> tblWorkGroupList = FXCollections.observableArrayList();
	private ObservableList<String> groupUserList = FXCollections.observableArrayList();
	private ConnectionSocket connection;
	private User user = User.getUser();
	private ConnectionOutput out;
	private PopUpController popupController;
	private Thread connectionThread;
	private Thread outputThread;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setMainUIView(true, false);
		initChatEvents();
		initConnection();
		initMainAppEvents();
		out.getWorkGroupList();
	}
	
	//Method initializing various main app events
	private void initMainAppEvents() {
		this.mainUI.getPrimaryStage().setOnCloseRequest(event -> {
			endConnection();
			System.out.println("here");
			this.mainUI.getPrimaryStage().close();
			this.mainUI.exit();
		});
	}
	
	/**
	 * Send message method used to delegate message sending to OutputConnection class instance.
	 * @param message String representation of message
	 */
	public void sendMessage(String message) {
		if(!message.isEmpty()) {
			out.sendMessage(this.user.getName() +"> " + message);
		}
	}
	
	//Setting of various chat window events
	private void initChatEvents() {
		txtfMessage.setOnKeyPressed(event -> {
			if(event.getCode() == KeyCode.ENTER){
				sendMessage(txtfMessage.getText());
			}
		});

		btnSendMsg.setOnAction((event) -> {
			sendMessage(txtfMessage.getText());
		});
	}
	
	//Initializing connection
	//Starting server input and outputu threads
	private void initConnection() {
		this.connection = new ConnectionSocket();
		
		this.connection.addObserver(this);
		
		ConnectionSocket.connect();
		
		this.out = new ConnectionOutput(user);
		
		connectionThread = new Thread(connection);
		connectionThread.setName("servConnectionThread");
		connectionThread.start();
		outputThread = new Thread(out);
		outputThread.setName("outToServThread");
		outputThread.start();
		
		out.getWorkGroupList();
	}
	
	//Close connection method
	private void endConnection() {
		ConnectionSocket.disconnect();
		connectionThread.interrupt();
		outputThread.interrupt();
		try {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Observer interface update() method implementation.
	 * 
	 */
	@Override
	public void update(String event, Object obj) {
		if(event.equalsIgnoreCase("get_workgroup_list")) {
			Platform.runLater( new Runnable() {
				public void run() {
					ArrayList<String> workGroupList = (ArrayList<String>) obj;
					tblWorkGroupList.clear();
					for (String s : workGroupList) {
						tblWorkGroupList.add(new WorkGroupListRow(s));
					}
					System.out.println("main update");
					System.out.println(workGroupList);
					tblColWorkGroupName.setCellValueFactory(new PropertyValueFactory<>("name"));
					tblColBtnJoinWorkGroup.setCellValueFactory(new PropertyValueFactory<>("joinButton"));
					workGroupTable.getColumns().setAll(tblColWorkGroupName, tblColBtnJoinWorkGroup);
					workGroupTable.setItems(tblWorkGroupList);
				}
			});
		}

		if(event.equalsIgnoreCase("newuser")) {
			ArrayList<User> newUserList = (ArrayList<User>) obj;
			ArrayList<String> userNameList = new ArrayList<>();
			for(User u : newUserList) {
				userNameList.add(u.getName());
			}
			System.out.println("Main view");
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					groupUserList.setAll(userNameList);
					lstvUsers.setItems(groupUserList);
				}				
			});
		}

		if(event.equalsIgnoreCase(Socket.EVENT_MESSAGE)) {
			txtaChatDisplay.appendText((String)obj+"\n");
		}
		
		if(event.equalsIgnoreCase("user_disconnect")) {
			User userDisconnected = (User) obj;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					groupUserList.remove(userDisconnected.getName());
					lstvUsers.setItems(groupUserList);
				}
			});
		}
	}

	//Switch between main start UI to tabbed group chat UI
	private void setMainUIView(boolean startUI, boolean wgUI) {
		if(startUI && workGroupTable.isVisible()) {
			mainBox.getChildren().remove(mainWGTabPane);
		}
		if(startUI && !workGroupTable.isVisible()) {
			mainBox.getChildren().add(workGroupTable);
			mainBox.getChildren().add(lblMainUI);

			mainBox.getChildren().remove(mainWGTabPane);
		}
		if(wgUI) {
			mainBox.getChildren().remove(workGroupTable);
			mainBox.getChildren().remove(lblMainUI);

			mainBox.getChildren().add(mainWGTabPane);
		}
	}

	//POPUP WINDOW METHODS ======================================

	public void showCreateGroupPopUp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("CreateGroupPop.fxml"));
		// initializing the controller
		popupController = new PopUpController("creategroup");
		loader.setController(popupController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// now
			popupController.setStage(popupStage);
			if(this.mainUI!=null) {
				popupStage.initOwner(mainUI.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			new Alert(AlertType.ERROR, "There was an error trying to load the popup fxml file.").show();
		}
		this.user.setIsAdmin(true);
		this.user.setPassword(popupController.getResult().get("wgPassword"));
		this.user.setWorkGroup(popupController.getResult().get("wgName"));

		out.createGroup(this.user);

		setMainUIView(false, true);

	}

	public void showCreateUserPopUp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("UserNamePop.fxml"));
		// initializing the controller
		popupController = new PopUpController("createuser");
		loader.setController(popupController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// now
			popupController.setStage(popupStage);
			if(this.mainUI!=null) {
				popupStage.initOwner(mainUI.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			new Alert(AlertType.ERROR, "There was an error trying to load the popup fxml file.").show();
		}
		this.user.setName(popupController.getResult().get("username"));
	}

	private void showEnterWGPasswordPop() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("PasswordPop.fxml"));
		// initializing the controller
		popupController = new PopUpController("password");
		loader.setController(popupController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// now
			popupController.setStage(popupStage);
			if(this.mainUI!=null) {
				popupStage.initOwner(mainUI.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			new Alert(AlertType.ERROR, "There was an error trying to load the popup fxml file.").show();
		}
		this.user.setPassword(popupController.getResult().get("password"));

		setMainUIView(false, true);
	}

	/**
	 * Inner class JoinButton extending Button.
	 * Instances of this class are used to view "Join" button and provide 
	 * joining to group function.
	 * 
	 * @author ak
	 *
	 */
	public class JoinButton extends Button{
		private String groupName;
		public JoinButton(String group) {
			this.groupName = group;
			setText("Join");
			setOnAction((event) -> {
				showEnterWGPasswordPop();
				user.setWorkGroup(groupName);
				out.joinGroup(user);
			});
		}
	}
	
	/**
	 * Inner class representing individual row in main starting UI Table of work groups.
	 * 
	 * @author ak
	 *
	 */
	public class WorkGroupListRow {
		private SimpleStringProperty name;
		private SimpleObjectProperty<JoinButton> joinButton;

		public WorkGroupListRow(String group) {
			this.name = new SimpleStringProperty(group);
			this.joinButton = new SimpleObjectProperty<JoinButton>(new JoinButton(group));
		}

		public String getName() {
			return this.name.get();
		}

		public JoinButton getJoinButton() {
			return this.joinButton.get();
		}

		public SimpleStringProperty getWgName() {
			return name;
		}

		public void setWgName(SimpleStringProperty wgName) {
			this.name = wgName;
		}

		public void setJoinButton(SimpleObjectProperty<JoinButton> joinButton) {
			this.joinButton = joinButton;
		}
	}
}


