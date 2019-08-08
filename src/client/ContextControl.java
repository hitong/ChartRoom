package client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Friend;
import model.Message;
import model.MessageService;
import utils.InversionControl;
import utils.MyAnimation;

public class ContextControl implements InversionControl<ChatViewControl> {
	@FXML
	private TextArea input;

	@FXML
	private ScrollPane sp;

	@FXML
	private FlowPane vbox;

	private int numOfSendFile = 0;

	public int getNumOfSendFile() {
		return numOfSendFile;
	}

	ChatViewControl main;
	Friend friend;
	HashMap<String, ProgressIndicator> pis = new HashMap<>();//key为文件id，多个下载的文件指示器

	public ProgressIndicator getNewPi(String id) {
		numOfSendFile++;
		ProgressIndicator pi = new ProgressIndicator(0);
		Platform.runLater(() -> {
			vbox.getChildren().add(pi);
		});
		pis.put(id, pi);
		updateViewLocal();
		return pi;
	}

	public void setProcess(double length, String id) {
		Platform.runLater(() -> {
			pis.get(id).setProgress(length);
			if (length == 1) {
				delProcess(id);
			}
			updateViewLocal();
		});
	}

	private void delProcess(String id) {
		numOfSendFile--;
		pis.remove(id);
	}

	@FXML
	private void sendFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ALL files (*.*)", "*.*");
		fileChooser.getExtensionFilters().add(extFilter);
		// main.getMain().session.write();
		File path = fileChooser.showOpenDialog(new Stage());
		if (path == null) {
			return;
		}
		String name = path.getName();
		String type = name.substring(name.lastIndexOf(".") + 1, name.length());
		this.vbox.getChildren()
				.add(new Text(main.getMain().user.getName() + "(" + utils.DateUtil.getNowTime() + "):  "));
		new Thread(() -> {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
				byte[] tmp = new byte[1024 * 4];
				int length;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				LinkedList<byte[]> linkedList = new LinkedList<>();
				try {
					while ((length = in.read(tmp)) != -1) {
						byteArrayOutputStream.write(tmp, 0, length);
						linkedList.add(byteArrayOutputStream.toByteArray());
						byteArrayOutputStream.reset();
					}
				} catch (Exception e) {
					Platform.runLater(() -> main.getMain().showInfo("上传文件过大！！！"));
					in.close();
					return;
				}
				in.close();
				byteArrayOutputStream.close();
				numOfSendFile++;
				final ProgressIndicator pi = new ProgressIndicator(0);
				Text text = new Text("文档正在发送");
				pi.setId("0");
				Platform.runLater(() -> {
					this.vbox.getChildren().addAll(text, pi);
				});
				final String fielId = main.main.user.getUserId() + System.nanoTime() + "." + type;
				main.getMain().session.write(new Message(MessageService.FILE, "start",
						new String[] { 0 + "", linkedList.size() + "", fielId }, main.getMain().user.getUserId(),
						friend.getUserId().getValue(), ""));
				for (int i = 0; i < linkedList.size(); i++) {
					main.getMain().session.write(new Message(MessageService.FILE, i + "", fielId, linkedList.get(i)));
					Platform.runLater(() -> {
						int f = Integer.parseInt(pi.getId()) + 1;
						pi.setProgress(f * 1.0 / linkedList.size() * 1.0);
						pi.setId("" + f);
					});
				}
				numOfSendFile--;
				Platform.runLater(() -> {
					text.setText("文件发送完成");
				});
				main.getMain().session.write(
						new Message(MessageService.FILE, "end", new String[] { 0 + "", linkedList.size() + "", fielId },
								main.getMain().user.getUserId(), friend.getUserId().getValue(), ""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

	private FileChooser fileChooser = new FileChooser();
	@FXML
	private void sendImg() {
		File path = fileChooser.showOpenDialog(new Stage());
		if (path == null) {
			return;
		}
		fileChooser.setInitialDirectory(path.getParentFile());
		String name = path.getName();
		String type = name.substring(name.lastIndexOf(".") + 1, name.length());
		new Thread(() -> {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
				byte[] tmp = new byte[1024 * 1024 * 50]; // 50MB的图片缓存，大缓存，方便图片一次发送
				int length;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				length = in.read(tmp);
				byteArrayOutputStream.write(tmp, 0, length);
				if (in.read(tmp) != -1) {
					main.main.showInfo("请选择不大于50MB的图片");
					in.close();
					byteArrayOutputStream.close();
					return;
				}
				final String fielId = main.main.user.getUserId() + System.nanoTime() + "." + type;
				main.getMain().session.write(
						new Message(MessageService.IMG, "", new String[] { fielId }, main.getMain().user.getUserId(),
								friend.getUserId().getValue(), "", byteArrayOutputStream.toByteArray()));
				in.close();
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		showImg(path, false);
	}

	public void showImg(File path, boolean isFriend) {
		Image img = new Image(path.getAbsoluteFile().toURI().toString());
		ImageView iv = new ImageView(img);
		double width = img.getWidth() / (img.getHeight() + img.getWidth());
		double height = 1 - width;
		iv.setFitWidth(700 * width);
		iv.setFitHeight(700 * height);
		Platform.runLater(() -> {
			if (!isFriend) {
				this.vbox.getChildren()
						.add(new Text(main.getMain().user.getName() + "(" + utils.DateUtil.getNowTime() + "):  "));
			} else {
				this.vbox.getChildren()
						.add(new Text(friend.getUserName().getValue() + "(" + utils.DateUtil.getNowTime() + "):  "));
			}
			MyAnimation.showOut(iv.getParent());
			vbox.getChildren().add(iv);
		});
		updateViewLocal();
	}

	@FXML
	private void showHistory() {

	}

	public void file(Message message) {
		this.vbox.getChildren().add(new Text(friend.getUserName().getValue() + "(" + message.getData() + "): "));
		VBox tmp = new VBox();
		try {
			final JSONObject json = new JSONObject(message.getMeg()[0]);
			double len = Integer.parseInt(json.getString("value2")) * 4.0;
			String strLen = len < 1024 ? len + "KB" : len / 1024 + "MB";

			tmp.getChildren().add(new Text("名称：" + json.getString("value1") + "大小：" + strLen));
			Button bt = new Button("开始下载");
			bt.setId(json.getString("value1"));
			bt.setOnAction(event -> {
				try {
					main.getMain().session
							.write(new Message(MessageService.DFILE, "", new String[] { json.getString("value1") },
									friend.getUserId().getValue(), main.getMain().user.getUserId(), ""));
					bt.setDisable(true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			tmp.getChildren().add(bt);
			this.vbox.getChildren().add(tmp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void sendMessage() {
		Text t = new Text(main.getMain().user.getName() + "(" + utils.DateUtil.getNowTime() + "):  ");
		this.vbox.getChildren()
				.add(t);
		Message m = new Message(MessageService.MES, "", new String[] { input.getText() },
				main.getMain().user.getUserId(), friend.getUserId().getValue(), "");
		TextArea tmp = new TextArea();
//		tmp.setText(m.getMeg()[0]);
		MyAnimation.printMeg(m.getMeg()[0],tmp);
		tmp.setEditable(false);
		// tmp.setPrefSize(700, 300);
		// tmp.setWrapText(false);
		// double width = 1;
		// tmp.setMaxWidth(700);
		tmp.autosize();
		// tmp.setPrefWidth(1);
		// tmp.setStyle(" -fx-background-image: url('client/c.png')");
		this.vbox.getChildren().add(tmp);
		// while(tmp.getScrollTop() > 0 && width < 700){
		// tmp.setPrefWidth(width += 10);
		// }
		tmp.setWrapText(true);
		main.getMain().session.write(m);
		input.setText("");
		updateViewLocal();
	}

	private Thread waitView = new Thread();//让界面数值更新在界面重绘过后

	private void updateViewLocal() {
		if (waitView.isAlive()) {
			return;
		} else {
			waitView = new Thread(() -> {
				try {
					Thread.sleep(90);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Platform.runLater(() -> sp.setVvalue(1));
			});
			waitView.start();
		}
	}

	public void reciveMessage(Message message) {
		this.vbox.getChildren().add(new Text(friend.getUserName().getValue() + "(" + message.getData() + "): "));
		TextArea tmp = new TextArea();
		tmp.setText(message.getMeg()[0]);
		tmp.setEditable(false);
		tmp.setWrapText(true);
		tmp.setPrefSize(500, message.getMeg()[0].length() / 50 * 15);
		MyAnimation.printMeg(message.getMeg()[0],tmp);
		this.vbox.getChildren().add(tmp);
		updateViewLocal();
	}

	@Override
	public void setMain(ChatViewControl t) {
		// TODO Auto-generated method stub
		this.main = t;
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("图片文件", "*.jpg","*.gif", "*.bmp", "*.png"))	;
	}

	public void setFriend(Friend friend) {
		this.friend = friend;
	}
}
