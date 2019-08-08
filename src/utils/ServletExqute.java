package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import model.Message;
import model.MessageService;
import model.Relation;
import model.RelationService;
import model.User;
import model.UserService;
import servlet.LogedUser;
import servlet.ServerHandle;

public class ServletExqute {
	private static ServletExqute servletExqute = new ServletExqute();

	private ServletExqute() {
		// TODO Auto-generated constructor stub
	}

	public static ServletExqute getInstance() {
		return servletExqute;
	}

	/**
	 * 登陆信息
	 * 
	 * @param userId
	 * @param psw
	 * @param session
	 */
	public synchronized void login(String userId, String psw, IoSession session) {
		LogedUser logedUser = LogedUser.getInstance();
		if (!logedUser.hasUser(userId)) {
			UserService userService = UserService.getInstance();
			if (userService.verificationUser(userId, psw)) {
				ArrayList<Relation> relations = RelationService.getInstance().getRelations(userId);
				User user = userService.selectUser(userId);
				ArrayList<User> friends = new ArrayList<>(relations.size());
				if (relations.size() > 0) {
					for (int i = 0; i < relations.size(); i++) {
						friends.add(userService
								.selectUser(RelationService.getFriendId(relations.get(i), user.getUserId())));
					}
				}
				logedUser.putUser(userId, session);
				session.setAttribute("id", userId);
				LogedUser.sendMessage(new Message(MessageService.RLOGIN, "succees",
						new String[] { userId, user.getName() }, userId, userId, new Date().toString()), session);
				for (User friend : friends) {
					friendInfo(friend, friend.getUserId(), userId, session);
				}
				List<Message> newFriends = MessageService.getInstance().getNewFriendMessage(userId);
				for (Message newFriend : newFriends) {
					LogedUser.sendMessage(newFriend, session);
					MessageService.getInstance().removeMessage(newFriend);
				}
				// new Thread(new Runnable() {
				// public void run(){
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// List<Message> history =
				// MessageService.getInstance().getMessage(userId);
				// MessageService messageService = MessageService.getInstance();
				// for(int i = 0; i < history.size() && session.isActive();
				// i++){
				// logedUser.sendMessage(history.get(i),session);
				// messageService.removeMessage(history.get(i));
				// }
				// }
				// }).start();
			} else if (userService.selectUser(userId) == null) {
				LogedUser.sendMessage(
						new Message(MessageService.RLOGIN, "no user", null, userId, userId, new Date().toString()),
						session);
			} else {
				LogedUser.sendMessage(
						new Message(MessageService.RLOGIN, "psw err", null, userId, userId, new Date().toString()),
						session);
			}
		} else {
			LogedUser.sendMessage(
					new Message(MessageService.RLOGIN, "has loged", null, userId, userId, new Date().toString()),
					session);
		}
	}

	public static void friendInfo(User friend, String from, String to, IoSession session) {
		MessageService messageService = MessageService.getInstance();
		List<Message> message = messageService.getMessage(from, to);
		LogedUser.sendMessage(
				new Message(MessageService.FRIENDINFO, "",
						new String[] { friend.getUserId(), friend.getName(), message.size() + "" }, from, to, ""),
				session);
		if (message.size() == 1 && message.get(0).getCmd() == MessageService.RADD_FRIEND) {
			messageService.removeMessage(message.get(0));
		}
	}

