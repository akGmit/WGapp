package wgapp.gui;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import wgapp.client.ConnectionOutput;
import wgapp.client.ConnectionSocket;
import wgapp.client.Events;
import wgapp.client.User;
/**
 * Main UI controller of an application.
 * Using this controller instance app is providing UI functionality.
 * 
 * @author ak
 */
public class MainViewController extends AbstractController implements Initializable{

	@FXML private VBox mainBox;
	@FXML private TextField userName;
	@FXML private TextField workGroupName;
	@FXML private MenuItem createUserMenu;
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
	private EventManager eventManager = new EventManager(this);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		validateClient();
		initConnection();
		showMainStartUI();
		initChatEvents();
		initMainAppEvents();
	}
	//Validate client 
	void validateClient() {
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

		this.connection.addObserver(eventManager);

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
		out.leaveGroup();
		System.out.println(user.toJSON());
		txtaChatDisplay.clear();
		user.setWorkGroup("");
		createGroupMenu.setDisable(false);
		leaveGroupMenu.setDisable(true);
		createUserMenu.setDisable(false);
		showMainStartUI();
	}

	//Show main start UI	
	void showMainStartUI() {
		mainBox.getChildren().remove(mainWGTabPane);
		if(!mainBox.getChildren().contains(lblMainUI) && !mainBox.getChildren().contains(workGroupTable)) {
			mainBox.getChildren().add(lblMainUI);
			mainBox.getChildren().add(workGroupTable);
		}
	}
	//Show main group chat UI and disable create group menu button
	void showMainGroupUI() {
		mainBox.getChildren().remove(lblMainUI);
		mainBox.getChildren().remove(workGroupTable);
		createGroupMenu.setDisable(true);
		createUserMenu.setDisable(true);
		if(!mainBox.getChildren().contains(mainWGTabPane)) {
			mainBox.getChildren().add(mainWGTabPane);
			
		}
	}

	private void setUser(Map<String, String> u) {
		if(u != null) {
			System.out.println(user.toJSON());
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
			System.out.println(user.toJSON());
		}
	}

	public void showCreateGroupPopUp() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateGroupPop.fxml"));
		popupController = new PopUpController(this, loader, Events.NEW_GROUP);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			out.createGroup(this.user);
			showMainGroupUI();
			System.out.println(user.toJSON());
		}
	}

	public void showCreateUserPopUp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("CreateUserPopUp.fxml"));
		popupController = new PopUpController(this, loader, Events.CREATE_USER);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			out.createNewUser(this.user);
			System.out.println(user.toJSON());
		}
		
	}

	private void showEnterWGPasswordPop() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("PasswordPop.fxml"));
		popupController = new PopUpController(this, loader, Events.JOIN_WORKGROUP);

		if(popupController.getResult() != null) {
			setUser(popupController.getResult());
			showMainGroupUI();
			System.out.println(user.toJSON());
		}
	}

	
	//GETTERS SETTERS
	public VBox getMainBox() {
		return mainBox;
	}
	public void setMainBox(VBox mainBox) {
		this.mainBox = mainBox;
	}
	public TextField getUserName() {
		return userName;
	}
	public void setUserName(TextField userName) {
		this.userName = userName;
	}
	public TextField getWorkGroupName() {
		return workGroupName;
	}
	public void setWorkGroupName(TextField workGroupName) {
		this.workGroupName = workGroupName;
	}
	public MenuItem getCreateUserMenu() {
		return createUserMenu;
	}
	public void setCreteUserMenu(MenuItem creteUserMenu) {
		this.createUserMenu = creteUserMenu;
	}
	public TableView<WorkGroupListRow> getWorkGroupTable() {
		return workGroupTable;
	}
	public void setWorkGroupTable(TableView<WorkGroupListRow> workGroupTable) {
		this.workGroupTable = workGroupTable;
	}
	public TableColumn<WorkGroupListRow, String> getTblColWorkGroupName() {
		return tblColWorkGroupName;
	}
	public void setTblColWorkGroupName(TableColumn<WorkGroupListRow, String> tblColWorkGroupName) {
		this.tblColWorkGroupName = tblColWorkGroupName;
	}
	public TableColumn<WorkGroupListRow, Button> getTblColBtnJoinWorkGroup() {
		return tblColBtnJoinWorkGroup;
	}
	public void setTblColBtnJoinWorkGroup(TableColumn<WorkGroupListRow, Button> tblColBtnJoinWorkGroup) {
		this.tblColBtnJoinWorkGroup = tblColBtnJoinWorkGroup;
	}
	public TabPane getMainWGTabPane() {
		return mainWGTabPane;
	}
	public void setMainWGTabPane(TabPane mainWGTabPane) {
		this.mainWGTabPane = mainWGTabPane;
	}
	public Tab getTabChat() {
		return tabChat;
	}
	public void setTabChat(Tab tabChat) {
		this.tabChat = tabChat;
	}
	public ListView<String> getLstvUsers() {
		return lstvUsers;
	}
	public void setLstvUsers(ListView<String> lstvUsers) {
		this.lstvUsers = lstvUsers;
	}
	public TextField getTxtfMessage() {
		return txtfMessage;
	}
	public void setTxtfMessage(TextField txtfMessage) {
		this.txtfMessage = txtfMessage;
	}
	public Label getLblMainUI() {
		return lblMainUI;
	}
	public void setLblMainUI(Label lblMainUI) {
		this.lblMainUI = lblMainUI;
	}
	public TextArea getTxtaChatDisplay() {
		return txtaChatDisplay;
	}
	public void setTxtaChatDisplay(TextArea txtaChatDisplay) {
		this.txtaChatDisplay = txtaChatDisplay;
	}
	public Button getBtnSendMsg() {
		return btnSendMsg;
	}
	public void setBtnSendMsg(Button btnSendMsg) {
		this.btnSendMsg = btnSendMsg;
	}
	public MenuItem getCreateGroupMenu() {
		return createGroupMenu;
	}
	public void setCreateGroupMenu(MenuItem createGroupMenu) {
		this.createGroupMenu = createGroupMenu;
	}
	public MenuItem getLeaveGroupMenu() {
		return leaveGroupMenu;
	}
	public void setLeaveGroupMenu(MenuItem leaveGroupMenu) {
		this.leaveGroupMenu = leaveGroupMenu;
	}
	public MenuItem getLoginMenu() {
		return loginMenu;
	}
	public void setLoginMenu(MenuItem loginMenu) {
		this.loginMenu = loginMenu;
	}
	public ObservableList<WorkGroupListRow> getTblWorkGroupList() {
		return tblWorkGroupList;
	}
	public void setTblWorkGroupList(ObservableList<WorkGroupListRow> tblWorkGroupList) {
		this.tblWorkGroupList = tblWorkGroupList;
	}
	public ObservableList<String> getGroupUserList() {
		return groupUserList;
	}
	public void setGroupUserList(ObservableList<String> groupUserList) {
		this.groupUserList = groupUserList;
	}
	public ConnectionSocket getConnection() {
		return connection;
	}
	public void setConnection(ConnectionSocket connection) {
		this.connection = connection;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ConnectionOutput getOut() {
		return out;
	}
	public void setOut(ConnectionOutput out) {
		this.out = out;
	}
	public PopUpController getPopupController() {
		return popupController;
	}
	public void setPopupController(PopUpController popupController) {
		this.popupController = popupController;
	}
	public Thread getConnectionThread() {
		return connectionThread;
	}
	public void setConnectionThread(Thread connectionThread) {
		this.connectionThread = connectionThread;
	}
	public Thread getOutputThread() {
		return outputThread;
	}
	public void setOutputThread(Thread outputThread) {
		this.outputThread = outputThread;
	}
	public boolean isUserValid() {
		return isUserValid;
	}
	public void setUserValid(boolean isUserValid) {
		this.isUserValid = isUserValid;
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


