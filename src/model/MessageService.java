package model;

import java.util.List;

import org.apache.mina.core.session.IoSession;

import client.ClientHandle;
import other.GenneralBaseDao;
import servlet.LogedUser;
import servlet.ServerHandle;
import utils.ClientExqute;
import utils.ServletExqute;

public class MessageService {
	public final static short LOGIN = 0; // 登陆命令
	public final static short LOGON = 1; // 注册命令
	public final static short MES = 2; // 消息发送
	public final static short HISTORY = 3; // 历史消息获取
	public final static short ADD_FRIEND = 4; // 好友添加请求
	public final static short DELF_FRIEND = 5; // 好友删除请求
	public final static short SEARCh_FRIEND = 6;// 好友搜索
	public final static short BUILD_GROUP = 7; // 群创建
	public final static short FILE = 8;// 文件传送
	public final static short VARFICATION = 9;// ID测试
	public final static short LOGOUT = 10; // 注销命令
	public final static short RADD_FRIEND = 11;// 好友请求信息反馈
	public final static short FRIENDINFO = 12;// 好友信息
	public final static short DFILE = 13;//文件下载请求
	public final static short IMG = 14;//图片上传请求
	public final static short DIMG = 15;//图片下载请求
	public final static short RLOGIN = 20; // 登陆结果返回
	public final static short RLOGON = 21;// 注册结果返回
	public final static short RHISTORY = 22; // 历史消息返回
	public final static short RBOL = 23;// 返回true|false
	public final static short ADD = 24; // 群成员添加
	public final static short DEL = 25; // 群成员删除
	public final static short UPDATE = 26; // 群成员更新
	public final static short RSEARCH_FRIEND = 27;// 搜索结果返回


	private GenneralBaseDao<Message> bd = new GenneralBaseDao<Message>();
	private static MessageService messageService = new MessageService();

	private MessageService() {
	}

	public static MessageService getInstance() {
		return messageService;
	}

	public static void excuateFromClient(Message input, IoSession session, ServerHandle sh) throws Exception {
		switch (input.getCmd()) {
		case LOGIN:
			ServletExqute.getInstance().login(input.getFrom(), input.getMeg()[0], session);
			break;
		case LOGON:
			ServletExqute.getInstance().logon(input, session);
			break;
		case MES:
			LogedUser.getInstance().sendMessage(input);
			break;
		case HISTORY:
			ServletExqute.getInstance().history(input.getFrom(), input.getTo(), session);
			break;
		case FILE:
			ServletExqute.getInstance().file(input, sh);
			break;
		case VARFICATION:
			ServletExqute.getInstance().varifaction(input.getFrom(), session);
			break;
		case SEARCh_FRIEND:
			ServletExqute.getInstance().searchfriend(input, session);
			break;
		case LOGOUT:
			ServletExqute.getInstance().logout(input.getFrom());
			break;
		case ADD_FRIEND:
			ServletExqute.getInstance().addFriend(input);
			break;
		case RADD_FRIEND:
			ServletExqute.getInstance().resultAddFriend(input);
			break;
		case DFILE:
			ServletExqute.getInstance().dFile(input, session);
			break;
		case IMG:
			LogedUser.getInstance().sendImg(input, sh);break;
		}
	}

	public static String excuateFromService(Message input, ClientHandle ch) throws Exception {
		switch (input.getCmd()) {
		case RLOGIN:
			ClientExqute.login(input, ch);
			break;
		case MES:
			ClientExqute.recive(input, ch);
			break;
		case RHISTORY:
			ClientExqute.recive(input, ch);
			break;
		case RLOGON:
			ClientExqute.logon(input.getFlag(), ch);
			break;
		case RBOL:
			ClientExqute.varifacation(input.getFlag(), ch);
			break;
		case RSEARCH_FRIEND:
			ClientExqute.searhFriend(input, ch);
			break;
		case ADD_FRIEND:
			ClientExqute.addFriend(input, ch);
			break;
		case RADD_FRIEND:
			ClientExqute.resultAddFriend(input, ch);
			break;
		case FRIENDINFO:
			ClientExqute.friendinfo(input, ch);
			break;
		case DFILE:
			ClientExqute.dFile(input, ch);
			break;
		case FILE:
			ClientExqute.recive(input, ch);
			break;
		case IMG:
			ClientExqute.recive(input, ch);
			break;
		}
		return "";
	}

	public List<Message> getNewFriendMessage(String to) {
		List<Message> messages = bd.queryObj(new Message(),
				"select * from message where mTo = '" + to + "' and Cmd in ('" + ADD_FRIEND + "','"+RADD_FRIEND  + "')");
		if (messages != null) {
			CMService cms = CMService.getInstance();
			messages.forEach(m -> m.setMeg(new String[] { cms.getCM(m.getMessageId()).getMeg() }));
		}
		return messages;
	}

	public List<Message> getMessage(String from, String to) {
		List<Message> messages = bd.queryObj(new Message(),
				"select * from message where mTo = '" + to + "' and mFrom = '" + from + "' and Cmd in ('" + IMG + "','" + MES + "','" +FILE +"')");
		if (messages != null) {
			CMService cms = CMService.getInstance();
			messages.forEach(m ->m.setMeg(new String[] { cms.getCM(m.getMessageId()).getMeg() }));
		}
		return messages;
	}

	public boolean putMessage(Message message) {
		message.setMessageId(System.nanoTime() + "");
		if (bd.addObj(message)) {
			if (CMService.getInstance().putCM(message.getCM())) {
				return true;
			} else {
				bd.deleteObj(message);
			}
		}
		return false;
	}

	public boolean removeMessage(Message message) {
		if (CMService.getInstance().delCM(message.getMessageId())) {
			if (bd.deleteObj(message)) {
				return true;
			}
		}
		return false;
	}
}
