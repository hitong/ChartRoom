package client;

import java.util.HashMap;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Friend;
import model.Message;
import model.MessageService;
import utils.FXMLLoad;
import utils.InversionControl;

public class ChatViewControl implements InversionControl<LaunchClient> {
	@FXML
	private Button name;

	@FXML
	private Button search;

	@FXML
	private TabPane tab;

	LaunchClient main;
	ChatViewControl t = this;
	AddFriendControl addFriendControl;
	HashMap<String, ContextControl> chat = new HashMap<>();
	
	public HashMap<String, ContextControl> getChat(){
		return chat;
	}
	
	public void setAddCtl(AddFriendControl addFriendControl){
		this.addFriendControl = addFriendControl;
	}
	
	public AddFriendControl getAddCtl(){
		return addFriendControl;
	}

	@FXML
	private TableView<Friend> table;

	@FXML
	private TableColumn<Friend, String> viewName;

	@FXML
	private TableColumn<Friend, String> viewState;

	@FXML
	public void add(){
		Parent parent = FXMLLoad.load(utils.Source.ADDFRIEND, this, null);
		Stage stage = new Stage();
		stage.setScene(new Scene(parent));
		stage.show();
	}
	
	/**
	 * 事件响应
	 * @param friend
	 */
	public void addTab(Friend friend) {
		if(chat.containsKey(friend.getUserId().getValue())){
			return;
		}
		Tab tmpTab = new Tab();
		tmpTab.setText(friend.getUserName().getValue());
		Button closeButton = new Button("x");
		closeButton.setOnMouseClicked(event ->{
			if(chat.get(friend.getUserId().getValue()).getNumOfSendFile() == 0){
				tab.getTabs().remove(tmpTab);
				chat.remove(friend.getUserId().getValue());
			} else {
				main.showInfo("当前聊天有文件正在发送，请不要关闭聊天窗口");
			}
		});
		tmpTab.setGraphic(closeButton);
		tmpTab.setClosable(true);
		tmpTab.setContent(utils.FXMLLoad.load(utils.Source.CONTEXT, this,friend));
		tmpTab.getContent().prefWidth(1920);
		tmpTab.getContent().maxWidth(9999);
		tab.getTabs().add(tmpTab);
	}
	
	/**
	 * 会话创建调用
	 * @param friend
	 * @param c
	 */
	public void addChat(String id,ContextControl c){
		chat.put(id,c);
	}

	@FXML
	private void change() {
		main.session.write(new Message(MessageService.LOGOUT,main.user.getUserId(),false));
		main.friends.clear();
		main.showLogin();
	}

	@FXML
	private void close() {
		Set<String> set = this.chat.keySet();
		set.forEach(n -> {
			if(chat.get(n).getNumOfSendFile() != 0){
				main.showInfo("当前有文件正在传输，请不要关闭程序！");
				return;
			}
		});
		main.close();
	}

	@FXML
	private void initialize() {
//		viewName.setCellValueFactory(cellData -> cellData.getValue().getUserName());
		viewState.setCellValueFactory(cellData -> cellData.getValue().getUserId());
		viewName.setCellFactory((col) -> {
			TableCell<Friend, String> cell = new TableCell<Friend, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty) {
                    	 Friend friend = this.getTableView().getItems().get(this.getIndex());
//                    	ImageView imageView = new ImageView(new Image("img/d.png"));
//                    	imageView.setFitWidth(20);
//                    	imageView.setFitHeight(20);
                        Button delBtn = new Button(friend.getUserName().getValue());
                        if(friend.getHistory()){
                        	delBtn.setStyle("-fx-background-color: red");
                        }
                        delBtn.setMinSize(137, 20);
                        delBtn.setMaxSize(137, 20);
                        this.setGraphic(delBtn);
                        delBtn.setOnMouseClicked((me) -> {
                        	Friend clicked = this.getTableView().getItems().get(this.getIndex());
                        	if(friend.getHistory()){
                        		main.session.write(new Message(MessageService.HISTORY,"",null,friend.getUserId().getValue(),main.user.getUserId(),""));
                        		delBtn.setStyle("");
                        	}
                        	t.addTab(clicked);
                        });
                    }
                }
            };
            return cell;
		});
	}

	@Override
	public void setMain(LaunchClient t) {
		// TODO Auto-generated method stub
		main = t;
		name.setText(main.user.getName());
		table.setItems(main.friends);
	}

	public LaunchClient getMain() {
		return main;
	}
}
