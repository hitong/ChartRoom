package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import model.Message;
import model.MessageService;
import utils.InversionControl;
import utils.MyJson;

public class AddFriendControl implements InversionControl<ChatViewControl> {
	@FXML
	private TextField searchText;

	@FXML
	private StackPane view;

	ChatViewControl main;

	@FXML
	private void search() {
		view.getChildren().clear();
		if (searchText.getText() != null && !searchText.getText().equals("")) {
			main.getMain().session.write(new Message(MessageService.SEARCh_FRIEND, "", new String[] {searchText.getText()},
					main.getMain().user.getUserId(), main.getMain().user.getUserId(), ""));
		}
	}
	
	public void addResult(String id, String name){
		this.view.getChildren().clear();
		if(id != null && name != null){
			HBox tmp = new HBox();
			Text viewText = new Text("         ID: " + id + "    name: " + name + "    op: " );
			Button button = new Button("已添加");
			button.setDisable(true);
			if(main.main.getFriend(id) != null){
				button.setText("已添加该好友");
			} else if(main.main.user.getUserId().equals(id)) {
				button.setText("禁止添加自己");
			}
			else{
				button.setDisable(false);
				button.setId(id);
				button.setText("添加");
				button.setOnAction(event ->{
					main.main.showInfo("请求已经发送！！！");
					main.main.session.write(new Message(MessageService.ADD_FRIEND,"",new String[]{MyJson.changeCmd(main.getMain().user.getName(),name, "").toString()},main.getMain().user.getUserId(),button.getId(),""));
				});
			}
			tmp.getChildren().addAll(viewText,button);
			this.view.getChildren().add(tmp);
		} else {
			this.view.getChildren().add(new Text("未查找到任何用户信息"));
		}
	}

	@Override
	public void setMain(ChatViewControl t) {
		// TODO Auto-generated method stub
		this.main = t;
	}

}
