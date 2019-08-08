package servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import model.Message;
import model.MessageService;

public class LogedUser {
	private HashMap<String, IoSession> allUser = new HashMap<>();
	private static LogedUser logedUser = new LogedUser();

	private LogedUser() {
	}

	public static LogedUser getInstance() {
		return logedUser;
	}

	public synchronized boolean putUser(String userId, IoSession session) {
		if (allUser.get(userId) != null) {
			return false;
		} else {
			allUser.put(userId, session);
			return true;
		}
	}

	public synchronized boolean hasUser(String userId) {
		return allUser.get(userId) != null;
	}

	public synchronized boolean delUser(String userId) {
		return allUser.remove(userId) != null;
	}

	public synchronized boolean sendMessage(Message message) {
		IoSession session = allUser.get(message.getTo());
		message.setData(utils.DateUtil.getNowTime());
		if (session == null) {
			MessageService.getInstance().putMessage(message);
			return false;
		} else {
			session.write(message);
			return true;
		}
	}

	public synchronized static void sendMessage(Message message, IoSession session) {
		session.write(message);
	}

	public synchronized List<String> sendMessage(List<String> userIds, Message meg) {
		List<String> unsend = new ArrayList<String>();
		for (int i = 0; i < userIds.size(); i++) {
			if (!sendMessage(meg)) {
				unsend.add(userIds.get(i));
			}
		}
		return unsend;
	}

	public boolean sendImg(Message message,ServerHandle sh) {
		IoSession session = allUser.get(message.getTo());
		message.setData(utils.DateUtil.getNowTime());
		if (session == null) {//接受方离线则将数据流保存至本地
			MessageService.getInstance().putMessage(message);
			try {
				String fileId = message.getMeg()[0];
				File file = new File("serverletFile/" + fileId);
				file.createNewFile();
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				out.write(message.getBt());
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		} else {
			session.write(message);
			return true;
		}
	}
}
