package servlet;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import model.Message;
import model.MessageService;

public class ServerHandle extends IoHandlerAdapter {
	// 会话创建
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		// 获取客户端ip
		InetSocketAddress socketAddress = (InetSocketAddress) session.getRemoteAddress();
		session.setAttribute("sad", 123);
		InetAddress inetAddress = socketAddress.getAddress();
		System.out.println("sessionCreated  id=" + session.getId() + " , ip=" + inetAddress.getHostAddress());
	}

	// public HashMap<Integer, byte[]> cache = new HashMap<>();
	//该方式仅支持一个用户上传一个文件
	public HashMap<String, HashMap<Integer, byte[]>> cache = new HashMap<>();
	// 文件缓冲区，通过用户上传的fileId设立第一个key，第二个通过数据包中的序号控制来控制数据流的文件位置

	// 接受消息
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println(((Message) message).toString());
		MessageService.excuateFromClient((Message) message, session, this);
	}

	// 会话打开
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		System.out.println("sessionOpened");
	}

	// 会话关闭
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		LogedUser.getInstance().delUser(session.getAttribute("id").toString());
		System.out.println("sessionClosed");
	}

	// 会话等待
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		super.sessionIdle(session, status);
	}

	// 会话异常
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		LogedUser.getInstance().delUser(session.getAttribute("id").toString());
		cause.printStackTrace();
	}

	// 发送消息
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	// 关闭输入流
	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
		System.out.println("inputClosed");
	}
}