package wgapp.gui;

import java.util.ArrayList;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import wgapp.client.Events;
import wgapp.client.User;
import wgapp.inter.Observer;

/**
 * Class responsible for dealing with events from server.
 * 
 * @author ak
 */
public class EventManager implements Observer {
	MainViewController view;
	
	/**
	 * EventManager constructor.
	 * 
	 * @param c Reference to MainViewController object.
	 */
	public EventManager(MainViewController c) {
		view = c;
	}
	
	@Override
	public void update(String event, Object obj) {
		event = event.toLowerCase();
		
		switch(event) {
		case (Socket.EVENT_MESSAGE):
			eventMessage(obj);
			break;
		case (Events.WORKGROUP_LIST):
			eventWorkGroupList(obj);
			break;
		case (Events.LOG_IN):
			eventLogIn(obj);
			break;
		case (Events.USER_JOIN):
			eventUserJoin(obj);
			break;
		case (Events.NEW_USER):
			eventNewUser(obj);
			break;
		case (Events.USER_DISCONNECT):
			eventUserDisconnect(obj);
			break;
		case (Events.ERROR_MSG):
			eventErrorMsg(obj);
			break;
		}
	}
	
	private void eventErrorMsg(Object obj) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				new Alert(AlertType.ERROR, (String)obj, ButtonType.OK).showAndWait();
				view.showMainStartUI();
			}
		});
	}

	private void eventUserDisconnect(Object obj) {
		User userDisconnected = (User) obj;
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				view.getGroupUserList().remove(userDisconnected.getName());
				view.getLstvUsers().setItems(view.getGroupUserList());
			}
		});
	}

	private void eventNewUser(Object obj) {
		boolean isValid = (Boolean) obj;
		if(isValid) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					new Alert(AlertType.INFORMATION, "User account created.", ButtonType.OK).showAndWait();
					view.getWorkGroupTable().setDisable(false);
					view.getLblMainUI().setText("Available work groups list:");
				}
			});
			view.setUserValid(true);
			view.getCreateGroupMenu().setDisable(false);
			view.getLoginMenu().setDisable(true);
			User.getUser().setPassword("");
			view.showMainStartUI();
		}else {
			view.validateClient();
		}
	}

	private void eventUserJoin(Object obj) {
		ArrayList<User> newUserList = (ArrayList<User>) obj;
		ArrayList<String> userNameList = new ArrayList<>();
		for(User u : newUserList) {
			userNameList.add(u.getName());
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				view.getGroupUserList().setAll(userNameList);
				view.getLstvUsers().setItems(view.getGroupUserList());
			}
		});
		view.getCreateGroupMenu().setDisable(true);
		view.getLeaveGroupMenu().setDisable(false);
	}

	private void eventMessage(Object obj) {
		view.getTxtaChatDisplay().appendText((String)obj +"\n");
	}

	private void eventLogIn(Object obj) {
		boolean isValid = (Boolean) obj;
		if(isValid) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					new Alert(AlertType.INFORMATION, "Log in succesful!", ButtonType.OK).showAndWait();
					view.getWorkGroupTable().setDisable(false);
					view.getLblMainUI().setText("Available work groups list:");
				}
			});
			view.setUserValid(true);
			view.getCreateGroupMenu().setDisable(false);
			view.getLoginMenu().setDisable(true);
			User.getUser().setPassword("");
			view.showMainStartUI();
		}else {
			view.validateClient();
		}
		
	}

	private void eventWorkGroupList(Object obj) {
		Platform.runLater( new Runnable() {
			public void run() {
				ArrayList<String> workGroupList = (ArrayList<String>) obj;
				view.getTblWorkGroupList().clear();
				for (String s : workGroupList) {
					view.getTblWorkGroupList().add(view.new WorkGroupListRow(s));
				}
				view.getTblColWorkGroupName().setCellValueFactory(new PropertyValueFactory<>("name"));
				view.getTblColBtnJoinWorkGroup().setCellValueFactory(new PropertyValueFactory<>("joinButton"));
				view.getWorkGroupTable().getColumns().setAll(view.getTblColWorkGroupName(), view.getTblColBtnJoinWorkGroup());
				view.getWorkGroupTable().setItems(view.getTblWorkGroupList());
			}
		});
	}

}
