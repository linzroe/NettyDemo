package netty.demo.handler;

import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import netty.demo.cache.NettyCache;
import netty.demo.enums.RequestType;
import netty.demo.pojo.NettyCommandPo;



@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<NettyCommandPo>{

	
	Gson gson=new Gson();
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
        System.out.println("exceptionCaught-------");
        System.out.println(ctx.toString());
        
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("channelReadComplete--------"+ctx.toString());
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
        System.out.println("channelInactive--------"+ctx.toString());

	}

	/**  读写空闲时间超过指定时间会触发这个方法，用于心跳检测      **/
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {  //判断evt事件是不是IdleStateEvent事件;
			IdleStateEvent event = (IdleStateEvent) evt; 
			if (event.state() == IdleState.READER_IDLE){  //继续判断是读空闲事件还是写空闲事件还是读写空闲事件,根据不同事件类型发起相应的操作
				System.out.println("[Server read idle]");
			} else if (event.state() == IdleState.WRITER_IDLE){   
				System.out.println("[Server write idle]");
		    }else if (event.state() == IdleState.ALL_IDLE){
		    	System.out.println("[All idle]");
		    }
		}
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyCommandPo msg) throws Exception {
		
		System.out.println("Server channelRead0:"+gson.toJson(msg));
		switch (RequestType.valueOf(msg.getCommandType())) {
		case REGISTER:
			  NettyCommandPo po=new NettyCommandPo();
		     	po.setCommandId(msg.getCommandId());
		     	po.setCommandType(RequestType.REGISTER.toString());
		     	po.setSendTime(new Date());
		     	po.setNeedsReturn(true);
		     	po.setRequestType(RequestType.RESPONSE.toString());
				po.setReturnCode(0);
		     	po.setParameter(null);
		     	ctx.writeAndFlush(po);
			break;
		default:
			//其他命令 可以丢到线程池或者队列里面去
			break;
		}
	}

	/** 连接建立时候触发 **/
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[One Client Connect To Server] [Id:"+ctx.channel().id().asLongText()+"] ");
		NettyCache.getChannels().put("server",ctx);
	}

 
	
	
	
	

}