package netty.demo.server;

import java.util.concurrent.TimeUnit;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import netty.demo.client.NettyClient;
import netty.demo.codec.RpcDecoder;
import netty.demo.codec.RpcEncoder;
import netty.demo.handler.NettyServerHandler;
import netty.demo.pojo.NettyCommandPo;

 


public class NettyServer implements Runnable{
	
	private static ChannelFuture channelFuture;
 
	public NettyServer(){}
	
 
	
	public void start(int port) throws Exception {
        NioEventLoopGroup workGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(4);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
        	@Override
            public void initChannel(SocketChannel ch) throws Exception {
                //ch.pipeline().addLast(new ObjectEncoder());
                //使用 weakCachingConcurrentResolver 创建线程安全的 WeakReferenceMap 对类加载器进行缓存.
                //它支持多线程并发访问,当虚拟机内存不足时,会释放缓存中的内存,防止内存泄露,
                //为了防止异常码流和解码错位导致的内存溢出,这里将单个对象序列化之后的字节数组长度设置为1M
                //ObjectDecoder objectDecoder = new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader()));
                //ch.pipeline().addLast(objectDecoder);
        		ch.pipeline().addLast(new RpcDecoder(NettyCommandPo.class)); // 将 RPC 响应进行解码（为了处理响应）
        		ch.pipeline().addLast(new RpcEncoder(NettyCommandPo.class)); // 将 RPC 请求进行编码（为了发送请求）
                //一般是客户端负责发送心跳的PING消息,因此客户端注意关注 ALL_IDLE事件,在这个事件触发后,客户端需要向服务器发送 PING消息,告诉服务器"我还存活着".
                //服务器是接收客户端的 PING消息的,因此服务器关注的是 READER_IDLE事件,并且服务器的 READER_IDLE间隔需要比客户端的 ALL_IDLE事件间隔大(例如客户端ALL_IDLE 是5s 没有读写时触发, 因此服务器的 READER_IDLE 可以设置为10s)
                //当服务器收到客户端的 PING 消息时, 会发送一个 PONG 消息作为回复. 一个 PING-PONG 消息对就是一个心跳交互
                /** IdleStateHandler 参数 1:读操作空闲秒数  2:写操作空闲秒数  3:读写全部空闲秒数  **/
                ch.pipeline().addLast(new IdleStateHandler(10, 0, 0,TimeUnit.SECONDS)); //心跳机制handler
                //第一个参数为信息最大长度，超过这个长度回报异常，第二参数为长度属性的起始（偏移）位，我们的协议中长度是0到第3个字节，
                //所以这里写0，第三个参数为“长度属性”的长度，我们是4个字节，所以写4，第四个参数为长度调节值，在总长被定义为包含包头长度时，
                //修正信息长度，第五个参数为跳过的字节数，根据需要我们跳过前4个字节，以便接收端直接接受到不含“长度属性”的内容
                //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048,0,4,-4,0)); //解决粘包和拆包的问题，RpcDecoder中已做了处理
                ch.pipeline().addLast(new NettyServerHandler());      //业务处理handler
            }
        })
        // 配置 NioServerSocketChannel的tcp参数,BACKLOG 的大小
        .option(ChannelOption.SO_BACKLOG, 1024)
        //一个端口释放后会等待两分钟之后才能再被使用，SO_REUSEADDR是让端口释放后立即就可以被再次使用。（linux内核也要做相应配置）
//        .childOption(ChannelOption.SO_REUSEADDR, true)
//        .childOption(ChannelOption.SO_RCVBUF, 10 * 1024)  
//        .childOption(ChannelOption.SO_SNDBUF, 10 * 1024) 
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.TCP_NODELAY, true);
        //此选项允许完全重复捆绑，但仅在想捆绑相同IP地址和端口的套接口都指定了此套接口选项才行(单播和组播)。
        //.childOption(EpollChannelOption.SO_REUSEPORT,true);
        // 绑定端口,随后调用它的同步阻塞方法 sync 等等绑定操作成功,完成之后 Netty 会返回一个 ChannelFuture
        // 它的功能类似于的 Future,主要用于异步操作的通知回调.
        try {
        	System.out.println("[TcpServer] - [Init Finished] ");
        	// 等待服务端监听端口关闭,调用 sync 方法进行阻塞,等待服务端链路关闭之后 main 函数才退出.
            channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
            
           System.out.println("port:"+port);
           System.out.println("TcpServer Run....");
            
            
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
 
	 public static void main(String[] args) {
		Thread Server=new Thread(new NettyServer());
		Server.start();
		Thread Client = new Thread(new NettyClient());
		Client.start();
	}
	
	@Override
	public void run() {
		try {
			start(2333);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}