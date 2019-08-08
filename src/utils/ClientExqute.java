package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import client.AddFriendControl;
import client.ChatViewControl;
import client.ClientHandle;
import client.ContextControl;
import client.LogonViewControl;
import javafx.application.Platform;
import model.DownControl;
import model.Friend;
import model.Message;
import model.MessageService;
import model.User;

public class ClientExqute {

	public static void login(Message message, ClientHandle ch) {
		String meg = message.getMessageId();
		if (meg.equals("succees")) {
			Platform.runLater(() -> {
				ch.getMain().setUser(new User(message.getMeg()[0], message.getMeg()[1], ""));
				ch.getMain().showChat();
			});
		} else if (meg.equals("psw err")) {
			Platform.runLater(() -> {
				ch.getMain().showInfo("密码错误");
			});
		} else if (meg.equals("no user")) {
			Platform.runLater(() -> {
				ch.getMain().showInfo("登陆用户不存在");
			});
		} else if (meg.equals("has loged")) {
			Platform.runLater(() -> {
				ch.getMain().showInfo("该用户已经登陆");
			});
		}
	}

	public static void resultAddFriend(Message message, ClientHandle ch) {
		try {
			JSONObject json = new JSONObject(message.getMeg()[0]);
			Platform.runLater(() -> {
				try {
					if (json.getString("value3").equals("t")) {
						ch.getMain()
								.showInfo("添加好友" + json.getString("value1") + "( " + json.getString("value2") + " )成功");
						ch.getMain().addFriend(new Friend(json.getString("value1"), json.getString("value2")));
					} else if (json.getString("value3").equals("f")) {
						ch.getMain().showInfo("添加好友" + message.getFrom() + "失败");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void friendinfo(Message message, ClientHandle ch) {
		Platform.runLater(() -> {
			Friend friend = new Friend(message.getMeg()[0], message.getMeg()[1]);
			if (Integer.parseInt(message.getMeg()[2]) == 0) {
				friend.setHistory(false);
			} else {
				friend.setHistory(true);
			}
			ch.getMain().addFriend(friend);
		});
	}

	private static ContextControl openChat(String from, ClientHandle ch) {
		ChatViewControl chatViewControl = (ChatViewControl) (ch.getMain().viewControl);
		if (chatViewControl.getChat().containsKey(from)) {
			chatViewControl.addTab(ch.getMain().getFriend(from));
		}
		ContextControl c = chatViewControl.getChat().get(from);
		return c;
	}

	public static void recive(Message message, ClientHandle ch) {
		Platform.runLater(() -> {
			ChatViewControl chatViewControl = (ChatViewControl) (ch.getMain().viewControl);
			ContextControl c = openChat(message.getFrom(), ch);
			if (c == null) {
				chatViewControl.addTab(new Friend(message.getFrom(),
						ch.getMain().getFriend(message.getFrom()).getUserName().getValue()));
				chatViewControl.getChat().get(message.getFrom());
				c = chatViewControl.getChat().get(message.getFrom());
			}
			if (message.getCmd() == MessageService.RHISTORY || message.getCmd() == MessageService.MES) {
				c.reciveMessage(message);
			} else if(message.getCmd() ==MessageService.IMG ){
				try {
					String fileId = message.getMeg()[0];
					File file = new File("userFile/" + fileId);
					file.createNewFile();
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
					out.write(message.getBt());
					out.close();
					c.showImg(file, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				c.file(message);
			}
		});
	}

	public static void varifacation(boolean flag, ClientHandle ch) {
		LogonViewControl c = (LogonViewControl) ch.getMain().viewControl;
		Platform.runLater(() -> {
			c.testResult(flag);
		});
	}

	public static void logon(boolean flag, ClientHandle ch) {
		LogonViewControl c = (LogonViewControl) ch.getMain().viewControl;
		Platform.runLater(() -> {
			c.logonResult(flag);
		});
	}

	public static void searhFriend(Message message, ClientHandle ch) {
		Platform.runLater(() -> {
			AddFriendControl c = ((ChatViewControl) ch.getMain().viewControl).getAddCtl();
			if (message.getMeg() != null) {
				c.addResult(message.getMeg()[0], message.getMeg()[1]);
			} else {
				c.addResult(null, null);
			}
		});
	}

	/**
	 * 
	 * @param message
	 * @param ch
	 */
	public static synchronized void dFile(Message message, ClientHandle ch) {
		Platform.runLater(() -> {
			ChatViewControl c = (ChatViewControl) ch.getMain().viewControl;
			if (message.getMessageId().equals("end")) {
				DownControl dc = ch.controlMeg.get(message.getMeg()[0]);
				dc.setEndArr(true);
				if (dc.canStart()) {
					startDown(dc.getFileId(), ch.cache.get(dc.getFileId()));
					delProcess(dc.getFileId(), ch);
				}
			} else if (message.getMessageId().equals("start")) {
				ch.cache.put(message.getMeg()[0], new HashMap<>(Integer.parseInt(message.getMeg()[1]) * 2));
				ch.controlMeg.put(message.getMeg()[0],
						new DownControl(message.getMeg()[0], message.getFrom(), Integer.parseInt(message.getMeg()[1])));
				c.getChat().get(message.getFrom()).getNewPi(message.getMeg()[0]);
			} else {
				DownControl dc = ch.controlMeg.get(message.getFileId());
				ch.cache.get(dc.getFileId()).put(Integer.parseInt(message.getMessageId()), message.getBt());
				dc.proContinue();
				c.getChat().get(dc.getFrom()).setProcess(dc.getProcess(), dc.getFileId());
				if (dc.canStart()) {
					startDown(dc.getFileId(),ch.cache.get(dc.getFileId()));
					delProcess(dc.getFileId(), ch);
				}
			}
		});
	}

	public static synchronized void dImg(Message message, ClientHandle ch) {
		// System.out.println(message.getMessageId());
		// if (message.getMessageId().equals("end")) {
		// String type = message.getMeg()[2];
		// ch.file = new File("userFile/" + message.getMeg()[0] + "." + type);
		// try {
		// ch.file.createNewFile();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// ch.all = true;
		// if (ch.now == ch.length) {
		// startDownImg(ch);
		// Platform.runLater(() -> {
		// ContextControl c = openChat(ch.from, ch);
		// ChatViewControl chatViewControl = (ChatViewControl)
		// (ch.getMain().viewControl);
		// if (c == null) {
		// chatViewControl.addTab(new Friend(message.getFrom(),
		// ch.getMain().getFriend(message.getFrom()).getUserName().getValue()));
		// chatViewControl.getChat().get(message.getFrom());
		// c = chatViewControl.getChat().get(message.getFrom());
		// }
		// c.showImg(ch.file, true);
		// });
		// ch.all = false;
		// }
		// } else if (message.getMessageId().equals("start")) {
		// ch.cache = new HashMap<>(Integer.parseInt(message.getMeg()[1]) * 2);
		// ch.length = Integer.parseInt(message.getMeg()[1]);
		// ch.now = 0;
		// ch.name = message.getMeg()[0];
		// ch.from = message.getFrom();
		// } else {
		// ch.cache.put(Integer.parseInt(message.getMessageId()),
		// message.getBt());
		// if (ch.all) {
		// startDownImg(ch);
		// Platform.runLater(() -> {
		// ContextControl c = openChat(ch.from, ch);
		// ChatViewControl chatViewControl = (ChatViewControl)
		// (ch.getMain().viewControl);
		// if (c == null) {
		// chatViewControl.addTab(new Friend(message.getFrom(),
		// ch.getMain().getFriend(message.getFrom()).getUserName().getValue()));
		// chatViewControl.getChat().get(message.getFrom());
		// c = chatViewControl.getChat().get(message.getFrom());
		// }
		// c.showImg(ch.file, true);
		// });
		// }
		// }
	}
	
	public static void delProcess(String fileId,ClientHandle ch){
		ch.cache.remove(fileId);
		ch.controlMeg.remove(fileId);
	}

	private static void startDown(String fileId, HashMap<Integer, byte[]> map) {
		try {
			File file = new File("userFile/" + fileId);
			file.createNewFile();
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			int size = map.size();
			for (int i = 0; i < size; i++) {
				out.write(map.get(i));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addFriend(Message message, ClientHandle ch) {
		Platform.runLater(() -> {
			ch.getMain().showBl(message);
		});
	}
}
