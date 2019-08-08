package client;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.mina.core.session.IoSession;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Friend;
import model.Message;
import model.MessageService;
import model.User;
import utils.InversionControl;
import utils.MyAnimation;

public class LaunchClient extends Application {
	Stage stage = null;
	public InversionControl<LaunchClient> viewControl;
	User user = null;// 用于保存登陆信息
	IoSession session = null;// 用于发送信息
	ObservableList<Friend> friends = FXCollections.observableArrayList();// 用于保存返回的好友信息

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Client");
		// primaryStage.setResizable(false);
		MinaClient minaClient = new MinaClient(this);
		minaClient.start();
		this.session = minaClient.getSession();
		this.stage = primaryStage;
		stage.setOnCloseRequest(event -> {
			session.closeNow();
			System.exit(1);
		});
		// stage.setOpacity(0.93);
		showLogin();
		stage.show();
	}

	public void setViewControl(InversionControl<LaunchClient> viewControl) {
		this.viewControl = viewControl;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void addFriend(Friend friend) {
		for (Friend friend2 : friends) {
			if (friend2.getUserId().getValue().equals(friend.getUserId().getValue())) {
				return;
			}
		}
		friends.add(friend);
	}

	public void addFriends(ArrayList<Friend> friends) {
		this.friends.addAll(friends);
	}

	public Friend getFriend(String friendId) {
		// return (Friend) friends.stream().filter(friend ->
		// friend.getUserId().equals(friendId));
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getUserId().getValue().equals(friendId)) {
				return friends.get(i);
			}
		}
		return null;
	}

	public void showLogin() {
		Scene scene = utils.FXMLLoad.load(utils.Source.LOGIN, this);
		scene.getStylesheets().add(LaunchClient.class.getResource("client.css").toExternalForm());
		MyAnimation.showOut(scene.getRoot());
		stage.setScene(scene);
	}

	public void showLogon() {
		Scene scene = utils.FXMLLoad.load(utils.Source.LOGON, this);
		scene.getStylesheets().add(LaunchClient.class.getResource("client.css").toExternalForm());
		MyAnimation.showOut(scene.getRoot());
		stage.setScene(scene);
	}

	public void showChat() {
		Scene scene = utils.FXMLLoad.load(utils.Source.CHAT, this);
		MyAnimation.showOut(scene.getRoot());
		scene.getStylesheets().add(LaunchClient.class.getResource("chat.css").toExternalForm());
		stage.setScene(scene);
	}

	public void showInfo(String show) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("新消息");
		alert.setHeaderText("来自服务器的消息");
		alert.setContentText(show);
		alert.showAndWait();
	}

	public void showBl(Message message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("好友请求");
		alert.setHeaderText("有个好友请求");
		JSONObject json;
		try {
			json = new JSONObject(message.getMeg()[0]);
			alert.setContentText("你想添加" + json.getString("value1") + "(" + message.getFrom() + ")" + "为好友吗？");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() != ButtonType.OK) {
			message.setMeg(null);
		}
		message.setCmd(MessageService.RADD_FRIEND);
		session.write(message);
	}

	public void close() {
		session.closeNow();
		stage.close();
		System.exit(1);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
