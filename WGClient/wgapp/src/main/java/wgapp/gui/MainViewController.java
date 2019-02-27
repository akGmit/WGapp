package wgapp.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import wgapp.client.ConnectionOutput;
import wgapp.client.ConnectionSocket;
import wgapp.client.Events;
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
	@FXML private MenuItem createGroupMenu;
	@FXML private MenuItem leaveGroupMenu;
	@FXML private MenuItem loginMenu;

	private ObservableList<WorkGroupListRow> tblWorkGroupList = FXCollections.observableArrayList();
	private ObservableList<String> groupUserList = FXCollections.observableArrayList();
	private ConnectionSocket connection;
	private User user = User.getUser();
	private ConnectionOutput out;
	private PopUpController popupController;
	private Thread connectionThread;
	private Thread outputThread;
	private boolean isUserValid = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validateClient();
		initConnection();
		showMainStartUI();
		initChatEvents();
		initMainAppEvents();
	}
	//Validate client 
	private void validateClient() {
		if(!isUserValid) {
			workGroupTable.setDisable(true);
			lblMainUI.setText("Login or create new user to access work groups.");
			createGroupMenu.setDisable(true);
			leaveGroupMenu.setDisable(true);
		}
	}

	//Method initializing various main app events
	private void initMainAppEvents() {
		this.mainUI.getPrimaryStage().setOnCloseRequest(event -> {
			endConnection();
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
				txtfMessage.setText("");
			}
		});

		btnSendMsg.setOnAction((event) -> {
			sendMessage(txtfMessage.getText());
			txtfMessage.setText("");
		});
	}

	//Initializing connection
	//Starting server input and outputu threads
	private void initConnection() {
		this.connection = new ConnectionSocket();

		this.connection.addObserver(this);

		ConnectionSocket.connect();

		this.out = new ConnectionOutput();

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
	}
	
	public void groupLeave() {
		
	}

	/**
	 * Observer interface update() method implementation.
	 * 
	 */
	@Override
	public void update(String event, Object obj) {
		if(event.equalsIgnoreCase(Events.WORKGROUP_LIST)) {
			Platform.runLater( new Runnable() {
				public void run() {
					ArrayList<String> workGroupList = (ArrayList<String>) obj;
					tblWorkGroupList.clear();
					for (String s : workGroupList) {
						tblWorkGroupList.add(new WorkGroupListRow(s));
					}
					tblColWorkGroupName.setCellValueFactory(new PropertyValueFactory<>("name"));
					tblColBtnJoinWorkGroup.setCellValueFactory(new PropertyValueFactory<>("joinButton"));
					workGroupTable.getColumns().setAll(tblColWorkGroupName, tblColBtnJoinWorkGroup);
					workGroupTable.setItems(tblWorkGroupList);
				}
			});
		}

		if(event.equalsIgnoreCase(Events.USER_JOIN)) {
			ArrayList<User> newUserList = (ArrayList<User>) obj;
			ArrayList<String> userNameList = new ArrayList<>();
			for(User u : newUserList) {
				userNameList.add(u.getName());
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					groupUserList.setAll(userNameList);
					lstvUsers.setItems(groupUserList);
				}
			});
			createGroupMenu.setDisable(true);
			leaveGroupMenu.setDisable(false);
		}

		if(event.equalsIgnoreCase(Events.LOG_IN)) {
			boolean isValid = (Boolean) obj;
			if(isValid) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new Alert(AlertType.INFORMATION, "Log in succesful!", ButtonType.OK).showAndWait();
						workGroupTable.setDisable(false);
						lblMainUI.setText("Available work groups list:");
					}
				});
				isUserValid = true;
				createGroupMenu.setDisable(false);
				loginMenu.setDisable(true);
				this.user.setPassword("");
				showMainStartUI();
			}else {
				validateClient();
			}
		}

		if(event.equalsIgnoreCase(Events.NEW_USER)) {
			boolean isValid = (Boolean) obj;
			if(isValid) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						new Alert(AlertType.INFORMATION, "User account created.", ButtonType.OK).showAndWait();
						workGroupTable.setDisable(false);
						lblMainUI.setText("Available work groups list:");
					}
				});
				isUserValid = true;
				createGroupMenu.setDisable(false);
				loginMenu.setDisable(true);
				this.user.setPassword("");
				showMainStartUI();
			}else {
				validateClient();
			}
		}


		if(event.equalsIgnoreCase(Socket.EVENT_MESSAGE)) {
			txtaChatDisplay.appendText((String)obj+"\n");
		}

		if(event.equalsIgnoreCase(Events.USER_DISCONNECT)) {
			User userDisconnected = (User) obj;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					groupUserList.remove(userDisconnected.getName());
					lstvUsers.setItems(groupUserList);
				}
			});
		}

		if(event.equalsIgnoreCase(Events.ERROR_MSG)) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					new Alert(AlertType.ERROR, (String)obj, ButtonType.OK).showAndWait();
					showMainStartUI();
				}
			});

		}
	}

	//Show main start UI	
	private void showMainStartUI() {
		mainBox.getChildren().remove(mainWGTabPane);
		if(!mainBox.getChildren().contains(lblMainUI) && !mainBox.getChildren().contains(workGroupTable)) {
			mainBox.getChildren().add(lblMainUI);
			mainBox.getChildren().add(workGroupTable);
		}
	}
	//Show main group chat UI and disable create group menu button
	private void showMainGroupUI() {
		mainBox.getChildren().remove(lblMainUI);
		mainBox.getChildren().remove(workGroupTable);
		createGroupMenu.setDisable(true);
		if(!mainBox.getChildren().contains(mainWGTabPane)) {
			mainBox.getChildren().add(mainWGTabPane);
		}
	}

	private void setUser(Map<String, String> u) {
		if(u != null) {
			User.getUser().setUserData(u);
		}
	}

	//POPUP WINDOW METHODS ======================================

	public void showLoginPopUp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("LogInPopUp.fxml"));
		popupController = new PopUpController(this, loader, Events.LOG_IN);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			out.logIn(this.user);
		}
	}

	public void showCreateGroupPopUp() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateGroupPop.fxml"));
		popupController = new PopUpController(this, loader, Events.NEW_GROUP);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			out.createGroup(this.user);
			showMainGroupUI();
		}
	}

	public void showCreateUserPopUp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("CreateUserPopUp.fxml"));
		popupController = new PopUpController(this, loader, Events.CREATE_USER);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			out.createNewUser(this.user);
		}
	}

	private void showEnterWGPasswordPop() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("PasswordPop.fxml"));
		popupController = new PopUpController(this, loader, Events.JOIN_WORKGROUP);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			showMainGroupUI();
		}
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


