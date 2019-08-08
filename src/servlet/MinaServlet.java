package servlet;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServlet {
	public static void main(String[] args){
		new MinaServlet().start();
	}
	
    private void start() {  
    	SocketAcceptor acceptor = new NioSocketAcceptor();  
    	acceptor.getSessionConfig().setReadBufferSize(1024*1024*600);
    	 acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 3);
    	DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();  
    	ObjectSerializationCodecFactory obj = new ObjectSerializationCodecFactory();
    	obj.setDecoderMaxObjectSize(Integer.MAX_VALUE);
    	obj.setEncoderMaxObjectSize(Integer.MAX_VALUE);
        chain.addLast("myChin", new ProtocolCodecFilter(  
                obj));  
    	acceptor.setHandler(new ServerHandle());
        try {  
            acceptor.bind(new InetSocketAddress("127.0.0.1",9999));  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
}
