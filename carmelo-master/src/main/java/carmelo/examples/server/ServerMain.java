package carmelo.examples.server;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carmelo.common.SpringContext;
import carmelo.netty.GameServerBootstrap;

//import carmelo.netty.GameServerBootstrap;

public class ServerMain {

	private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
	
	public static void main(String args[]) throws IOException{
		
//		logger.info("日志测试");
		
		//主线程启动服务端后阻塞运行，将需要测试的代码放在单独线程执行
		new Thread() {
			public void run() {
//				//测试UserComposite数据库操作
				
				
				

//				UserService userService = (UserService)SpringContext.getBean(UserService.class);
//				logger.info("注册用户" + userService.register("2", "234"));
				
//				UserCompositeService ucService 
//					= (UserCompositeService)SpringContext.getBean(UserCompositeService.class);
//				logger.info("注册用户Composite " + ucService.apply(1));
//
//				UserBitPortService ubService 
//					= (UserBitPortService)SpringContext.getBean(UserBitPortService.class);
//				logger.info("注册用户BitPort " + ubService.apply(1));
				
				//测试自定义数据库操作类
//				DatabaseTransaction dt =  (DatabaseTransaction)SpringContext.getBean(DatabaseTransaction.class);
//				dt.getUser(1);
				
			}
		}.start();

		
		
		
		new GameServerBootstrap().run();
	}

}
