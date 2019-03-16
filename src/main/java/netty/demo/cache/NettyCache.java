package netty.demo.cache;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import io.netty.channel.ChannelHandlerContext;
import netty.demo.client.NettyClient;
import netty.demo.pojo.NettyCommandPo;

public class NettyCache {
	
	private static ConcurrentMap<String,ChannelHandlerContext> channels = new ConcurrentHashMap<>();
	private static NettyClient client = new NettyClient();
	private static Map<String,NettyCommandPo> CommandCacheMap=new HashMap<>();

	public static ConcurrentMap<String, ChannelHandlerContext> getChannels() {
		return channels;
	}

	public static void setChannels(ConcurrentMap<String, ChannelHandlerContext> channels) {
		NettyCache.channels = channels;
	}

	public static NettyClient getClient() {
		return client;
	}

	public static Map<String, NettyCommandPo> getCommandCacheMap() {
		return CommandCacheMap;
	}

	public static void setCommandCacheMap(Map<String, NettyCommandPo> commandCacheMap) {
		CommandCacheMap = commandCacheMap;
	}

	 
}

