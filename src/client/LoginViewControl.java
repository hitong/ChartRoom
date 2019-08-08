package client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Message;
import model.MessageService;
import model.User;
import utils.InversionControl;

public class LoginViewControl implements InversionControl<LaunchClient>{
	@FXML
	private TextField id;
	
	@FXML
	private PasswordField psw;
	
	public void Login(){
		User user = new User();
		user.setUserId(this.id.getText());
		user.setPsw(this.psw.getText());
		main.session.write(new Message(MessageService.LOGIN, "", new String[]{psw.getText()}, id.getText(), "", ""));
	}
	
	public void Logon(){
		main.showLogon();
	}
	
	
	LaunchClient main;
	public void setMain(LaunchClient main){
		this.main = main;
	}
}
