package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaClient implements Runnable {
	public MinaClient(LaunchClient main) {
		this.main = main;
	}

	private IoSession session = null;
	private ConnectFuture connFuture;
	private LaunchClient main;

	public IoSession getSession() {
		return session;
	}

	public void start() {
		NioSocketConnector connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		ObjectSerializationCodecFactory obj = new ObjectSerializationCodecFactory();
		obj.setDecoderMaxObjectSize(Integer.MAX_VALUE);
		obj.setEncoderMaxObjectSize(Integer.MAX_VALUE);
		chain.addLast("myChin", new ProtocolCodecFilter(obj));
		// 设置会话管理器
		ClientHandle clientHandle = new ClientHandle();
		clientHandle.setMain(main);
		connector.setHandler(clientHandle);

		// 添加编码过滤
		// connector.getFilterChain().addLast("codec",
		// new ProtocolCodecFilter(new
		// TextLineCodecFactory(Charset.forName("UTF-8")))); // 字符编码

		// 连接服务器
		connFuture = connector.connect(new InetSocketAddress("127.0.0.1", 9999));
		connFuture.awaitUninterruptibly();
		session = connFuture.getSession();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				String line = reader.readLine();
				if ("quit".equals(line)) {
					session.closeNow();
					break;
				}
				ByteBuffer byteBuff = encoder.encode(CharBuffer.wrap(line));
				System.out.println(byteBuff);
				this.getSession().write(byteBuff);
			}
			connFuture.cancel();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
}
