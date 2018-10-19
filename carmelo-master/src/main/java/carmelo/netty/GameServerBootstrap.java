package carmelo.netty;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import carmelo.common.Configuration;
import carmelo.common.SpringContext;
import carmelo.examples.server.sync.FutureManager;
import carmelo.examples.server.sync.RequestId;
import carmelo.examples.server.sync.SyncFuture;
import carmelo.json.MessageType;
import carmelo.json.ResponseDto;
import carmelo.json.ResponseType;
import carmelo.netty.tcp.TcpServerInitializer;
import carmelo.servlet.OutputMessage;
import carmelo.servlet.Request;
import carmelo.servlet.Response;
import carmelo.servlet.Servlet;
import carmelo.session.SessionManager;
import carmelo.session.Users;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * game server bootstrap
 * 
 * @author needmorecode
 *
 */
public class GameServerBootstrap {

	public void run() {

		// init servlet
		final Servlet servlet = new Servlet();
		servlet.init();

		// camelo supports both tcp and http
		
		// http channel
//		new Thread() {
//			public void run() {
//				EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
//				EventLoopGroup workerGroup2 = new NioEventLoopGroup();
//				try {
//					ServerBootstrap b2 = new ServerBootstrap();
//					b2.group(bossGroup2, workerGroup2)
//							.channel(NioServerSocketChannel.class)
//							.childHandler(new HttpServerInitializer(servlet));
//					int port = Integer.parseInt(Configuration.getProperty(Configuration.HTTP_PORT));
//					b2.bind(port).sync().channel().closeFuture().sync();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} finally {
//					bossGroup2.shutdownGracefully();
//					workerGroup2.shutdownGracefully();
//				}
//			}
//		}.start();
		
/*		// http push channel
		new Thread() {
			public void run() {
				EventLoopGroup bossGroup2 = new NioEventLoopGroup(1);
				EventLoopGroup workerGroup2 = new NioEventLoopGroup();
				try {
					ServerBootstrap b2 = new ServerBootstrap();
					b2.group(bossGroup2, workerGroup2)
							.channel(NioServerSocketChannel.class)
							.childHandler(new HttpServerInitializer(servlet));
					int port = Integer.parseInt(Configuration.getProperty(Configuration.HTTP_PUSH_PORT));
					b2.bind(port).sync().channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bossGroup2.shutdownGracefully();
					workerGroup2.shutdownGracefully();
				}
			}
		}.start();*/
		
		

		// tcp channel
		final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		final EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b1 = new ServerBootstrap();
			b1.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new TcpServerInitializer(servlet));
			int port = Integer.parseInt(Configuration.getProperty(Configuration.TCP_PORT));
			b1.bind(port).sync().channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

}
