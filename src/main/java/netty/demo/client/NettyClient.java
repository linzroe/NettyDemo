package netty.demo.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo.codec.RpcDecoder;
import netty.demo.codec.RpcEncoder;
import netty.demo.handler.NettyClientHandler;
import netty.demo.pojo.NettyCommandPo;
import netty.demo.server.NettyServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class NettyClient implements Runnable{


	
	
    private static Bootstrap bootstrap = null;
    private static Channel channel;
    public NettyClient(){
    	
    }
     

	public void connect(String host, int port) throws Exception {
		NioEventLoopGroup workGroup = null ;
        try {
            //配置client启动参数
            workGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);  
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
                 protected void initChannel(SocketChannel ch){         
                     /*
                     * 禁止堆类加载器进行缓存,他在基于 OSGI 的动态模块化编程中经常使用,由于 OSGI 可以进行热部署和热升级,当某个
                     * bundle升级后,它对应的类加载器也将一起升级,因此在动态模块化的编程过程中,很少对类加载器进行缓存,因为他随时可能会发生变化.
                     */
                	 //传输对象
                     //ch.pipeline().addLast(new ObjectDecoder(1024 >> 2, ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                     //ch.pipeline().addLast(new ObjectEncoder());
					 ch.pipeline().addLast(new RpcEncoder(NettyCommandPo.class)); // 将 RPC 请求进行编码（为了发送请求）
	        		 ch.pipeline().addLast(new RpcDecoder(NettyCommandPo.class)); // 将 RPC 响应进行解码（为了处理响应）
                     ch.pipeline().addLast(new IdleStateHandler(0,5,0,TimeUnit.SECONDS));
                     //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048,0,4,-4,0));  //RpcDecoder中已做了处理
                     ch.pipeline().addLast(new NettyClientHandler(NettyClient.this));
                 }
            })
            .option(ChannelOption.SO_KEEPALIVE, true);
            doConnect();
        } catch (Exception e){
        	 
        	System.out.println("[Disconnect From Server] - [Wait For Reconnect Again]");
        } finally {
        	System.out.println("finally");
//            workGroup.shutdownGracefully();
        }
    }
	

	//负责客户端和服务器的 TCP 连接的建立,并且当 TCP连接失败时, doConnect会通过 "channel().eventLoop().schedule" 来延时10s后尝试重新连接.
	public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        try {
        	
        	ChannelFuture channelFuture = bootstrap.connect(IP,PORT).sync();
        	channelFuture.channel().closeFuture().sync();
            //断线重连机制
        	channelFuture.addListener(new ChannelFutureListener() {
            	@Override
                public void operationComplete(ChannelFuture futureListener){
                    if (futureListener.isSuccess()) {
                    	channel = futureListener.channel();
                    	System.out.println("Connect to server successfully!");
                    
                    } else {
                    	System.out.println("Failed to connect to server, try connect after 10s");
                        futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                            }
                        },5,TimeUnit.SECONDS);
                    }
                 }
             });
        } catch (Exception e){
        	e.printStackTrace();
        	System.out.println("[Lost] [Connection] - [ready to connect again]");
        }
	}
	
	@Override
	public void run() {
		try {
			connect(IP,PORT);
		} catch (Exception e) {
		}
	}
	
	private final static String IP="127.0.0.1";
	private final static int PORT=2333;
	
	public static void main(String[] args) {
		try {
			Thread Server=new Thread(new NettyServer());
			Server.start();
			Thread Client = new Thread(new NettyClient());
			Client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}