package client;

import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import model.DownControl;
import model.Message;
import model.MessageService;

public class ClientHandle extends IoHandlerAdapter{
	LaunchClient main;
//	public HashMap<Integer, byte[]> cache = new HashMap<>();
//	public  double length = 0;
//	public  double now = 0;
//	public String from = "";
//	public boolean all = false;
//	public File file;
	
	public HashMap<String, HashMap<Integer, byte[]>> cache = new HashMap<>();
	public HashMap<String, DownControl> controlMeg = new HashMap<>();
	
	// 当客户端连接进入时
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("incomming 客户端: " + session.getRemoteAddress());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("客户端发送信息异常....");
	}

	// 当客户端发送消息到达时
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageService.excuateFromService((Message) message,this);
		System.out.println(message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("客户端与服务端断开连接.....");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("已建立连接" + session.getRemoteAddress());
	}
	
	public void setMain(LaunchClient main){
		this.main = main;
	}
	
	public LaunchClient getMain(){
		return main;
	}
}
