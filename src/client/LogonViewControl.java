package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Message;
import model.MessageService;
import model.User;
import utils.InversionControl;

public class LogonViewControl implements InversionControl<LaunchClient> {
	@FXML
	private TextField id;

	@FXML
	private TextField name;

	@FXML
	private TextField psw;

	private boolean hasUsed = false;

	public void change() {
		hasUsed = false;
	}

	public void sure() {
		if (hasUsed) {
			if (this.id.getText() != null && this.psw.getText() != null) {
				User user = new User();
				user.setUserId(this.id.getText());
				user.setName(name.getText());
				user.setPsw(psw.getText());
				main.session.write(new Message(MessageService.LOGON, "",
						new String[] { user.getUserId(), user.getName(), user.getPsw() }, user.getUserId(),
						user.getUserId(), ""));
			}
		} else {
			main.showInfo("请验证ID可用性");
		}
	}
	
	public void logonResult(boolean flag){
		if(flag){
			main.showInfo("恭喜你，注册成功了！快去登陆体验吧！！！");
			main.showLogin();
		} else {
			main.showInfo("注册失败，请切换信息重新注册！！！");
		}
	}

	public void cancel() {
		main.showLogin();
	}

	public void testId() {
		String tmp = this.id.getText();
		if (tmp != null && tmp.length() > 0) {
			main.session.write(new Message(MessageService.VARFICATION, tmp, false));
		}
	}

	public void testResult(boolean flag) {
		this.hasUsed = flag;
		if (flag) {
			main.showInfo("这个ID可以使用");
		} else {
			main.showInfo("这个ID已经被使用");
		}
	}

	LaunchClient main;

	public void setMain(LaunchClient main) {
		this.main = main;
	}
}
