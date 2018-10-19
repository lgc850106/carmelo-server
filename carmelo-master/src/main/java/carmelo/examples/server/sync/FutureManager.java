package carmelo.examples.server.sync;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import carmelo.servlet.Response;
import io.netty.channel.Channel;

/*
 * 自定义工具类，采用单例模式 ,配合SyncFuture实现请求-响应同步机制
 * 在服务端需要用channel和requestId两个参数来找到对应的future,这里的Map是二维的
 */
@Component
public class FutureManager {

	//futureMap用来管理当前所有的同步业务
	//	private Map<Integer, SyncFuture<Response>> futureMap = new ConcurrentHashMap<Integer, SyncFuture<Response>>();

	private Map<Channel, Map<Integer, SyncFuture<Response>>> futureMap 
	= new ConcurrentHashMap<Channel, Map<Integer, SyncFuture<Response>>>();
	//在每个客户端连接的时候要建立相应的二级Map,断开时清理相应的二级Map
	//在发出Request时，在对应的二级Map中添加相应的future;


	public SyncFuture<Response> getFuture(Channel channel, Integer requestId){
		return futureMap.get(channel).get(requestId);
	}

	
	//创建一个future,并添加到map中
	public SyncFuture<Response> createFuture(Channel channel,int requestId){
		SyncFuture<Response> future = new SyncFuture<Response>(requestId);
		addFuture(channel, future);
		return future;
	}

	public void addFuture(Channel channel, SyncFuture<Response> future ) {
		futureMap.get(channel).put(future.getRequestId(), future);
	}

	public boolean containsFuture(Channel channel, int requestId) {
		if(futureMap.get(channel).containsKey(requestId)) {
			return true;
		}
		return false;
	}
	
	public void removeFutureMap(Channel channel) {
		futureMap.remove(channel);
		return;
	}
	
	public void addFutureMap(Channel channel) {
		futureMap.put(channel, new ConcurrentHashMap<Integer, SyncFuture<Response>>());
	}

	@Scheduled(cron = "0/5 * * * * ?")  //每5秒执行一次,清理已经超时完成的future
	public void cleanFutureMap() {
		//		System.out.println("Server future Manager task");
		for (Channel channel : futureMap.keySet()) {

			//检查客户端是否下线
			if(!channel.isActive()) {
				futureMap.remove(channel);
			}else {
				//检查是否超时或已完成
				Map<Integer, SyncFuture<Response>> channelMap = futureMap.get(channel);
				for(SyncFuture<Response> future : channelMap.values()) {
					try {
						if (future.isTimeout() || future.isDone())
						{
							channelMap.remove(future.getRequestId());
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}
