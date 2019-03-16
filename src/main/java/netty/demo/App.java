package netty.demo;

import netty.demo.client.NettyClient;
import netty.demo.server.NettyServer;

public class App {

	
	public static void main(String[] args) {
		Thread Server=new Thread(new NettyServer());
		Server.start();
		Thread Client = new Thread(new NettyClient());
		Client.start();
	}
	
}
