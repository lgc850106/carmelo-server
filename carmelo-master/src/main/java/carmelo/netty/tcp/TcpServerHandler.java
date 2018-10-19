package carmelo.netty.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

import carmelo.common.SpringContext;
import carmelo.examples.server.sync.FutureManager;
import carmelo.examples.server.sync.SyncFuture;
import carmelo.json.MessageType;
import carmelo.servlet.OutputMessage;
import carmelo.servlet.Request;
import carmelo.servlet.Response;
import carmelo.servlet.Servlet;

public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {
	
	private Servlet servlet;
	
	public TcpServerHandler(Servlet servlet){
		this.servlet = servlet;
	}

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
    	
    	if(obj instanceof Request) {
    		
    		Response response = servlet.service((Request)obj);
    		if(response != null) {//有反馈内容
    			ctx.writeAndFlush(new OutputMessage(MessageType.RESPONSE, response));
    		}
    		
    	}else if( obj instanceof Response) {
    		//从FutureManager里找到对应的future并设置结果
			Response response = (Response)obj;
			//如果是futureMap中同步等待的response,则把response传递给future,由相应的业务类处理
			int requestId = response.getId();
			FutureManager fm = (FutureManager)SpringContext.getBean(FutureManager.class);
			if(fm.containsFuture(ctx.channel(), requestId)) {
				SyncFuture<Response> future = fm.getFuture(ctx.channel(), requestId);
				future.setResponse(response);//这里会通知发出Request的业务类读取数据并处理
				return;
			}
			return;
    	}    	
    }


}
