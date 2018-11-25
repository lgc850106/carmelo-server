package carmelo.examples.server.uterus;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import carmelo.common.SpringContext;
import carmelo.examples.server.device.domain.UserBitPort;
import carmelo.examples.server.device.domain.UserComposite;
import carmelo.examples.server.sync.FutureManager;
import carmelo.examples.server.sync.RequestId;
import carmelo.examples.server.sync.SyncFuture;
import carmelo.json.MessageType;
import carmelo.json.ResponseDto;
import carmelo.json.ResponseType;
import carmelo.servlet.OutputMessage;
import carmelo.servlet.Request;
import carmelo.servlet.Response;
import carmelo.session.Session;
import carmelo.session.SessionManager;
import carmelo.session.Users;
import io.netty.channel.Channel;


//Uterus子宫
//在这里，服务器向已经连接的客户端发出指令
@Component
public class Uterus {
	
	
	/*
	 * stroll漫步，向客户端随机发送上、右、下、左控制信息，控制小球随机漫步
	 */
	@SuppressWarnings("unchecked")
	@Async
	public void stroll(int userId) throws InterruptedException, ExecutionException, TimeoutException {
		String sessionId = Users.getSessionId(userId);
		//检查用户是否登录
		if(sessionId == null) return;
		Session session = SessionManager.getInstance().getSession(sessionId);
		Map<String, Object> params = session.getParams();
		
//		int rootId = (Integer)params.get("rootId");
		Map<Integer, UserBitPort> userBP = (Map<Integer, UserBitPort>) params.get("bitport");
//		Map<Integer, UserComposite> userCP = (Map<Integer, UserComposite>) params.get("composite");
		
		Object bpKeySet[] = userBP.keySet().toArray();
		int setSize = bpKeySet.length;
		System.out.println("userBP size:" + setSize);
		//每隔1秒，向客户端发出随机漫步指令
		while(true) {
			String sessionIdTemp = Users.getSessionId(userId);
			if(sessionIdTemp == null) break;//如果客户端已经下线，退出线程循环
			//如果用户重新登录，session变了，重新获取session
			if(!sessionIdTemp.equals(sessionId)) {
				sessionId = sessionIdTemp;
				params = session.getParams();
//				rootId = (Integer)params.get("rootId");
				userBP = (Map<Integer, UserBitPort>) params.get("bitport");
//				userCP = (Map<Integer, UserComposite>) params.get("composite");
			}
			int key = (Integer)bpKeySet[Math.abs(new Random().nextInt()) % setSize];
			UserBitPort bp = userBP.get(key);
			int requestId = RequestId.get();
			String parameters = "id=" + bp.getId();
			Request request = new Request(requestId, "ballMove!stroll", parameters, "0", null);
			Channel channel = session.getChannel();
			//发送请求
			channel.write(new OutputMessage(MessageType.REQUEST, request));
			channel.flush();
			//这里不等待客户端返回结果
			Thread.sleep(500);
		}
		

	}

}
