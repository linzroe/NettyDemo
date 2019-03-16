package netty.demo.handler;


import io.netty.channel.ChannelHandlerContext;


import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import netty.demo.cache.NettyCache;
import netty.demo.client.NettyClient;
import netty.demo.enums.RequestType;
import netty.demo.pojo.NettyCommandPo;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import com.google.gson.Gson;



public class NettyClientHandler extends SimpleChannelInboundHandler<NettyCommandPo>{
	

	Gson gson=new Gson();
 
    
	private NettyClient client;
    public NettyClientHandler(NettyClient Client) {
        this.client = Client;
    }

    
   
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("[Connect To The Server] [Id:"+ctx.channel().id().asLongText()+"]");
        NettyCache.getChannels().put("client",ctx);
        NettyCommandPo po=new NettyCommandPo();
     	po.setCommandId(UUID.randomUUID().toString());
     	po.setCommandType(RequestType.REGISTER.toString());
     	po.setSendTime(new Date());
     	po.setNeedsReturn(true);
     	po.setRequestType(RequestType.REQUEST.toString());
		po.setReturnCode(0);
     	po.setParameter(null);
     	ctx.writeAndFlush(po);
        
        
    }

    
    
    @Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        NettyCache.getChannels().put("client",ctx);
        
    }

	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

//    断线重连的关键一点是检测连接是否已经断开.因此我们重写了channelInactive方法.当 TCP连接断开时,
//    会回调 channelInactive方法,因此我们在这个方法中调用 client.doConnect()来进行重连.
    @Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		client.doConnect();
		System.out.println("断线重连...");
	}

    @Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	NettyCache.getChannels().remove("client");
        final EventLoop loop = ctx.channel().eventLoop(); 
        loop.schedule(new Runnable() { 
            @Override 
            public void run() { 
            	client.doConnect(); 
            } 
        }, 1, TimeUnit.SECONDS);
	}

	/**  读写空闲时间超过指定时间会触发这个方法，用于心跳检测      **/
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {  //判断evt事件是不是IdleStateEvent事件;
			IdleStateEvent event = (IdleStateEvent) evt; 
			if (event.state() == IdleState.READER_IDLE){  //继续判断是读空闲事件还是写空闲事件还是读写空闲事件,根据不同事件类型发起相应的操作
				System.out.println("[Client read idle]");
			} else if (event.state() == IdleState.WRITER_IDLE){   
				System.out.println("[Client write idle]");
		    }else if (event.state() == IdleState.ALL_IDLE){
		    	System.out.println("[All idle]");
		    }
			
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NettyCommandPo msg) throws Exception {
		System.out.println("[received] [Server] [message] - ["+gson.toJson(msg)+"]");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