	/**
	 * 历史信息请求
	 * 
	 * @param from
	 * @param to
	 * @param session
	 */
	public synchronized void history(String from, String to, IoSession session) {
		List<Message> messages = MessageService.getInstance().getMessage(from, to);
		MessageService messageService = MessageService.getInstance();
		for (int i = 0; i < messages.size() && session.isActive(); i++) {
			Message message = messages.get(i);
			if (message.getCmd() == MessageService.IMG) {
				String filePath = "serverletFile/" + message.getMeg()[0];
				try {
					byte[] tmp = new byte[1024 * 1024 * 50]; // 50MB的图片缓存，大缓存，方便图片一次发送
					int length;
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					length = in.read(tmp);
					byteArrayOutputStream.write(tmp, 0, length);
					message.setBt(byteArrayOutputStream.toByteArray());
					in.close();
					byteArrayOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LogedUser.sendMessage(message, session);
			messageService.removeMessage(messages.get(i));
		}
	}

	/**
	 * 文件上传
	 * 
	 * @param message
	 *            Message::Cmd,messageId(start,end),meg(0.startindex,1.len,2.fileId),from,to,date
	 *            Message::Cmd,messageId(index),messageId(index),fileId,len
	 * @param sh
	 */
	public synchronized void file(Message message, ServerHandle sh) {
		if (message.getMessageId().equals("end")) {
			int start = Integer.parseInt(message.getMeg()[0]);
			int end = Integer.parseInt(message.getMeg()[1]);
			String fileId = message.getMeg()[2];
			try {
				File file = new File("serverletFile/" + fileId);
				file.createNewFile();
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				for (int i = start; i < end; i++) {
					out.write(sh.cache.get(fileId).get(i));
				}
				sh.cache.remove(fileId);
				out.close();
				message.setMeg(new String[] { MyJson.changeCmd(fileId, message.getMeg()[1], "").toString() });// 将数据转换为json，用于离线消息存储
				LogedUser.getInstance().sendMessage(message);// 通知接受方接收
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (message.getMessageId().equals("start")) {
			sh.cache.put(message.getMeg()[2], new HashMap<Integer, byte[]>(Integer.parseInt(message.getMeg()[1]) * 2));
		} else {
			sh.cache.get(message.getFileId()).put(Integer.parseInt(message.getMessageId()), message.getBt());
		}
	}

	public void dFile(Message message, IoSession session) {
		new Thread(() -> {
			// main.getMain().session.write();
			File path = new File("serverletFile/" + message.getMeg()[0]);
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
				byte[] tmp = new byte[1024 * 4];
				int length;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				LinkedList<byte[]> linkedList = new LinkedList<>();
				while ((length = in.read(tmp)) != -1) {
					byteArrayOutputStream.write(tmp, 0, length);
					linkedList.add(byteArrayOutputStream.toByteArray());
					byteArrayOutputStream.reset();
				}
				in.close();
				byteArrayOutputStream.close();
				path.delete();
				LogedUser.sendMessage(new Message(MessageService.DFILE, "start",
						new String[] { message.getMeg()[0], linkedList.size() + "" }, message.getFrom(),
						message.getTo(), ""), session);
				for (int i = 0; i < linkedList.size(); i++) {
					LogedUser.sendMessage(
							new Message(MessageService.DFILE, i + "", message.getMeg()[0], linkedList.get(i)), session);
				}
				LogedUser.sendMessage(new Message(MessageService.DFILE, "end",
						new String[] { message.getMeg()[0], linkedList.size() + "" }, message.getFrom(),
						message.getTo(), ""), session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * 注册信息
	 * 
	 * @param message
	 * @param session
	 */
	public synchronized void logon(Message message, IoSession session) {
		UserService personService = UserService.getInstance();
		if (personService.addUser(new User(message.getMeg()[0], message.getMeg()[1], message.getMeg()[2]))) {
			LogedUser.sendMessage(new Message(MessageService.RLOGON, message.getFrom(), true), session);
		} else {
			LogedUser.sendMessage(new Message(MessageService.RLOGON, message.getFrom(), false), session);
		}
	}

	/**
	 * 验证id是否可用
	 * 
	 * @param id
	 * @param session
	 */
	public synchronized void varifaction(String id, IoSession session) {
		UserService personService = UserService.getInstance();
		if (personService.selectUser(id) == null) {
			LogedUser.sendMessage(new Message(MessageService.RBOL, id, true), session);
		} else {
			LogedUser.sendMessage(new Message(MessageService.RBOL, id, false), session);
		}
	}

	/**
	 * 好友搜索
	 * 
	 * @param message
	 * @param session
	 */
	public synchronized void searchfriend(Message message, IoSession session) {
		UserService userService = UserService.getInstance();
		ArrayList<User> results = userService.searchUser(message.getMeg()[0]);
		if (results.size() > 0) {
			for (User user : results) {
				LogedUser.sendMessage(new Message(MessageService.RSEARCH_FRIEND, "",
						new String[] { user.getUserId(), user.getName() }, message.getFrom(), message.getFrom(), ""),
						session);
			}
		} else {
			LogedUser.sendMessage(
					new Message(MessageService.RSEARCH_FRIEND, "", null, message.getFrom(), message.getFrom(), ""),
					session);
		}
	}

	/**
	 * 好友添加请求
	 * 
	 * @param message
	 */
	public synchronized void addFriend(Message message) {
		LogedUser.getInstance().sendMessage(message);
	}

	public synchronized void resultAddFriend(Message message) {
		String fromId = message.getFrom();
		String toId = message.getTo();
		if (message.getMeg() != null) {
			UserService userService = UserService.getInstance();
			User from = userService.selectUser(fromId);
			User to = userService.selectUser(toId);
			Message m1 = new Message(MessageService.RADD_FRIEND, "",
					new String[] { MyJson.changeCmd(from.getUserId(), from.getUserId(), "t").toString() }, fromId, toId,
					"");
			LogedUser.getInstance().sendMessage(m1);
			RelationService.getInstance().addRelation(new Relation(fromId, toId));
			Message m2 = new Message(MessageService.RADD_FRIEND, "",
					new String[] { MyJson.changeCmd(to.getUserId(), to.getUserId(), "t").toString() }, toId, fromId,
					"");
			LogedUser.getInstance().sendMessage(m2);
		} else {
			Message m = new Message(MessageService.RADD_FRIEND, "",
					new String[] { MyJson.changeCmd("", "", "f").toString() }, toId, fromId, "");
			LogedUser.getInstance().sendMessage(m);
		}
	}

	/**
	 * 注销信息
	 * 
	 * @param id
	 */
	public synchronized void logout(String id) {
		LogedUser.getInstance().delUser(id);
	}
}
