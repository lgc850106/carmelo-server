package carmelo.examples.server.timertask;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import carmelo.common.SpringContext;
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

//使用spring 的异步注解实现定时任务
@Component
public class TimerTask {

//	@Scheduled(cron = "0/5 * * * * ?")  //每5秒执行一次
	public void task() throws InterruptedException, ExecutionException, TimeoutException {
		System.out.println("5秒一次向已经登录的客户端测试sayHello方法");

		SessionManager sessionManager = SessionManager.getInstance();
		Set<Integer> users = Users.getUsers();
		for(Integer userId : users) {
			Channel channel = sessionManager.getSession(Users.getSessionId(userId)).getChannel();
			
			System.out.println("向用户" + userId + "测试sayHello方法");
			int requestId = RequestId.get();
			String params = "name=server";
			Request request = new Request(requestId, "test!sayHello", params, "0", null);
			//创建并添加future
			FutureManager fm = (FutureManager)SpringContext.getBean(FutureManager.class);
			SyncFuture<Response> future = fm.createFuture(channel, requestId);
			//发送请求
			channel.write(new OutputMessage(MessageType.REQUEST, request));
			channel.flush();
			//同步等待返回结果
			Response response = future.get(3000, TimeUnit.MILLISECONDS);
			//返回格式
			if(response == null) {
				System.out.println("等待客户端响应超时！");
				continue;
			}
			//服务器返回的内容
			byte[] contents = response.getContents();
			System.out.println("返回内容：" + new String(contents));
			ResponseDto responseDto = new ResponseDto(new String(contents));
			int responseType = responseDto.getResponseType();
			//		System.out.println("login返回类型:" + responseType + " 失败:" + ResponseType.FAIL.getType() + " 成功:" + ResponseType.SUCCESS.getType());
			if(responseType == ResponseType.FAIL.getType()) {
				String content = (String)responseDto.getData();
				System.out.println("返回失败结果，内容: " + content);
				continue;
			}else if(responseType == ResponseType.SUCCESS.getType()) {
				JSONObject jsonObject = JSON.parseObject(responseDto.getData().toString());
				String result = jsonObject.getString("result");
				System.out.println("返回成功结果，内容: " + result);
				continue;
			}else {
				System.out.println("未知错误，登录失败,请联系开发人员");
				continue;
			}
		}
		
	}

}


//		CRON表达式    含义 
//		
//		"0 0 12 * * ?"    每天中午十二点触发 
//		
//		"0 15 10 ? * *"    每天早上10：15触发 
//		
//		"0 15 10 * * ?"    每天早上10：15触发 
//		
//		"0 15 10 * * ? *"    每天早上10：15触发 
//		
//		"0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
//		
//		"0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
//		
//		"0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
//		
//		"0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
//		
//		"0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
//		
//		"0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
//		
//		"0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发 